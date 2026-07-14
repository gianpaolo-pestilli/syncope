/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.core.provisioning.java;

import java.text.ParseException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.syncope.common.lib.Attr;
import org.apache.syncope.common.lib.to.AnyObjectTO;
import org.apache.syncope.common.lib.to.AnyTO;
import org.apache.syncope.common.lib.to.GroupTO;
import org.apache.syncope.common.lib.to.GroupableRelatableTO;
import org.apache.syncope.common.lib.to.Item;
import org.apache.syncope.common.lib.to.Mapping;
import org.apache.syncope.common.lib.to.MembershipTO;
import org.apache.syncope.common.lib.to.OrgUnit;
import org.apache.syncope.common.lib.to.Provision;
import org.apache.syncope.common.lib.to.RealmTO;
import org.apache.syncope.common.lib.to.UserTO;
import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.syncope.common.lib.types.AttrSchemaType;
import org.apache.syncope.core.persistence.api.EncryptorManager;
import org.apache.syncope.core.persistence.api.dao.AnyObjectDAO;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.persistence.api.dao.ImplementationDAO;
import org.apache.syncope.core.persistence.api.dao.RealmSearchDAO;
import org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.Any;
import org.apache.syncope.core.persistence.api.entity.DerSchema;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.Groupable;
import org.apache.syncope.core.persistence.api.entity.Implementation;
import org.apache.syncope.core.persistence.api.entity.Membership;
import org.apache.syncope.core.persistence.api.entity.PlainAttr;
import org.apache.syncope.core.persistence.api.entity.PlainAttrValue;
import org.apache.syncope.core.persistence.api.entity.PlainSchema;
import org.apache.syncope.core.persistence.api.entity.Realm;
import org.apache.syncope.core.persistence.api.entity.Relatable;
import org.apache.syncope.core.persistence.api.entity.Relationship;
import org.apache.syncope.core.persistence.api.entity.RelationshipType;
import org.apache.syncope.core.persistence.api.entity.group.Group;
import org.apache.syncope.core.persistence.api.entity.user.Account;
import org.apache.syncope.core.persistence.api.entity.user.LinkedAccount;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.persistence.api.utils.FormatUtils;
import org.apache.syncope.core.provisioning.api.AccountGetter;
import org.apache.syncope.core.provisioning.api.DerAttrHandler;
import org.apache.syncope.core.provisioning.api.IntAttrName;
import org.apache.syncope.core.provisioning.api.IntAttrNameParser;
import org.apache.syncope.core.provisioning.api.MappingManager;
import org.apache.syncope.core.provisioning.api.PlainAttrGetter;
import org.apache.syncope.core.provisioning.api.data.ItemTransformer;
import org.apache.syncope.core.provisioning.api.jexl.JexlContextBuilder;
import org.apache.syncope.core.provisioning.api.jexl.JexlTools;
import org.apache.syncope.core.provisioning.java.utils.ConnObjectUtils;
import org.apache.syncope.core.provisioning.java.utils.MappingUtils;
import org.identityconnectors.framework.common.FrameworkUtil;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.identityconnectors.framework.common.objects.Uid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

//Class A C1

public class DefaultMappingManager implements MappingManager, ApplicationContextAware {

    protected static final Logger LOG = LoggerFactory.getLogger(DefaultMappingManager.class);

    private static final String PROCESSING_EXPRESSION = "Processing expression '{}'";
    private static final String EXPRESSION_FAILED = "Expression '{}' processing failed";
    private static final String INVALID_INT_ATTR = "Invalid intAttrName '{}' specified, ignoring";
    private static final String MUST_CHANGE_PWD = "mustChangePassword";

    private ApplicationContext ctx;

