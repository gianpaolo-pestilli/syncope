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
import org.apache.syncope.common.lib.to.*;
import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.syncope.common.lib.types.AttrSchemaType;
import org.apache.syncope.common.lib.types.SchemaType;
import org.apache.syncope.core.persistence.api.EncryptorManager;
import org.apache.syncope.core.persistence.api.dao.*;
import org.apache.syncope.core.persistence.api.entity.*;
import org.apache.syncope.core.persistence.api.entity.group.Group;
import org.apache.syncope.core.persistence.api.entity.user.Account;
import org.apache.syncope.core.persistence.api.entity.user.LinkedAccount;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.persistence.api.utils.FormatUtils;
import org.apache.syncope.core.provisioning.api.*;
import org.apache.syncope.core.provisioning.api.data.ItemTransformer;
import org.apache.syncope.core.provisioning.api.jexl.JexlContextBuilder;
import org.apache.syncope.core.provisioning.api.jexl.JexlTools;
import org.apache.syncope.core.provisioning.java.utils.ConnObjectUtils;
import org.apache.syncope.core.provisioning.java.utils.MappingUtils;
import org.identityconnectors.framework.common.FrameworkUtil;
import org.identityconnectors.framework.common.objects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

//Class A C4
public class DefaultMappingManager implements MappingManager, ApplicationContextAware {

    protected static final Logger LOG = LoggerFactory.getLogger(DefaultMappingManager.class);

    private static final String PROCESSING_EXPRESSION = "Processing expression '{}'";
    private static final String EXPRESSION_FAILED = "Expression '{}' processing failed";
    private static final String MUST_CHANGE_PWD = "mustChangePassword";
    private static final String U_MANAGER = "uManager";
    private static final String G_MANAGER = "gManager";

    private ApplicationContext ctx;

