package org.apache.syncope.core.provisioning.java;/*
 * Black-box JUnit 5 tests for DefaultMappingManager.
 *
 * Notes:
 * - These tests intentionally avoid inspecting DefaultMappingManager internals.
 * - They use only public API behavior plus constructor/signature-level interactions.
 * - PreparedAttrs is deliberately asserted only as non-null: no assumed attrs(), attributes(),
 *   key(), name(), or other record component accessor is used.
 */

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.syncope.common.lib.Attr;
import org.apache.syncope.common.lib.to.AnyObjectTO;
import org.apache.syncope.common.lib.to.GroupTO;
import org.apache.syncope.common.lib.to.Item;
import org.apache.syncope.common.lib.to.Mapping;
import org.apache.syncope.common.lib.to.Provision;
import org.apache.syncope.common.lib.to.RealmTO;
import org.apache.syncope.common.lib.to.UserTO;
import org.apache.syncope.common.lib.types.AttrSchemaType;
import org.apache.syncope.common.lib.types.MappingPurpose;
import org.apache.syncope.core.persistence.api.EncryptorManager;
import org.apache.syncope.core.persistence.api.dao.AnyObjectDAO;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.persistence.api.dao.ImplementationDAO;
import org.apache.syncope.core.persistence.api.dao.RealmSearchDAO;
import org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.Any;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.Realm;
import org.apache.syncope.core.persistence.api.entity.user.LinkedAccount;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.provisioning.api.AccountGetter;
import org.apache.syncope.core.provisioning.api.DerAttrHandler;
import org.apache.syncope.core.provisioning.api.IntAttrNameParser;
import org.apache.syncope.core.provisioning.api.MappingManager;
import org.apache.syncope.core.provisioning.api.PlainAttrGetter;
import org.apache.syncope.core.provisioning.api.jexl.JexlTools;
import org.apache.syncope.core.provisioning.java.DefaultMappingManager;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LLMZeroShotDefaultMappingManagerTest_3 {

    private DefaultMappingManager manager;

    private UserDAO userDAO;
    private AnyObjectDAO anyObjectDAO;
    private GroupDAO groupDAO;
    private RelationshipTypeDAO relationshipTypeDAO;
    private RealmSearchDAO realmSearchDAO;
    private ImplementationDAO implementationDAO;
    private DerAttrHandler derAttrHandler;
    private IntAttrNameParser intAttrNameParser;
    private EncryptorManager encryptorManager;
    private JexlTools jexlTools;

    @BeforeEach
    void setUp() {
        userDAO = mock(UserDAO.class);
        anyObjectDAO = mock(AnyObjectDAO.class);
        groupDAO = mock(GroupDAO.class);
        relationshipTypeDAO = mock(RelationshipTypeDAO.class);
        realmSearchDAO = mock(RealmSearchDAO.class);
        implementationDAO = mock(ImplementationDAO.class);
        derAttrHandler = mock(DerAttrHandler.class);
        intAttrNameParser = mock(IntAttrNameParser.class);
        encryptorManager = mock(EncryptorManager.class);
        jexlTools = mock(JexlTools.class);

        manager = new DefaultMappingManager(
                userDAO,
                anyObjectDAO,
                groupDAO,
                relationshipTypeDAO,
                realmSearchDAO,
                implementationDAO,
                derAttrHandler,
                intAttrNameParser,
                encryptorManager,
                jexlTools);
    }

    private static Item item(final String intAttrName, final String extAttrName) {
        Item item = new Item();
        item.setIntAttrName(intAttrName);
        item.setExtAttrName(extAttrName);
        item.setPurpose(MappingPurpose.BOTH);
        return item;
    }

    private static Item passwordItem(final String intAttrName, final String extAttrName) {
        Item item = item(intAttrName, extAttrName);
        item.setPassword(true);
        return item;
    }

    private static Item connObjectKeyItem(final String intAttrName, final String extAttrName) {
        Item item = item(intAttrName, extAttrName);
        item.setConnObjectKey(true);
        return item;
    }

    private static Provision provisionWith(final Item... items) {
        Mapping mapping = new Mapping();

        for (Item item : items) {
            if (item.isConnObjectKey()) {
                mapping.setConnObjectKeyItem(item);
            } else {
                mapping.add(item);
            }
        }

        Provision provision = new Provision();
        provision.setMapping(mapping);
        return provision;
    }

    private static Attr attrFrom(final Set<Attr> attrs, final String schema) {
        return attrs.stream()
                .filter(attr -> schema.equals(attr.getSchema()))
                .findFirst()
                .orElseThrow();
    }

    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("01 - constructor accepts mocked collaborators")
    void constructorAcceptsMockedCollaborators() {
        assertDoesNotThrow(() -> new DefaultMappingManager(
                userDAO,
                anyObjectDAO,
                groupDAO,
                relationshipTypeDAO,
                realmSearchDAO,
                implementationDAO,
                derAttrHandler,
                intAttrNameParser,
                encryptorManager,
                jexlTools));
    }

    // ---------------------------------------------------------------------
    // hasMustChangePassword
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("02 - hasMustChangePassword returns false for provision without mapping")
    void hasMustChangePasswordReturnsFalseForProvisionWithoutMapping() {
        Provision provision = new Provision();

        assertFalse(manager.hasMustChangePassword(provision));
    }

    @Test
    @DisplayName("03 - hasMustChangePassword returns false for empty mapping")
    void hasMustChangePasswordReturnsFalseForEmptyMapping() {
        Provision provision = provisionWith();

        assertFalse(manager.hasMustChangePassword(provision));
    }

    @Test
    @DisplayName("04 - hasMustChangePassword detects mustChangePassword mapping")
    void hasMustChangePasswordDetectsMustChangePasswordMapping() {
        Provision provision = provisionWith(item("mustChangePassword", "pwdExpired"));

        assertTrue(manager.hasMustChangePassword(provision));
    }

    @Test
    @DisplayName("05 - hasMustChangePassword ignores generic password mapping")
    void hasMustChangePasswordIgnoresGenericPasswordMapping() {
        Provision provision = provisionWith(passwordItem("password", OperationalAttributes.PASSWORD_NAME));

        assertFalse(manager.hasMustChangePassword(provision));
    }

    // ---------------------------------------------------------------------
    // setIntValues - AnyTO / UserTO / GroupTO / AnyObjectTO
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("06 - setIntValues maps scalar external value to UserTO plain attribute")
    void setIntValuesMapsScalarExternalValueToUserTOPlainAttribute() {
        UserTO userTO = new UserTO();

        manager.setIntValues(
                item("email", "mail"),
                AttributeBuilder.build("mail", "user@example.org"),
                userTO);

        Attr email = attrFrom(userTO.getPlainAttrs(), "email");
        assertEquals(List.of("user@example.org"), email.getValues());
    }

    @Test
    @DisplayName("07 - setIntValues maps multi-valued external attribute to UserTO plain attribute")
    void setIntValuesMapsMultiValuedExternalAttributeToUserTOPlainAttribute() {
        UserTO userTO = new UserTO();

        manager.setIntValues(
                item("roles", "groups"),
                AttributeBuilder.build("groups", "dev", "ops"),
                userTO);

        Attr roles = attrFrom(userTO.getPlainAttrs(), "roles");
        assertEquals(List.of("dev", "ops"), roles.getValues());
    }

    @Test
    @DisplayName("08 - setIntValues replaces existing UserTO plain attribute values")
    void setIntValuesReplacesExistingUserTOPlainAttributeValues() {
        UserTO userTO = new UserTO();

        Attr oldEmail = new Attr();
        oldEmail.setSchema("email");
        oldEmail.getValues().add("old@example.org");
        userTO.getPlainAttrs().add(oldEmail);

        manager.setIntValues(
                item("email", "mail"),
                AttributeBuilder.build("mail", "new@example.org"),
                userTO);

        Attr email = attrFrom(userTO.getPlainAttrs(), "email");
        assertEquals(List.of("new@example.org"), email.getValues());
    }

    @Test
    @DisplayName("09 - setIntValues maps empty external values to empty plain attribute")
    void setIntValuesMapsEmptyExternalValuesToEmptyPlainAttribute() {
        UserTO userTO = new UserTO();

        manager.setIntValues(
                item("emptySchema", "empty"),
                AttributeBuilder.build("empty"),
                userTO);

        Attr emptySchema = attrFrom(userTO.getPlainAttrs(), "emptySchema");
        assertTrue(emptySchema.getValues().isEmpty());
    }

    @Test
    @DisplayName("10 - setIntValues maps external value to AnyObjectTO plain attribute")
    void setIntValuesMapsExternalValueToAnyObjectTOPlainAttribute() {
        AnyObjectTO anyObjectTO = new AnyObjectTO();

        manager.setIntValues(
                item("serialNumber", "sn"),
                AttributeBuilder.build("sn", "SN-001"),
                anyObjectTO);

        Attr serialNumber = attrFrom(anyObjectTO.getPlainAttrs(), "serialNumber");
        assertEquals(List.of("SN-001"), serialNumber.getValues());
    }

    @Test
    @DisplayName("11 - setIntValues maps external value to GroupTO plain attribute")
    void setIntValuesMapsExternalValueToGroupTOPlainAttribute() {
        GroupTO groupTO = new GroupTO();

        manager.setIntValues(
                item("description", "description"),
                AttributeBuilder.build("description", "Engineering"),
                groupTO);

        Attr description = attrFrom(groupTO.getPlainAttrs(), "description");
        assertEquals(List.of("Engineering"), description.getValues());
    }

    @Test
    @DisplayName("12 - setIntValues accepts pull JEXL transformer configuration")
    void setIntValuesAcceptsPullJexlTransformerConfiguration() {
        UserTO userTO = new UserTO();
        Item item = item("email", "mail");
        item.setPullJEXLTransformer("value.toLowerCase()");

        assertDoesNotThrow(() -> manager.setIntValues(
                item,
                AttributeBuilder.build("mail", "USER@EXAMPLE.ORG"),
                userTO));

        Attr email = attrFrom(userTO.getPlainAttrs(), "email");
        assertFalse(email.getValues().isEmpty());
    }

    @Test
    @DisplayName("13 - setIntValues rejects null AnyTO")
    void setIntValuesRejectsNullAnyTO() {
        assertThrows(RuntimeException.class, () -> manager.setIntValues(
                item("email", "mail"),
                AttributeBuilder.build("mail", "user@example.org"),
                (UserTO) null));
    }

    @Test
    @DisplayName("14 - setIntValues rejects null Item for AnyTO")
    void setIntValuesRejectsNullItemForAnyTO() {
        UserTO userTO = new UserTO();

        assertThrows(RuntimeException.class, () -> manager.setIntValues(
                null,
                AttributeBuilder.build("mail", "user@example.org"),
                userTO));
    }

    // ---------------------------------------------------------------------
    // setIntValues - RealmTO
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("15 - setIntValues maps scalar external value to RealmTO plain attribute")
    void setIntValuesMapsScalarExternalValueToRealmTOPlainAttribute() {
        RealmTO realmTO = new RealmTO();

        manager.setIntValues(
                item("location", "l"),
                AttributeBuilder.build("l", "EU"),
                realmTO);

        Attr location = attrFrom(realmTO.getPlainAttrs(), "location");
        assertEquals(List.of("EU"), location.getValues());
    }

    @Test
    @DisplayName("16 - setIntValues maps multi-valued external attribute to RealmTO plain attribute")
    void setIntValuesMapsMultiValuedExternalAttributeToRealmTOPlainAttribute() {
        RealmTO realmTO = new RealmTO();

        manager.setIntValues(
                item("tags", "tags"),
                AttributeBuilder.build("tags", "internal", "trusted"),
                realmTO);

        Attr tags = attrFrom(realmTO.getPlainAttrs(), "tags");
        assertEquals(List.of("internal", "trusted"), tags.getValues());
    }

    @Test
    @DisplayName("17 - setIntValues replaces existing RealmTO plain attribute values")
    void setIntValuesReplacesExistingRealmTOPlainAttributeValues() {
        RealmTO realmTO = new RealmTO();

        Attr oldLocation = new Attr();
        oldLocation.setSchema("location");
        oldLocation.getValues().add("OLD");
        realmTO.getPlainAttrs().add(oldLocation);

        manager.setIntValues(
                item("location", "l"),
                AttributeBuilder.build("l", "NEW"),
                realmTO);

        Attr location = attrFrom(realmTO.getPlainAttrs(), "location");
        assertEquals(List.of("NEW"), location.getValues());
    }

    @Test
    @DisplayName("18 - setIntValues rejects null RealmTO")
    void setIntValuesRejectsNullRealmTO() {
        assertThrows(RuntimeException.class, () -> manager.setIntValues(
                item("location", "l"),
                AttributeBuilder.build("l", "EU"),
                (RealmTO) null));
    }

    @Test
    @DisplayName("19 - setIntValues rejects null Item for RealmTO")
    void setIntValuesRejectsNullItemForRealmTO() {
        RealmTO realmTO = new RealmTO();

        assertThrows(RuntimeException.class, () -> manager.setIntValues(
                null,
                AttributeBuilder.build("l", "EU"),
                realmTO));
    }

    // ---------------------------------------------------------------------
    // prepareAttrsFromAny / prepareAttrsFromRealm
    // No assumption on PreparedAttrs record component names.
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("20 - prepareAttrsFromAny returns non-null PreparedAttrs for empty mapping")
    void prepareAttrsFromAnyReturnsNonNullPreparedAttrsForEmptyMapping() {
        Any any = mock(Any.class);
        ExternalResource resource = mock(ExternalResource.class);
        Provision provision = provisionWith();

        MappingManager.PreparedAttrs prepared = Assertions.assertDoesNotThrow(
                () -> manager.prepareAttrsFromAny(
                        any,
                        null,
                        false,
                        null,
                        resource,
                        provision));

        assertNotNull(prepared);
    }

    @Test
    @DisplayName("21 - prepareAttrsFromAny accepts enable flag with empty mapping")
    void prepareAttrsFromAnyAcceptsEnableFlagWithEmptyMapping() {
        Any any = mock(Any.class);
        ExternalResource resource = mock(ExternalResource.class);
        Provision provision = provisionWith();

        MappingManager.PreparedAttrs prepared = Assertions.assertDoesNotThrow(
                () -> manager.prepareAttrsFromAny(
                        any,
                        null,
                        false,
                        Boolean.TRUE,
                        resource,
                        provision));

        assertNotNull(prepared);
    }

    @Test
    @DisplayName("22 - prepareAttrsFromAny accepts password-change request with empty mapping")
    void prepareAttrsFromAnyAcceptsPasswordChangeRequestWithEmptyMapping() {
        Any any = mock(Any.class);
        ExternalResource resource = mock(ExternalResource.class);
        Provision provision = provisionWith();

        MappingManager.PreparedAttrs prepared = Assertions.assertDoesNotThrow(
                () -> manager.prepareAttrsFromAny(
                        any,
                        "clearPassword",
                        true,
                        null,
                        resource,
                        provision));

        assertNotNull(prepared);
    }

    @Test
    @DisplayName("23 - prepareAttrsFromRealm returns non-null PreparedAttrs")
    void prepareAttrsFromRealmReturnsNonNullPreparedAttrs() {
        Realm realm = mock(Realm.class);
        ExternalResource resource = mock(ExternalResource.class);

        MappingManager.PreparedAttrs prepared = Assertions.assertDoesNotThrow(
                () -> manager.prepareAttrsFromRealm(realm, resource));

        assertNotNull(prepared);
    }

    @Test
    @DisplayName("24 - prepareAttrsFromAny rejects null Any")
    void prepareAttrsFromAnyRejectsNullAny() {
        ExternalResource resource = mock(ExternalResource.class);
        Provision provision = provisionWith();

        assertThrows(RuntimeException.class, () -> manager.prepareAttrsFromAny(
                null,
                null,
                false,
                null,
                resource,
                provision));
    }

    // ---------------------------------------------------------------------
    // prepareAttrsFromLinkedAccount
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("25 - prepareAttrsFromLinkedAccount returns non-null attribute set for empty mapping")
    void prepareAttrsFromLinkedAccountReturnsNonNullAttributeSetForEmptyMapping() {
        User user = mock(User.class);
        LinkedAccount linkedAccount = mock(LinkedAccount.class);
        Provision provision = provisionWith();

        Set<Attribute> attrs = Assertions.assertDoesNotThrow(
                () -> manager.prepareAttrsFromLinkedAccount(
                        user,
                        linkedAccount,
                        null,
                        false,
                        provision));

        assertNotNull(attrs);
    }

    @Test
    @DisplayName("26 - prepareAttrsFromLinkedAccount accepts password-change request for empty mapping")
    void prepareAttrsFromLinkedAccountAcceptsPasswordChangeRequestForEmptyMapping() {
        User user = mock(User.class);
        LinkedAccount linkedAccount = mock(LinkedAccount.class);
        Provision provision = provisionWith();

        Set<Attribute> attrs = Assertions.assertDoesNotThrow(
                () -> manager.prepareAttrsFromLinkedAccount(
                        user,
                        linkedAccount,
                        "clearPassword",
                        true,
                        provision));

        assertNotNull(attrs);
    }

    @Test
    @DisplayName("27 - prepareAttrsFromLinkedAccount rejects null linked account")
    void prepareAttrsFromLinkedAccountRejectsNullLinkedAccount() {
        User user = mock(User.class);
        Provision provision = provisionWith();

        assertThrows(RuntimeException.class, () -> manager.prepareAttrsFromLinkedAccount(
                user,
                null,
                null,
                false,
                provision));
    }

    // ---------------------------------------------------------------------
    // prepareAttr / getIntValues error boundaries
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("28 - prepareAttr for Any rejects null Item")
    void prepareAttrForAnyRejectsNullItem() {
        ExternalResource resource = mock(ExternalResource.class);
        Provision provision = provisionWith();
        Any any = mock(Any.class);
        AccountGetter usernameGetter = mock(AccountGetter.class);
        AccountGetter passwordGetter = mock(AccountGetter.class);
        PlainAttrGetter plainAttrGetter = mock(PlainAttrGetter.class);

        assertThrows(RuntimeException.class, () -> manager.prepareAttr(
                resource,
                provision,
                null,
                any,
                null,
                usernameGetter,
                passwordGetter,
                plainAttrGetter));
    }

    @Test
    @DisplayName("29 - prepareAttr for Realm rejects null Item")
    void prepareAttrForRealmRejectsNullItem() {
        Realm realm = mock(Realm.class);
        ExternalResource resource = mock(ExternalResource.class);

        assertThrows(RuntimeException.class, () -> manager.prepareAttr(
                resource,
                null,
                realm));
    }

    @Test
    @DisplayName("30 - getIntValues for Any rejects missing IntAttrName")
    void getIntValuesForAnyRejectsMissingIntAttrName() {
        ExternalResource resource = mock(ExternalResource.class);
        Provision provision = provisionWith(item("email", "mail"));
        Any any = mock(Any.class);
        AccountGetter usernameGetter = mock(AccountGetter.class);
        PlainAttrGetter plainAttrGetter = mock(PlainAttrGetter.class);

        assertThrows(RuntimeException.class, () -> manager.getIntValues(
                resource,
                provision,
                item("email", "mail"),
                null,
                AttrSchemaType.String,
                any,
                usernameGetter,
                plainAttrGetter));
    }

    @Test
    @DisplayName("31 - getIntValues for Realm rejects missing IntAttrName")
    void getIntValuesForRealmRejectsMissingIntAttrName() {
        ExternalResource resource = mock(ExternalResource.class);
        Realm realm = mock(Realm.class);

        assertThrows(RuntimeException.class, () -> manager.getIntValues(
                resource,
                item("name", "ou"),
                null,
                AttrSchemaType.String,
                realm));
    }

    // ---------------------------------------------------------------------
    // Connection object key
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("32 - getConnObjectKeyValue for Any returns empty when no connObjectKey item exists")
    void getConnObjectKeyValueForAnyReturnsEmptyWhenNoConnObjectKeyItemExists() {
        ExternalResource resource = mock(ExternalResource.class);
        Any any = mock(Any.class);
        Provision provision = provisionWith(item("email", "mail"));

        Optional<String> value = Assertions.assertDoesNotThrow(
                () -> manager.getConnObjectKeyValue(any, resource, provision));

        assertNotNull(value);
        assertTrue(value.isEmpty());
    }

    @Test
    @DisplayName("33 - getConnObjectKeyValue for Any handles configured connObjectKey item")
    void getConnObjectKeyValueForAnyHandlesConfiguredConnObjectKeyItem() {
        ExternalResource resource = mock(ExternalResource.class);
        Any any = mock(Any.class);
        Provision provision = provisionWith(connObjectKeyItem("username", "__NAME__"));

        Optional<String> value = Assertions.assertDoesNotThrow(
                () -> manager.getConnObjectKeyValue(any, resource, provision));

        assertNotNull(value);
    }

    @Test
    @DisplayName("34 - getConnObjectKeyValue for Realm returns empty for minimal resource mock")
    void getConnObjectKeyValueForRealmReturnsEmptyForMinimalResourceMock() {
        ExternalResource resource = mock(ExternalResource.class);
        Realm realm = mock(Realm.class);

        Optional<String> value = Assertions.assertDoesNotThrow(
                () -> manager.getConnObjectKeyValue(realm, resource));

        assertNotNull(value);
        assertTrue(value.isEmpty());
    }
}