    @Override
    public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }

    private MappingManager getSelf() {
        return ctx.getBean(MappingManager.class);
    }

    protected static Optional<String> processPreparedAttr(
            final PreparedAttr preparedAttr,
            final Set<Attribute> attributes) {

        if (preparedAttr == null) {
            return Optional.empty();
        }

        String connObjectKey = preparedAttr.connObjectLink();

        if (preparedAttr.attribute() != null) {
            Optional.ofNullable(AttributeUtil.find(preparedAttr.attribute().getName(), attributes)).ifPresentOrElse(
                    alreadyAdded -> {
                        attributes.remove(alreadyAdded);

                        Set<Object> values = new HashSet<>();
                        if (!CollectionUtils.isEmpty(alreadyAdded.getValue())) {
                            values.addAll(alreadyAdded.getValue());
                        }
                        if (preparedAttr.attribute().getValue() != null) {
                            values.addAll(preparedAttr.attribute().getValue());
                        }

                        attributes.add(AttributeBuilder.build(preparedAttr.attribute().getName(), values));
                    },
                    () -> attributes.add(preparedAttr.attribute()));
        }

        return Optional.ofNullable(connObjectKey);
    }

    protected static Name getName(final String evalConnObjectLink, final String connObjectKey) {
        if (StringUtils.isBlank(evalConnObjectLink)) {
            LOG.debug("Add connObjectKey [{}] as {}", connObjectKey, Name.NAME);
            return new Name(connObjectKey);
        } else {
            LOG.debug("Add connObjectLink [{}] as {}", evalConnObjectLink, Name.NAME);
            LOG.debug("connObjectKey [{}] will be used as {}", connObjectKey, Uid.NAME);
            return new Name(evalConnObjectLink);
        }
    }

    protected static PlainAttrValue clonePlainAttrValue(final PlainAttrValue src) {
        PlainAttrValue dst = new PlainAttrValue();
        dst.setBinaryValue(src.getBinaryValue());
        dst.setBooleanValue(src.getBooleanValue());
        dst.setDateValue(src.getDateValue());
        dst.setDoubleValue(src.getDoubleValue());
        dst.setLongValue(src.getLongValue());
        dst.setStringValue(src.getStringValue());
        return dst;
    }

    protected final UserDAO userDAO;
    protected final AnyObjectDAO anyObjectDAO;
    protected final GroupDAO groupDAO;
    protected final RelationshipTypeDAO relationshipTypeDAO;
    protected final RealmSearchDAO realmSearchDAO;
    protected final ImplementationDAO implementationDAO;
    protected final DerAttrHandler derAttrHandler;
    protected final IntAttrNameParser intAttrNameParser;
    protected final EncryptorManager encryptorManager;
    protected final JexlTools jexlTools;

    public DefaultMappingManager(
            final UserDAO userDAO,
            final AnyObjectDAO anyObjectDAO,
            final GroupDAO groupDAO,
            final RelationshipTypeDAO relationshipTypeDAO,
            final RealmSearchDAO realmSearchDAO,
            final ImplementationDAO implementationDAO,
            final DerAttrHandler derAttrHandler,
            final IntAttrNameParser intAttrNameParser,
            final EncryptorManager encryptorManager,
            final JexlTools jexlTools) {

        this.userDAO = userDAO;
        this.anyObjectDAO = anyObjectDAO;
        this.groupDAO = groupDAO;
        this.relationshipTypeDAO = relationshipTypeDAO;
        this.realmSearchDAO = realmSearchDAO;
        this.implementationDAO = implementationDAO;
        this.derAttrHandler = derAttrHandler;
        this.intAttrNameParser = intAttrNameParser;
        this.encryptorManager = encryptorManager;
        this.jexlTools = jexlTools;
    }

    protected List<Implementation> getTransformers(final Item item) {
        return item.getTransformers().stream().
                map(implementationDAO::findById).
                flatMap(Optional::stream).
                collect(Collectors.toList());
    }

    protected Name evaluateNAME(final Any any, final Provision provision, final String connObjectKey) {
        if (StringUtils.isBlank(connObjectKey)) {
            LOG.debug("Missing connObjectKey for {}", any.getType().getKey());
        }

        String connObjectLink = Optional.ofNullable(provision.getMapping()).
                map(Mapping::getConnObjectLink).
                orElse(null);
        String evalConnObjectLink = null;
        if (StringUtils.isNotBlank(connObjectLink)) {
            JexlContext jexlContext = new JexlContextBuilder().
                    fields(any).
                    plainAttrs(any.getPlainAttrs()).
                    derAttrs(derAttrHandler.getValues(any)).
                    build();

            evalConnObjectLink = jexlTools.evaluateExpression(connObjectLink, jexlContext).toString();
        }

        return getName(evalConnObjectLink, connObjectKey);
    }

    protected Name evaluateNAME(final Realm realm, final OrgUnit orgUnit, final String connObjectKey) {
        if (StringUtils.isBlank(connObjectKey)) {
            LOG.debug("Missing connObjectKey for Realms");
        }

        String connObjectLink = orgUnit.getConnObjectLink();
        String evalConnObjectLink = null;
        if (StringUtils.isNotBlank(connObjectLink)) {
            JexlContext jexlContext = new JexlContextBuilder().
                    fields(realm).
                    plainAttrs(realm.getPlainAttrs()).
                    derAttrs(derAttrHandler.getValues(realm)).
                    build();

            evalConnObjectLink = jexlTools.evaluateExpression(connObjectLink, jexlContext).toString();
        }

        return getName(evalConnObjectLink, connObjectKey);
    }

    @Transactional(readOnly = true)
    @Override
    public PreparedAttrs prepareAttrsFromAny(
            final Any any,
            final String password,
            final boolean changePwd,
            final Boolean enable,
            final ExternalResource resource,
            final Provision provision) {

        LOG.debug("Preparing resource attributes for {} with provision {} for attributes {}",
                any, provision, any.getPlainAttrs());

        Set<Attribute> attributes = new HashSet<>();
        Mutable<String> connObjectKeyValue = new MutableObject<>();

        MappingUtils.getPropagationItems(provision.getMapping().getItems().stream()).forEach(item -> {
            LOG.debug(PROCESSING_EXPRESSION, item.getIntAttrName());

            try {
                processPreparedAttr(
                        prepareAttr(
                                resource,
                                provision,
                                item,
                                any,
                                password,
                                AccountGetter.DEFAULT,
                                AccountGetter.DEFAULT,
                                PlainAttrGetter.DEFAULT),
                        attributes).ifPresent(connObjectKeyValue::setValue);
            } catch (Exception e) {
                LOG.error(EXPRESSION_FAILED, item.getIntAttrName(), e);
            }
        });

        MappingUtils.getConnObjectKeyItem(provision).ifPresent(item -> {
            Attribute connObjectKeyAttr = AttributeUtil.find(item.getExtAttrName(), attributes);
            if (connObjectKeyAttr != null) {
                attributes.remove(connObjectKeyAttr);
                attributes.add(AttributeBuilder.build(item.getExtAttrName(), connObjectKeyValue.get()));
            }

            Name name = evaluateNAME(any, provision, connObjectKeyValue.get());
            attributes.add(name);

            Optional.ofNullable(connObjectKeyValue.get()).
                    filter(cokv -> connObjectKeyAttr == null && !cokv.equals(name.getNameValue())).
                    ifPresent(cokv -> attributes.add(AttributeBuilder.build(item.getExtAttrName(), cokv)));
        });

        Optional.ofNullable(enable).ifPresent(e -> attributes.add(AttributeBuilder.buildEnabled(e)));

        if (!changePwd) {
            Optional.ofNullable(AttributeUtil.find(OperationalAttributes.PASSWORD_NAME, attributes)).
                    ifPresent(attributes::remove);
        }

        return new PreparedAttrs(connObjectKeyValue.get(), attributes);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Attribute> prepareAttrsFromLinkedAccount(
            final User user,
            final LinkedAccount account,
            final String password,
            final boolean changePwd,
            final Provision provision) {

        LOG.debug("Preparing resource attributes for linked account {} of user {} with provision {} "
                        + "for user attributes {} with override {}",
                account, user, provision, user.getPlainAttrs(), account.getPlainAttrs());

        Set<Attribute> attributes = new HashSet<>();

        MappingUtils.getPropagationItems(provision.getMapping().getItems().stream()).forEach(item -> {
            LOG.debug(PROCESSING_EXPRESSION, item.getIntAttrName());

            try {
                processPreparedAttr(
                        prepareAttr(
                                account.getResource(),
                                provision,
                                item,
                                user,
                                password,
                                acct -> account.getUsername() == null ? AccountGetter.DEFAULT.apply(acct) : account,
                                acct -> account.getPassword() == null ? AccountGetter.DEFAULT.apply(acct) : account,
                                (attributable, schema) -> {
                                    PlainAttr result = null;
                                    if (attributable instanceof User) {
                                        result = account.getPlainAttr(schema).orElse(null);
                                    }
                                    if (result == null) {
                                        result = PlainAttrGetter.DEFAULT.apply(attributable, schema);
                                    }
                                    return result;
                                }),
                        attributes);
            } catch (Exception e) {
                LOG.error(EXPRESSION_FAILED, item.getIntAttrName(), e);
            }
        });

        String connObjectKey = account.getConnObjectKeyValue();
        MappingUtils.getConnObjectKeyItem(provision).ifPresent(connObjectKeyItem -> {
            Attribute connObjectKeyExtAttr = AttributeUtil.find(connObjectKeyItem.getExtAttrName(), attributes);
            if (connObjectKeyExtAttr != null) {
                attributes.remove(connObjectKeyExtAttr);
                attributes.add(AttributeBuilder.build(connObjectKeyItem.getExtAttrName(), connObjectKey));
            }
            Name name = evaluateNAME(user, provision, connObjectKey);
            attributes.add(name);
            if (!connObjectKey.equals(name.getNameValue()) && connObjectKeyExtAttr == null) {
                attributes.add(AttributeBuilder.build(connObjectKeyItem.getExtAttrName(), connObjectKey));
            }
        });

        if (account.isSuspended() != null) {
            attributes.add(AttributeBuilder.buildEnabled(BooleanUtils.negate(account.isSuspended())));
        }
        if (!changePwd) {
            Attribute pwdAttr = AttributeUtil.find(OperationalAttributes.PASSWORD_NAME, attributes);
            if (pwdAttr != null) {
                attributes.remove(pwdAttr);
            }
        }

        return attributes;
    }

    @Override
    public PreparedAttrs prepareAttrsFromRealm(final Realm realm, final ExternalResource resource) {
        if (resource.getOrgUnit() == null) {
            LOG.error("No mapping configured for Realms");
            return new PreparedAttrs(null, Set.of());
        }

        LOG.debug("Preparing resource attributes for {} with orgUnit {}", realm, resource.getOrgUnit());

        Set<Attribute> attributes = new HashSet<>();
        Mutable<String> connObjectKeyValue = new MutableObject<>();

        MappingUtils.getPropagationItems(resource.getOrgUnit().getItems().stream()).forEach(item -> {
            LOG.debug(PROCESSING_EXPRESSION, item.getIntAttrName());

            try {
                processPreparedAttr(
                        prepareAttr(
                                resource,
                                item,
                                realm),
                        attributes).ifPresent(connObjectKeyValue::setValue);
            } catch (Exception e) {
                LOG.error(EXPRESSION_FAILED, item.getIntAttrName(), e);
            }
        });

        resource.getOrgUnit().getConnObjectKeyItem().ifPresent(item -> {
            Attribute connObjectKeyAttr = AttributeUtil.find(item.getExtAttrName(), attributes);
            if (connObjectKeyAttr != null) {
                attributes.remove(connObjectKeyAttr);
                attributes.add(AttributeBuilder.build(item.getExtAttrName(), connObjectKeyValue.get()));
            }

            Name name = evaluateNAME(realm, resource.getOrgUnit(), connObjectKeyValue.get());
            attributes.add(name);

            Optional.ofNullable(connObjectKeyValue.get()).
                    filter(cokv -> connObjectKeyAttr == null && !cokv.equals(name.getNameValue())).
                    ifPresent(cokv -> attributes.add(AttributeBuilder.build(item.getExtAttrName(), cokv)));
        });

        return new PreparedAttrs(connObjectKeyValue.get(), attributes);
    }

    protected Optional<String> decodePassword(final Account account) {
        try {
            return Optional.of(encryptorManager.getInstance().
                    decode(account.getPassword(), account.getCipherAlgorithm()));
        } catch (Exception e) {
            LOG.error("Could not decode password for {}", account, e);
            return Optional.empty();
        }
    }

    protected Optional<String> getPasswordAttrValue(final Account account, final String defaultValue) {
        Optional<String> passwordAttrValue;
        if (account instanceof LinkedAccount) {
            passwordAttrValue = account.getPassword() == null
                    ? Optional.of(defaultValue)
                    : decodePassword(account);
        } else {
            if (StringUtils.isNotBlank(defaultValue)) {
                passwordAttrValue = Optional.of(defaultValue);
            } else if (account.canDecodeSecrets()) {
                passwordAttrValue = decodePassword(account);
            } else {
                passwordAttrValue = Optional.empty();
            }
        }

        return passwordAttrValue;
    }

    @Override
    public PreparedAttr prepareAttr(
            final ExternalResource resource,
            final Provision provision,
            final Item item,
            final Any any,
            final String password,
            final AccountGetter usernameAccountGetter,
            final AccountGetter passwordAccountGetter,
            final PlainAttrGetter plainAttrGetter) {

        IntAttrName intAttrName;
        try {
            intAttrName = intAttrNameParser.parse(item.getIntAttrName(), any.getType().getKind());
        } catch (ParseException e) {
            LOG.error(INVALID_INT_ATTR, item.getIntAttrName(), e);
            return null;
        }

        AttrSchemaType schemaType = Optional.ofNullable(intAttrName.getSchemaInfo()).
                filter(schemaInfo -> schemaInfo.schema() instanceof PlainSchema).
                map(schemaInfo -> schemaInfo.schema().getType()).
                orElse(AttrSchemaType.String);

        IntValues intValues = getSelf().getIntValues(
                resource, provision, item, intAttrName, schemaType, any, usernameAccountGetter, plainAttrGetter);
        schemaType = intValues.attrSchemaType();
        List<PlainAttrValue> values = intValues.values();

        List<Object> objValues = transformPlainAttrValues(intAttrName, schemaType, values);

        PreparedAttr result;
        if (item.isConnObjectKey()) {
            result = new PreparedAttr(objValues.isEmpty() ? null : objValues.getFirst().toString(), null);
        } else if (item.isPassword() && any instanceof User user) {
            result = getPasswordAttrValue(passwordAccountGetter.apply(user), password).
                    map(passwordAttrValue -> new PreparedAttr(
                            null, AttributeBuilder.buildPassword(passwordAttrValue.toCharArray()))).
                    orElse(null);
        } else if (objValues.isEmpty()) {
            result = new PreparedAttr(
                    null,
                    AttributeBuilder.build(item.getExtAttrName()));
        } else if (OperationalAttributes.PASSWORD_NAME.equals(item.getExtAttrName())) {
            result = new PreparedAttr(
                    null,
                    AttributeBuilder.buildPassword(objValues.getFirst().toString().toCharArray()));
        } else {
            result = new PreparedAttr(
                    null,
                    AttributeBuilder.build(item.getExtAttrName(), objValues));
        }

        return result;
    }

    @Override
    public PreparedAttr prepareAttr(
            final ExternalResource resource,
            final Item item,
            final Realm realm) {

        IntAttrName intAttrName;
        try {
            intAttrName = intAttrNameParser.parse(item.getIntAttrName());
        } catch (ParseException e) {
            LOG.error(INVALID_INT_ATTR, item.getIntAttrName(), e);
            return null;
        }

        AttrSchemaType schemaType = Optional.ofNullable(intAttrName.getSchemaInfo()).
                filter(schemaInfo -> schemaInfo.schema() instanceof PlainSchema).
                map(schemaInfo -> schemaInfo.schema().getType()).
                orElse(AttrSchemaType.String);

        IntValues intValues = getSelf().getIntValues(resource, item, intAttrName, schemaType, realm);
        schemaType = intValues.attrSchemaType();
        List<PlainAttrValue> values = intValues.values();

        List<Object> objValues = transformPlainAttrValues(intAttrName, schemaType, values);

        PreparedAttr result;
        if (item.isConnObjectKey()) {
            result = new PreparedAttr(objValues.isEmpty() ? null : objValues.getFirst().toString(), null);
        } else if (objValues.isEmpty()) {
            result = new PreparedAttr(
                    null,
                    AttributeBuilder.build(item.getExtAttrName()));
        } else if (OperationalAttributes.PASSWORD_NAME.equals(item.getExtAttrName())) {
            result = new PreparedAttr(
                    null,
                    AttributeBuilder.buildPassword(objValues.iterator().next().toString().toCharArray()));
        } else {
            result = new PreparedAttr(
                    null,
                    AttributeBuilder.build(item.getExtAttrName(), objValues));
        }

        return result;
    }

    private List<Object> transformPlainAttrValues(
            final IntAttrName intAttrName,
            final AttrSchemaType schemaType,
            final List<PlainAttrValue> values) {

        List<Object> objValues = new ArrayList<>();
        for (PlainAttrValue value : values) {
            if (schemaType == AttrSchemaType.Encrypted
                    && intAttrName.getSchemaInfo().schema() instanceof PlainSchema schema) {
                String decoded = null;
                try {
                    decoded = encryptorManager.getInstance(schema.getSecretKey()).
                            decode(value.getStringValue(), schema.getCipherAlgorithm());
                } catch (Exception e) {
                    LOG.warn("Could not decode value for {} with algorithm {}",
                            intAttrName.getSchemaInfo(), schema.getCipherAlgorithm(), e);
                }
                objValues.add(Optional.ofNullable(decoded).orElse(value.getStringValue()));
            } else if (FrameworkUtil.isSupportedAttributeType(schemaType.getType())) {
                objValues.add(value.getValue());
            } else {
                PlainSchema plainSchema = Optional.ofNullable(intAttrName.getSchemaInfo()).
                        map(IntAttrName.SchemaInfo::schema).
                        filter(PlainSchema.class::isInstance).map(PlainSchema.class::cast).
                        orElse(null);
                if (plainSchema == null || plainSchema.getType() != schemaType) {
                    objValues.add(value.getValueAsString(schemaType));
                } else {
                    objValues.add(value.getValueAsString(plainSchema));
                }
            }
        }
        return objValues;
    }

    @Transactional(readOnly = true)
    @Override
    public IntValues getIntValues(
            final ExternalResource resource,
            final Provision provision,
            final Item item,
            final IntAttrName intAttrName,
            final AttrSchemaType schemaType,
            final Any any,
            final AccountGetter usernameAccountGetter,
            final PlainAttrGetter plainAttrGetter) {

        LOG.debug("Get internal values for {} as '{}' on {}", any, item.getIntAttrName(), resource);

        List<Any> references = new ArrayList<>();
        if (intAttrName.getExternalGroup() == null
                && intAttrName.getExternalAnyObject() == null
                && intAttrName.getExternalUser() == null) {

            references.add(any);
        }

        Relationship<?, ?> relationship = null;
        Membership<?> membership = null;

        if (intAttrName.getExternalUser() != null) {
            userDAO.findByUsername(intAttrName.getExternalUser()).ifPresentOrElse(
                    references::add,
                    () -> LOG.warn("Could not find user {}, ignoring", intAttrName.getExternalUser()));
        } else if (intAttrName.getExternalGroup() != null) {
            groupDAO.findByName(intAttrName.getExternalGroup()).ifPresentOrElse(
                    references::add,
                    () -> LOG.warn("Could not find group {}, ignoring", intAttrName.getExternalGroup()));
        } else if (intAttrName.getExternalAnyObject() != null) {
            references.addAll(anyObjectDAO.findByName(intAttrName.getExternalAnyObject()));
        } else if (intAttrName.getMembership() != null && any instanceof Groupable<?, ?, ?> groupable) {
            membership = groupDAO.findByName(intAttrName.getMembership()).
                    flatMap(group -> groupable.getMembership(group.getKey())).
                    orElse(null);
        } else if (intAttrName.getRelationshipInfo() != null && any instanceof Relatable<?, ?> relatable) {
            RelationshipType relationshipType = relationshipTypeDAO.findById(
                    intAttrName.getRelationshipInfo().type()).orElse(null);
            if (relationshipType == null) {
                LOG.warn("Could not find relationship type {}, ignoring", intAttrName.getRelationshipInfo().type());
            } else {
                relationship = anyObjectDAO.findByName(
                                relationshipType.getRightEndAnyType().getKey(), intAttrName.getRelationshipInfo().anyObject()).
                        flatMap(otherEnd -> relatable.getRelationship(relationshipType, otherEnd.getKey())).
                        orElse(null);
            }
        }
        if (references.isEmpty()) {
            LOG.warn("Could not determine the reference instance for {}", item.getIntAttrName());
            return new IntValues(schemaType, List.of());
        }

        List<PlainAttrValue> values = new ArrayList<>();
        for (Any ref : references) {
            if (intAttrName.getField() != null) {
                processField(intAttrName.getField(), ref, provision, resource, values, usernameAccountGetter);
            } else if (intAttrName.getSchemaInfo() != null) {
                processSchema(intAttrName.getSchemaInfo(), ref, membership, relationship, plainAttrGetter, values);
            }
        }

        LOG.debug("Internal values: {}", values);

        IntValues transformed = new IntValues(schemaType, values);
        for (ItemTransformer transformer : MappingUtils.getItemTransformers(item, getTransformers(item))) {
            transformed = transformer.beforePropagation(
                    item, any, transformed.attrSchemaType(), transformed.values());
        }
        LOG.debug("Transformed values: {}", values);

        return transformed;
    }

    private void processField(
            final String field,
            final Any ref,
            final Provision provision,
            final ExternalResource resource,
            final List<PlainAttrValue> values,
            final AccountGetter usernameAccountGetter) {

        PlainAttrValue attrValue = new PlainAttrValue();
        switch (field) {
            case "key" -> {
                attrValue.setStringValue(ref.getKey());
                values.add(attrValue);
            }
            case "username" -> {
                if (ref instanceof Account account) {
                    attrValue.setStringValue(usernameAccountGetter.apply(account).getUsername());
                    values.add(attrValue);
                }
            }
            case "realm" -> {
                attrValue.setStringValue(ref.getRealm().getFullPath());
                values.add(attrValue);
            }
            case "uManager", "gManager" -> {
                Mapping mappingTO = provision.getMapping();
                String managerValue = null;
                if (ref.getUManager() != null && AnyTypeKind.USER.name().equals(provision.getAnyType())) {
                    managerValue = getManagerValue(resource, provision, ref.getUManager());
                } else if (ref.getGManager() != null && AnyTypeKind.GROUP.name().equals(provision.getAnyType())) {
                    managerValue = getManagerValue(resource, provision, ref.getGManager());
                }
                if (StringUtils.isNotBlank(managerValue)) {
                    attrValue.setStringValue(managerValue);
                    values.add(attrValue);
                }
            }
            case "suspended" -> {
                if (ref instanceof User user) {
                    attrValue.setBooleanValue(user.isSuspended());
                    values.add(attrValue);
                }
            }
            case "mustChangePassword" -> {
                if (ref instanceof User user) {
                    attrValue.setBooleanValue(user.isMustChangePassword());
                    values.add(attrValue);
                }
            }
            default -> {
                try {
                    Object fieldValue = FieldUtils.readField(ref, field, true);
                    if (fieldValue instanceof TemporalAccessor temporalAccessor) {
                        attrValue.setStringValue(FormatUtils.format(temporalAccessor));
                    } else if (Boolean.TYPE.isInstance(fieldValue)) {
                        attrValue.setBooleanValue((Boolean) fieldValue);
                    } else if (Double.TYPE.isInstance(fieldValue) || Float.TYPE.isInstance(fieldValue)) {
                        attrValue.setDoubleValue((Double) fieldValue);
                    } else if (Long.TYPE.isInstance(fieldValue) || Integer.TYPE.isInstance(fieldValue)) {
                        attrValue.setLongValue((Long) fieldValue);
                    } else {
                        attrValue.setStringValue(fieldValue.toString());
                    }
                    values.add(attrValue);
                } catch (Exception e) {
                    LOG.error("Could not read value of '{}' from {}", field, ref, e);
                }
            }
        }
    }

    private void processSchema(
            final IntAttrName.SchemaInfo schemaInfo,
            final Any ref,
            final Membership<?> membership,
            final Relationship<?, ?> relationship,
            final PlainAttrGetter plainAttrGetter,
            final List<PlainAttrValue> values) {

        switch (schemaInfo.type()) {
            case PLAIN -> {
                PlainAttr attr = membership == null && relationship == null
                        ? plainAttrGetter.apply(ref, schemaInfo.schema().getKey())
                        : membership == null
                          ? ((Relatable<?, ?>) ref).getPlainAttr(schemaInfo.schema().getKey(), relationship).orElse(null)
                          : ((Groupable<?, ?, ?>) ref).getPlainAttr(schemaInfo.schema().getKey(), membership).orElse(null);
                if (attr != null) {
                    if (attr.getUniqueValue() != null) {
                        values.add(clonePlainAttrValue(attr.getUniqueValue()));
                    } else if (attr.getValues() != null) {
                        attr.getValues().forEach(value -> values.add(clonePlainAttrValue(value)));
                    }
                }
            }
            case DERIVED -> {
                DerSchema derSchema = (DerSchema) schemaInfo.schema();
                String derValue = membership == null && relationship == null
                        ? derAttrHandler.getValue(ref, derSchema)
                        : membership == null
                          ? derAttrHandler.getValue((Relatable<?, ?>) ref, relationship, derSchema)
                          : derAttrHandler.getValue((Groupable<?, ?, ?>) ref, membership, derSchema);
                if (derValue != null) {
                    PlainAttrValue attrValue = new PlainAttrValue();
                    attrValue.setStringValue(derValue);
                    values.add(attrValue);
                }
            }
            default -> {
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public IntValues getIntValues(
            final ExternalResource resource,
            final Item item,
            final IntAttrName intAttrName,
            final AttrSchemaType schemaType,
            final Realm realm) {

        LOG.debug("Get internal values for {} as '{}' on {}", realm, item.getIntAttrName(), resource);

        List<PlainAttrValue> values = new ArrayList<>();
        if (intAttrName.getField() != null) {
            PlainAttrValue attrValue = new PlainAttrValue();
            switch (intAttrName.getField()) {
                case "key" -> {
                    attrValue.setStringValue(realm.getKey());
                    values.add(attrValue);
                }
                case "name" -> {
                    attrValue.setStringValue(realm.getName());
                    values.add(attrValue);
                }
                case "fullPath" -> {
                    attrValue.setStringValue(realm.getFullPath());
                    values.add(attrValue);
                }
                default -> {
                }
            }
        } else if (intAttrName.getSchemaInfo() != null) {
            switch (intAttrName.getSchemaInfo().type()) {
                case PLAIN -> realm.getPlainAttr(intAttrName.getSchemaInfo().schema().getKey()).ifPresent(attr -> {
                    if (attr.getUniqueValue() != null) {
                        values.add(clonePlainAttrValue(attr.getUniqueValue()));
                    } else if (attr.getValues() != null) {
                        attr.getValues().forEach(value -> values.add(clonePlainAttrValue(value)));
                    }
                });
                case DERIVED -> Optional.ofNullable(derAttrHandler.getValue(
                                realm, (DerSchema) intAttrName.getSchemaInfo().schema())).
                        ifPresent(derValue -> {
                            PlainAttrValue attrValue = new PlainAttrValue();
                            attrValue.setStringValue(derValue);
                            values.add(attrValue);
                        });
                default -> {
                }
            }
        }

        LOG.debug("Internal values: {}", values);

        IntValues transformed = new IntValues(schemaType, values);
        for (ItemTransformer transformer : MappingUtils.getItemTransformers(item, getTransformers(item))) {
            transformed = transformer.beforePropagation(
                    item, realm, transformed.attrSchemaType(), transformed.values());
        }
        LOG.debug("Transformed values: {}", values);

        return transformed;
    }

    protected String getManagerValue(
            final ExternalResource resource,
            final Provision provision,
            final Any any) {

        Optional<Item> connObjectKeyItem = MappingUtils.getConnObjectKeyItem(provision);

        PreparedAttr preparedAttr = null;
        if (connObjectKeyItem.isPresent()) {
            preparedAttr = prepareAttr(
                    resource,
                    provision,
                    connObjectKeyItem.get(),
                    any,
                    null,
                    AccountGetter.DEFAULT,
                    AccountGetter.DEFAULT,
                    PlainAttrGetter.DEFAULT);
        }

        return Optional.ofNullable(preparedAttr).
                map(attr -> evaluateNAME(any, provision, attr.connObjectLink()).getNameValue()).orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<String> getConnObjectKeyValue(
            final Any any,
            final ExternalResource resource,
            final Provision provision) {

        Optional<Item> connObjectKeyItem = provision.getMapping().getConnObjectKeyItem();
        if (connObjectKeyItem.isEmpty()) {
            LOG.error("Unable to locate conn object key item for {}", any.getType().getKey());
            return Optional.empty();
        }

        Item item = connObjectKeyItem.get();
        IntValues intValues;
        try {
            intValues = getSelf().getIntValues(
                    resource,
                    provision,
                    item,
                    intAttrNameParser.parse(item.getIntAttrName(), any.getType().getKind()),
                    AttrSchemaType.String,
                    any,
                    AccountGetter.DEFAULT,
                    PlainAttrGetter.DEFAULT);
        } catch (ParseException e) {
            LOG.error(INVALID_INT_ATTR, item.getIntAttrName(), e);
            intValues = new IntValues(AttrSchemaType.String, List.of());
        }
        return intValues.values().isEmpty()
                ? Optional.empty()
                : Optional.of(intValues.values().getFirst().getValueAsString());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<String> getConnObjectKeyValue(final Realm realm, final ExternalResource resource) {
        if (resource.getOrgUnit() == null) {
            LOG.error("No mapping configured for Realms");
            return Optional.empty();
        }

        Optional<Item> connObjectKeyItem = resource.getOrgUnit().getConnObjectKeyItem();
        if (connObjectKeyItem.isEmpty()) {
            LOG.error("Unable to locate conn object key item for Realms");
            return Optional.empty();
        }

        Item item = connObjectKeyItem.get();
        IntValues intValues;
        try {
            intValues = getSelf().getIntValues(
                    resource,
                    item,
                    intAttrNameParser.parse(item.getIntAttrName()),
                    AttrSchemaType.String,
                    realm);
        } catch (ParseException e) {
            LOG.error(INVALID_INT_ATTR, item.getIntAttrName(), e);
            intValues = new IntValues(AttrSchemaType.String, List.of());
        }
        return intValues.values().isEmpty()
                ? Optional.empty()
                : Optional.of(intValues.values().getFirst().getValueAsString());
    }

    @Override
    public void setIntValues(final Item item, final Attribute attr, final AnyTO anyTO) {
        List<Object> values = null;
        if (attr != null) {
            values = attr.getValue();
            for (ItemTransformer transformer : MappingUtils.getItemTransformers(item, getTransformers(item))) {
                values = transformer.beforePull(item, anyTO, values);
            }
        }
        values = Optional.ofNullable(values).orElseGet(List::of);

        IntAttrName intAttrName;
        try {
            intAttrName = intAttrNameParser.parse(item.getIntAttrName(), AnyTypeKind.fromTOClass(anyTO.getClass()));
        } catch (ParseException e) {
            LOG.error(INVALID_INT_ATTR, item.getIntAttrName(), e);
            return;
        }

        if (intAttrName.getField() != null && !values.isEmpty() && values.getFirst() != null) {
            switch (intAttrName.getField()) {
                case "password" -> {
                    if (anyTO instanceof UserTO userTO) {
                        userTO.setPassword(ConnObjectUtils.getPassword(values.getFirst()));
                    }
                }
                case "username" -> {
                    if (anyTO instanceof UserTO userTO) {
                        userTO.setUsername(values.getFirst().toString());
                    }
                }
                case "name" -> {
                    if (anyTO instanceof GroupTO groupTO) {
                        groupTO.setName(values.getFirst().toString());
                    } else if (anyTO instanceof AnyObjectTO anyObjectTO) {
                        anyObjectTO.setName(values.getFirst().toString());
                    }
                }
                case "mustChangePassword" -> {
                    if (anyTO instanceof UserTO userTO) {
                        userTO.setMustChangePassword(BooleanUtils.toBoolean(values.getFirst().toString()));
                    }
                }
                case "uManager" -> anyTO.setUManager(values.getFirst().toString());
                case "gManager" -> anyTO.setGManager(values.getFirst().toString());
                default -> {
                }
            }
        } else if (intAttrName.getSchemaInfo() != null && attr != null) {
            GroupableRelatableTO groupableTO;
            Group group;
            if (anyTO instanceof GroupableRelatableTO groupableRelatableTO && intAttrName.getMembership() != null) {
                groupableTO = groupableRelatableTO;
                group = groupDAO.findByName(intAttrName.getMembership()).orElse(null);
            } else {
                group = null;
                groupableTO = null;
            }

            switch (intAttrName.getSchemaInfo().type()) {
                case PLAIN -> {
                    Attr attrTO = new Attr();
                    attrTO.setSchema(intAttrName.getSchemaInfo().schema().getKey());
                    PlainSchema schema = (PlainSchema) intAttrName.getSchemaInfo().schema();
                    for (Object value : values) {
                        AttrSchemaType schemaType = schema == null ? AttrSchemaType.String : schema.getType();
                        if (value != null) {
                            if (schemaType == AttrSchemaType.Binary) {
                                attrTO.getValues().add(Base64.getEncoder().encodeToString((byte[]) value));
                            } else {
                                attrTO.getValues().add(value.toString());
                            }
                        }
                    }
                    if (groupableTO == null || group == null) {
                        anyTO.getPlainAttrs().add(attrTO);
                    } else {
                        MembershipTO membership = groupableTO.getMembership(group.getKey()).orElseGet(() -> {
                            MembershipTO newMemb = new MembershipTO.Builder(group.getKey()).build();
                            groupableTO.getMemberships().add(newMemb);
                            return newMemb;
                        });
                        membership.getPlainAttrs().add(attrTO);
                    }
                }
                case DERIVED -> {
                    Attr attrTO = new Attr();
                    attrTO.setSchema(intAttrName.getSchemaInfo().schema().getKey());
                    if (groupableTO == null || group == null) {
                        anyTO.getDerAttrs().add(attrTO);
                    } else {
                        MembershipTO membership = groupableTO.getMembership(group.getKey()).orElseGet(() -> {
                            MembershipTO newMemb = new MembershipTO.Builder(group.getKey()).build();
                            groupableTO.getMemberships().add(newMemb);
                            return newMemb;
                        });
                        membership.getDerAttrs().add(attrTO);
                    }
                }
                default -> {
                }
            }
        }
    }

    @Override
    public void setIntValues(final Item item, final Attribute attr, final RealmTO realmTO) {
        List<Object> values = null;
        if (attr != null) {
            values = attr.getValue();
            for (ItemTransformer transformer : MappingUtils.getItemTransformers(item, getTransformers(item))) {
                values = transformer.beforePull(item, realmTO, values);
            }
        }
        values = Optional.ofNullable(values).orElseGet(List::of);

        IntAttrName intAttrName;
        try {
            intAttrName = intAttrNameParser.parse(item.getIntAttrName());
        } catch (ParseException e) {
            LOG.error(INVALID_INT_ATTR, item.getIntAttrName(), e);
            return;
        }

        if (intAttrName.getField() != null) {
            if ("name".equals(intAttrName.getField())) {
                realmTO.setName(values.isEmpty() || values.getFirst() == null
                        ? null
                        : values.getFirst().toString());
            } else if ("fullpath".equals(intAttrName.getField())) {
                String parentFullPath = StringUtils.substringBeforeLast(values.getFirst().toString(), "/");
                realmSearchDAO.findByFullPath(parentFullPath).ifPresentOrElse(
                        parent -> realmTO.setParent(parent.getFullPath()),
                        () -> LOG.warn("Could not find Realm with path {}, ignoring", parentFullPath));
            }
        } else if (intAttrName.getSchemaInfo() != null && attr != null) {
            switch (intAttrName.getSchemaInfo().type()) {
                case PLAIN -> {
                    Attr attrTO = new Attr();
                    attrTO.setSchema(intAttrName.getSchemaInfo().schema().getKey());
                    PlainSchema schema = (PlainSchema) intAttrName.getSchemaInfo().schema();
                    for (Object value : values) {
                        AttrSchemaType schemaType = schema == null ? AttrSchemaType.String : schema.getType();
                        if (value != null) {
                            if (schemaType == AttrSchemaType.Binary) {
                                attrTO.getValues().add(Base64.getEncoder().encodeToString((byte[]) value));
                            } else {
                                attrTO.getValues().add(value.toString());
                            }
                        }
                    }
                    realmTO.getPlainAttrs().add(attrTO);
                }
                case DERIVED -> {
                    Attr attrTO = new Attr();
                    attrTO.setSchema(intAttrName.getSchemaInfo().schema().getKey());
                    realmTO.getDerAttrs().add(attrTO);
                }
                default -> {
                }
            }
        }
    }

    @Override
    public boolean hasMustChangePassword(final Provision provision) {
        return Optional.ofNullable(provision.getMapping()).
                map(mapping -> mapping.getItems().stream().
                        anyMatch(item -> MUST_CHANGE_PWD.equals(item.getIntAttrName()))).
                orElse(false);
    }
}