    @Override
    public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }

    private MappingManager getSelf() {
        return (ctx != null) ? ctx.getBean(MappingManager.class) : this;
    }

    public static Optional<String> processPreparedAttr(final PreparedAttr preparedAttr, final Set<Attribute> attributes) {
        if (preparedAttr == null) return Optional.empty();

        String connObjectKey = preparedAttr.connObjectLink();
        if (preparedAttr.attribute() != null) {
            Optional.ofNullable(AttributeUtil.find(preparedAttr.attribute().getName(), attributes)).ifPresentOrElse(
                    alreadyAdded -> {
                        attributes.remove(alreadyAdded);
                        Set<Object> values = new HashSet<>();
                        if (!CollectionUtils.isEmpty(alreadyAdded.getValue())) values.addAll(alreadyAdded.getValue());
                        if (preparedAttr.attribute().getValue() != null) values.addAll(preparedAttr.attribute().getValue());
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
        }
        LOG.debug("Add connObjectLink [{}] as {}", evalConnObjectLink, Name.NAME);
        LOG.debug("connObjectKey [{}] will be used as {}", connObjectKey, Uid.NAME);
        return new Name(evalConnObjectLink);
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
            final UserDAO userDAO, final AnyObjectDAO anyObjectDAO, final GroupDAO groupDAO,
            final RelationshipTypeDAO relationshipTypeDAO, final RealmSearchDAO realmSearchDAO,
            final ImplementationDAO implementationDAO, final DerAttrHandler derAttrHandler,
            final IntAttrNameParser intAttrNameParser, final EncryptorManager encryptorManager,
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
        return item.getTransformers().stream()
                .map(implementationDAO::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    protected Name evaluateNAME(final Any any, final Provision provision, final String connObjectKey) {
        String connObjectLink = Optional.ofNullable(provision.getMapping()).map(Mapping::getConnObjectLink).orElse(null);
        String evalConnObjectLink = null;
        if (StringUtils.isNotBlank(connObjectLink)) {
            JexlContext jexlContext = new JexlContextBuilder().
                    fields(any).plainAttrs(any.getPlainAttrs()).derAttrs(derAttrHandler.getValues(any)).build();
            evalConnObjectLink = jexlTools.evaluateExpression(connObjectLink, jexlContext).toString();
        }
        return getName(evalConnObjectLink, connObjectKey);
    }

    protected Name evaluateNAME(final Realm realm, final OrgUnit orgUnit, final String connObjectKey) {
        String connObjectLink = orgUnit.getConnObjectLink();
        String evalConnObjectLink = null;
        if (StringUtils.isNotBlank(connObjectLink)) {
            JexlContext jexlContext = new JexlContextBuilder().
                    fields(realm).plainAttrs(realm.getPlainAttrs()).derAttrs(derAttrHandler.getValues(realm)).build();
            evalConnObjectLink = jexlTools.evaluateExpression(connObjectLink, jexlContext).toString();
        }
        return getName(evalConnObjectLink, connObjectKey);
    }

    @Transactional(readOnly = true)
    @Override
    public PreparedAttrs prepareAttrsFromAny(final Any any, final String password, final boolean changePwd,
                                             final Boolean enable, final ExternalResource resource, final Provision provision) {

        Set<Attribute> attributes = new HashSet<>();
        Mutable<String> connObjectKeyValue = new MutableObject<>();

        MappingUtils.getPropagationItems(provision.getMapping().getItems().stream()).forEach(item -> {
            LOG.debug(PROCESSING_EXPRESSION, item.getIntAttrName());
            try {
                processPreparedAttr(getSelf().prepareAttr(resource, provision, item, any, password,
                        AccountGetter.DEFAULT, AccountGetter.DEFAULT, PlainAttrGetter.DEFAULT), attributes)
                        .ifPresent(connObjectKeyValue::setValue);
            } catch (Exception _) {
                LOG.error(EXPRESSION_FAILED, item.getIntAttrName());
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
            Optional.ofNullable(AttributeUtil.find(OperationalAttributes.PASSWORD_NAME, attributes)).ifPresent(attributes::remove);
        }
        return new PreparedAttrs(connObjectKeyValue.get(), attributes);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Attribute> prepareAttrsFromLinkedAccount(final User user, final LinkedAccount account,
                                                        final String password, final boolean changePwd, final Provision provision) {

        Set<Attribute> attributes = new HashSet<>();
        MappingUtils.getPropagationItems(provision.getMapping().getItems().stream()).forEach(item -> {
            LOG.debug(PROCESSING_EXPRESSION, item.getIntAttrName());
            try {
                processPreparedAttr(getSelf().prepareAttr(account.getResource(), provision, item, user, password,
                        acct -> account.getUsername() == null ? AccountGetter.DEFAULT.apply(acct) : account,
                        acct -> account.getPassword() == null ? AccountGetter.DEFAULT.apply(acct) : account,
                        (attributable, schema) -> {
                            PlainAttr result = null;
                            if (attributable instanceof User) result = account.getPlainAttr(schema).orElse(null);
                            return (result == null) ? PlainAttrGetter.DEFAULT.apply(attributable, schema) : result;
                        }), attributes);
            } catch (Exception _) {
                LOG.error(EXPRESSION_FAILED, item.getIntAttrName());
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

        if (account.isSuspended() != null) attributes.add(AttributeBuilder.buildEnabled(BooleanUtils.negate(account.isSuspended())));
        if (!changePwd) Optional.ofNullable(AttributeUtil.find(OperationalAttributes.PASSWORD_NAME, attributes)).ifPresent(attributes::remove);

        return attributes;
    }

    @Override
    public PreparedAttrs prepareAttrsFromRealm(final Realm realm, final ExternalResource resource) {
        if (resource.getOrgUnit() == null) return new PreparedAttrs(null, Set.of());
        Set<Attribute> attributes = new HashSet<>();
        Mutable<String> connObjectKeyValue = new MutableObject<>();

        MappingUtils.getPropagationItems(resource.getOrgUnit().getItems().stream()).forEach(item -> {
            try {
                processPreparedAttr(getSelf().prepareAttr(resource, item, realm), attributes).ifPresent(connObjectKeyValue::setValue);
            } catch (Exception _) {
                LOG.error(EXPRESSION_FAILED, item.getIntAttrName());
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
            return Optional.of(encryptorManager.getInstance().decode(account.getPassword(), account.getCipherAlgorithm()));
        } catch (Exception _) {
            return Optional.empty();
        }
    }

    protected Optional<String> getPasswordAttrValue(final Account account, final String defaultValue) {
        if (account instanceof LinkedAccount) {
            return (account.getPassword() == null) ? Optional.of(defaultValue) : decodePassword(account);
        }
        if (StringUtils.isNotBlank(defaultValue)) return Optional.of(defaultValue);
        return account.canDecodeSecrets() ? decodePassword(account) : Optional.empty();
    }

    @Override
    public PreparedAttr prepareAttr(final ExternalResource resource, final Provision provision, final Item item,
                                    final Any any, final String password, final AccountGetter usernameAccountGetter,
                                    final AccountGetter passwordAccountGetter, final PlainAttrGetter plainAttrGetter) {

        IntAttrName intAttrName;
        try {
            intAttrName = intAttrNameParser.parse(item.getIntAttrName(), any.getType().getKind());
        } catch (ParseException _) {
            return null;
        }

        AttrSchemaType schemaType = Optional.ofNullable(intAttrName.getSchemaInfo())
                .filter(si -> si.schema() instanceof PlainSchema).map(si -> si.schema().getType()).orElse(AttrSchemaType.String);

        IntValues intValues = getSelf().getIntValues(resource, provision, item, intAttrName, schemaType, any, usernameAccountGetter, plainAttrGetter);
        List<Object> objValues = transformPlainAttrValues(intAttrName, intValues.attrSchemaType(), intValues.values());

        if (item.isConnObjectKey()) return new PreparedAttr(objValues.isEmpty() ? null : objValues.getFirst().toString(), null);
        if (item.isPassword() && any instanceof User user) {
            return getPasswordAttrValue(passwordAccountGetter.apply(user), password)
                    .map(v -> new PreparedAttr(null, AttributeBuilder.buildPassword(v.toCharArray()))).orElse(null);
        }
        if (objValues.isEmpty()) return new PreparedAttr(null, AttributeBuilder.build(item.getExtAttrName()));
        if (OperationalAttributes.PASSWORD_NAME.equals(item.getExtAttrName())) {
            return new PreparedAttr(null, AttributeBuilder.buildPassword(objValues.getFirst().toString().toCharArray()));
        }
        return new PreparedAttr(null, AttributeBuilder.build(item.getExtAttrName(), objValues));
    }

    @Override
    public PreparedAttr prepareAttr(final ExternalResource resource, final Item item, final Realm realm) {
        IntAttrName intAttrName;
        try {
            intAttrName = intAttrNameParser.parse(item.getIntAttrName());
        } catch (ParseException _) {
            return null;
        }
        AttrSchemaType schemaType = Optional.ofNullable(intAttrName.getSchemaInfo())
                .filter(si -> si.schema() instanceof PlainSchema).map(si -> si.schema().getType()).orElse(AttrSchemaType.String);

        IntValues intValues = getSelf().getIntValues(resource, item, intAttrName, schemaType, realm);
        List<Object> objValues = transformPlainAttrValues(intAttrName, intValues.attrSchemaType(), intValues.values());

        if (item.isConnObjectKey()) return new PreparedAttr(objValues.isEmpty() ? null : objValues.getFirst().toString(), null);
        if (objValues.isEmpty()) return new PreparedAttr(null, AttributeBuilder.build(item.getExtAttrName()));
        if (OperationalAttributes.PASSWORD_NAME.equals(item.getExtAttrName())) {
            return new PreparedAttr(null, AttributeBuilder.buildPassword(objValues.iterator().next().toString().toCharArray()));
        }
        return new PreparedAttr(null, AttributeBuilder.build(item.getExtAttrName(), objValues));
    }

    private List<Object> transformPlainAttrValues(final IntAttrName intAttrName, final AttrSchemaType schemaType, final List<PlainAttrValue> values) {
        List<Object> objValues = new ArrayList<>();
        for (PlainAttrValue value : values) {
            if (schemaType == AttrSchemaType.Encrypted && intAttrName.getSchemaInfo() != null && intAttrName.getSchemaInfo().schema() instanceof PlainSchema schema) {
                try {
                    String decoded = encryptorManager.getInstance(schema.getSecretKey()).decode(value.getStringValue(), schema.getCipherAlgorithm());
                    objValues.add(Optional.ofNullable(decoded).orElse(value.getStringValue()));
                } catch (Exception _) {
                    LOG.warn("Could not decode value for {} with algorithm {}", intAttrName.getSchemaInfo(), schema.getCipherAlgorithm());
                }
            } else if (FrameworkUtil.isSupportedAttributeType(schemaType.getType())) {
                objValues.add(value.getValue());
            } else {
                PlainSchema ps = (intAttrName.getSchemaInfo() != null && intAttrName.getSchemaInfo().schema() instanceof PlainSchema) ? (PlainSchema) intAttrName.getSchemaInfo().schema() : null;
                objValues.add((ps == null || ps.getType() != schemaType) ? value.getValueAsString(schemaType) : value.getValueAsString(ps));
            }
        }
        return objValues;
    }

    @Transactional(readOnly = true)
    @Override
    public IntValues getIntValues(final ExternalResource resource, final Provision provision, final Item item,
                                  final IntAttrName intAttrName, final AttrSchemaType schemaType, final Any any,
                                  final AccountGetter usernameAccountGetter, final PlainAttrGetter plainAttrGetter) {

        List<Any> references = new ArrayList<>();
        if (intAttrName.getExternalGroup() == null && intAttrName.getExternalAnyObject() == null && intAttrName.getExternalUser() == null) {
            references.add(any);
        }

        Relationship<?, ?> relationship = null;
        Membership<?> membership = null;
        if (intAttrName.getExternalUser() != null) userDAO.findByUsername(intAttrName.getExternalUser()).ifPresent(references::add);
        else if (intAttrName.getExternalGroup() != null) groupDAO.findByName(intAttrName.getExternalGroup()).ifPresent(references::add);
        else if (intAttrName.getExternalAnyObject() != null) references.addAll(anyObjectDAO.findByName(intAttrName.getExternalAnyObject()));
        else if (intAttrName.getMembership() != null && any instanceof Groupable<?, ?, ?> g) membership = groupDAO.findByName(intAttrName.getMembership()).flatMap(group -> g.getMembership(group.getKey())).orElse(null);
        else if (intAttrName.getRelationshipInfo() != null && any instanceof Relatable<?, ?> r) {
            relationshipTypeDAO.findById(intAttrName.getRelationshipInfo().type()).ifPresent(rt -> {
                anyObjectDAO.findByName(rt.getRightEndAnyType().getKey(), intAttrName.getRelationshipInfo().anyObject())
                        .flatMap(other -> r.getRelationship(rt, other.getKey())).ifPresent(rel -> { /* Intentionally empty */ });
            });
        }

        List<PlainAttrValue> values = new ArrayList<>();
        for (Any ref : references) {
            if (intAttrName.getField() != null) processField(intAttrName.getField(), ref, provision, resource, values, usernameAccountGetter);
            else if (intAttrName.getSchemaInfo() != null) processSchema(intAttrName.getSchemaInfo(), ref, membership, relationship, plainAttrGetter, values);
        }

        IntValues transformed = new IntValues(schemaType, values);
        for (ItemTransformer transformer : MappingUtils.getItemTransformers(item, getTransformers(item))) {
            transformed = transformer.beforePropagation(item, any, transformed.attrSchemaType(), transformed.values());
        }
        return transformed;
    }

    private void processField(String field, Any ref, Provision provision, ExternalResource resource, List<PlainAttrValue> values, AccountGetter usernameAccountGetter) {
        PlainAttrValue av = new PlainAttrValue();
        switch (field) {
            case "key" -> { av.setStringValue(ref.getKey()); values.add(av); }
            case "username" -> { if (ref instanceof Account acc) { av.setStringValue(usernameAccountGetter.apply(acc).getUsername()); values.add(av); } }
            case "realm" -> { av.setStringValue(ref.getRealm().getFullPath()); values.add(av); }
            case "suspended" -> { if (ref instanceof User u) { av.setBooleanValue(u.isSuspended()); values.add(av); } }
            case "mustChangePassword" -> { if (ref instanceof User u) { av.setBooleanValue(u.isMustChangePassword()); values.add(av); } }
            case U_MANAGER, G_MANAGER -> handleManagerField(field, ref, provision, resource, av, values);
            default -> handleDefaultField(field, ref, av, values);
        }
    }

    private void handleManagerField(String field, Any ref, Provision provision, ExternalResource resource, PlainAttrValue av, List<PlainAttrValue> values) {
        String manager = null;
        if (U_MANAGER.equals(field) && ref instanceof User u && u.getUManager() != null) manager = getManagerValue(resource, provision, u.getUManager());
        else if (G_MANAGER.equals(field) && ref instanceof Group g && g.getGManager() != null) manager = getManagerValue(resource, provision, g.getGManager());

        if (StringUtils.isNotBlank(manager)) { av.setStringValue(manager); values.add(av); }
    }

    private void handleDefaultField(String field, Any ref, PlainAttrValue av, List<PlainAttrValue> values) {
        try {
            Object fv = FieldUtils.readField(ref, field, true);
            if (fv instanceof TemporalAccessor ta) av.setStringValue(FormatUtils.format(ta));
            else if (fv instanceof Boolean b) av.setBooleanValue(b);
            else if (fv instanceof Double d) av.setDoubleValue(d);
            else if (fv instanceof Float fl) av.setDoubleValue(fl.doubleValue());
            else if (fv instanceof Long l) av.setLongValue(l);
            else if (fv instanceof Integer i) av.setLongValue(i.longValue());
            else av.setStringValue(fv.toString());
            values.add(av);
        } catch (Exception _) { LOG.error("Read error"); }
    }

    private void processSchema(IntAttrName.SchemaInfo si, Any ref, Membership<?> m, Relationship<?, ?> r, PlainAttrGetter pag, List<PlainAttrValue> values) {
        if (si.type() == SchemaType.PLAIN) {
            PlainAttr attr = (m == null && r == null) ? pag.apply(ref, si.schema().getKey()) :
                    (m == null) ? ((Relatable<?, ?>) ref).getPlainAttr(si.schema().getKey(), r).orElse(null) :
                            ((Groupable<?, ?, ?>) ref).getPlainAttr(si.schema().getKey(), m).orElse(null);
            if (attr != null) {
                if (attr.getUniqueValue() != null) values.add(clonePlainAttrValue(attr.getUniqueValue()));
                else attr.getValues().forEach(v -> values.add(clonePlainAttrValue(v)));
            }
        } else if (si.type() == SchemaType.DERIVED) {
            String v = (m == null && r == null) ? derAttrHandler.getValue(ref, (DerSchema) si.schema()) :
                    (m == null) ? derAttrHandler.getValue((Relatable<?, ?>) ref, r, (DerSchema) si.schema()) :
                            derAttrHandler.getValue((Groupable<?, ?, ?>) ref, m, (DerSchema) si.schema());
            if (v != null) { PlainAttrValue av = new PlainAttrValue(); av.setStringValue(v); values.add(av); }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public IntValues getIntValues(final ExternalResource resource, final Item item, final IntAttrName intAttrName,
                                  final AttrSchemaType schemaType, final Realm realm) {
        List<PlainAttrValue> values = new ArrayList<>();
        if (intAttrName.getField() != null) {
            PlainAttrValue av = new PlainAttrValue();
            switch (intAttrName.getField()) {
                case "key" -> { av.setStringValue(realm.getKey()); values.add(av); }
                case "name" -> { av.setStringValue(realm.getName()); values.add(av); }
                case "fullPath" -> { av.setStringValue(realm.getFullPath()); values.add(av); }
            }
        } else if (intAttrName.getSchemaInfo() != null) {
            if (intAttrName.getSchemaInfo().type() == SchemaType.PLAIN) {
                realm.getPlainAttr(intAttrName.getSchemaInfo().schema().getKey()).ifPresent(a -> {
                    if (a.getUniqueValue() != null) values.add(clonePlainAttrValue(a.getUniqueValue()));
                    else a.getValues().forEach(v -> values.add(clonePlainAttrValue(v)));
                });
            } else if (intAttrName.getSchemaInfo().type() == SchemaType.DERIVED) {
                Optional.ofNullable(derAttrHandler.getValue(realm, (DerSchema) intAttrName.getSchemaInfo().schema())).ifPresent(v -> {
                    PlainAttrValue av = new PlainAttrValue(); av.setStringValue(v); values.add(av);
                });
            }
        }
        IntValues transformed = new IntValues(schemaType, values);
        for (ItemTransformer t : MappingUtils.getItemTransformers(item, getTransformers(item))) {
            transformed = t.beforePropagation(item, realm, transformed.attrSchemaType(), transformed.values());
        }
        return transformed;
    }

    protected String getManagerValue(final ExternalResource resource, final Provision provision, final Any any) {
        return MappingUtils.getConnObjectKeyItem(provision)
                .map(item -> getSelf().prepareAttr(resource, provision, item, any, null, AccountGetter.DEFAULT, AccountGetter.DEFAULT, PlainAttrGetter.DEFAULT))
                .map(pa -> evaluateNAME(any, provision, pa.connObjectLink()).getNameValue()).orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<String> getConnObjectKeyValue(final Any any, final ExternalResource resource, final Provision provision) {
        return provision.getMapping().getConnObjectKeyItem().flatMap(item -> {
            try {
                IntValues iv = getSelf().getIntValues(resource, provision, item, intAttrNameParser.parse(item.getIntAttrName(), any.getType().getKind()), AttrSchemaType.String, any, AccountGetter.DEFAULT, PlainAttrGetter.DEFAULT);
                return iv.values().isEmpty() ? Optional.empty() : Optional.of(iv.values().getFirst().getValueAsString());
            } catch (ParseException _) { return Optional.empty(); }
        });
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<String> getConnObjectKeyValue(final Realm realm, final ExternalResource resource) {
        if (resource.getOrgUnit() == null) return Optional.empty();
        return resource.getOrgUnit().getConnObjectKeyItem().flatMap(item -> {
            try {
                IntValues iv = getSelf().getIntValues(resource, item, intAttrNameParser.parse(item.getIntAttrName()), AttrSchemaType.String, realm);
                return iv.values().isEmpty() ? Optional.empty() : Optional.of(iv.values().getFirst().getValueAsString());
            } catch (ParseException _) { return Optional.empty(); }
        });
    }

    @Override
    public void setIntValues(final Item item, final Attribute attr, final AnyTO anyTO) {
        List<Object> values = Optional.ofNullable(attr).map(Attribute::getValue).orElseGet(List::of);
        for (ItemTransformer t : MappingUtils.getItemTransformers(item, getTransformers(item))) values = t.beforePull(item, anyTO, values);

        try {
            IntAttrName ina = intAttrNameParser.parse(item.getIntAttrName(), AnyTypeKind.fromTOClass(anyTO.getClass()));
            if (ina.getField() != null && !values.isEmpty() && values.getFirst() != null) {
                switch (ina.getField()) {
                    case "password" -> { if (anyTO instanceof UserTO u) u.setPassword(ConnObjectUtils.getPassword(values.getFirst())); }
                    case "username" -> { if (anyTO instanceof UserTO u) u.setUsername(values.getFirst().toString()); }
                    case "name" -> { if (anyTO instanceof GroupTO g) g.setName(values.getFirst().toString()); else if (anyTO instanceof AnyObjectTO a) a.setName(values.getFirst().toString()); }
                    case "mustChangePassword" -> { if (anyTO instanceof UserTO u) u.setMustChangePassword(BooleanUtils.toBoolean(values.getFirst().toString())); }
                    case U_MANAGER -> anyTO.setUManager(values.getFirst().toString());
                    case G_MANAGER -> anyTO.setGManager(values.getFirst().toString());
                }
            } else if (ina.getSchemaInfo() != null && attr != null) {
                Attr attrTO = new Attr(); attrTO.setSchema(ina.getSchemaInfo().schema().getKey());
                values.forEach(v -> attrTO.getValues().add(v.toString()));

                if (anyTO instanceof GroupableRelatableTO g && ina.getMembership() != null) {
                    groupDAO.findByName(ina.getMembership()).ifPresent(group -> {
                        MembershipTO m = g.getMembership(group.getKey()).orElseGet(() -> {
                            MembershipTO nm = new MembershipTO.Builder(group.getKey()).build(); g.getMemberships().add(nm); return nm;
                        });
                        if (ina.getSchemaInfo().type() == SchemaType.PLAIN) m.getPlainAttrs().add(attrTO);
                        else if (ina.getSchemaInfo().type() == SchemaType.DERIVED) m.getDerAttrs().add(attrTO);
                    });
                } else {
                    if (ina.getSchemaInfo().type() == SchemaType.PLAIN) anyTO.getPlainAttrs().add(attrTO);
                    else if (ina.getSchemaInfo().type() == SchemaType.DERIVED) anyTO.getDerAttrs().add(attrTO);
                }
            }
        } catch (ParseException _) { /* Intentionally empty */ }
    }

    @Override
    public void setIntValues(final Item item, final Attribute attr, final RealmTO realmTO) {
        List<Object> values = Optional.ofNullable(attr).map(Attribute::getValue).orElseGet(List::of);
        for (ItemTransformer t : MappingUtils.getItemTransformers(item, getTransformers(item))) values = t.beforePull(item, realmTO, values);

        try {
            IntAttrName ina = intAttrNameParser.parse(item.getIntAttrName());
            if (ina.getField() != null) {
                if ("name".equals(ina.getField())) realmTO.setName(values.isEmpty() || values.getFirst() == null ? null : values.getFirst().toString());
                else if ("fullpath".equals(ina.getField())) {
                    String path = values.getFirst().toString();
                    realmSearchDAO.findByFullPath(StringUtils.substringBeforeLast(path, "/")).ifPresent(p -> realmTO.setParent(p.getFullPath()));
                }
            } else if (ina.getSchemaInfo() != null && attr != null) {
                Attr attrTO = new Attr(); attrTO.setSchema(ina.getSchemaInfo().schema().getKey());
                values.forEach(v -> attrTO.getValues().add(v.toString()));
                if (ina.getSchemaInfo().type() == SchemaType.PLAIN) realmTO.getPlainAttrs().add(attrTO);
                else if (ina.getSchemaInfo().type() == SchemaType.DERIVED) realmTO.getDerAttrs().add(attrTO);
            }
        } catch (ParseException _) { /* Intentionally empty */ }
    }

    @Override
    public boolean hasMustChangePassword(final Provision provision) {
        return Optional.ofNullable(provision.getMapping()).map(m -> m.getItems().stream().anyMatch(i -> MUST_CHANGE_PWD.equals(i.getIntAttrName()))).orElse(false);
    }
}