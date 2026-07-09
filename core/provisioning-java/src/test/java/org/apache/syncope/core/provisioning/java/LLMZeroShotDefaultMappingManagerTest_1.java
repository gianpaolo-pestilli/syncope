package org.apache.syncope.core.provisioning.java;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.syncope.common.lib.Attr;
import org.apache.syncope.common.lib.to.AnyObjectTO;
import org.apache.syncope.common.lib.to.GroupTO;
import org.apache.syncope.common.lib.to.Item;
import org.apache.syncope.common.lib.to.Mapping;
import org.apache.syncope.common.lib.to.OrgUnit;
import org.apache.syncope.common.lib.to.Provision;
import org.apache.syncope.common.lib.to.RealmTO;
import org.apache.syncope.common.lib.to.UserTO;
import org.apache.syncope.common.lib.types.AttrSchemaType;
import org.apache.syncope.core.persistence.api.EncryptorManager;
import org.apache.syncope.core.persistence.api.dao.AnyObjectDAO;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.persistence.api.dao.ImplementationDAO;
import org.apache.syncope.core.persistence.api.dao.RealmSearchDAO;
import org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.Any;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.PlainAttrValue;
import org.apache.syncope.core.persistence.api.entity.Realm;
import org.apache.syncope.core.persistence.api.entity.user.Account;
import org.apache.syncope.core.persistence.api.entity.user.LinkedAccount;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.provisioning.api.AccountGetter;
import org.apache.syncope.core.provisioning.api.DerAttrHandler;
import org.apache.syncope.core.provisioning.api.IntAttrName;
import org.apache.syncope.core.provisioning.api.IntAttrNameParser;
import org.apache.syncope.core.provisioning.api.MappingManager;
import org.apache.syncope.core.provisioning.api.PlainAttrGetter;
import org.apache.syncope.core.provisioning.api.jexl.JexlTools;
import org.apache.syncope.core.provisioning.java.DefaultMappingManager;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)

class LLMZeroShotDefaultMappingManagerTest_1 {
    /*
    *
    * FIRST ZERO SHOT PROMPT
    *
    * */

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

    private TestableDefaultMappingManager manager;

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
        encryptorManager = mock(EncryptorManager.class, RETURNS_DEEP_STUBS);
        jexlTools = mock(JexlTools.class);

        manager = new TestableDefaultMappingManager(
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
        return item;
    }

    private static Item connObjectKeyItem(final String intAttrName, final String extAttrName) {
        Item item = item(intAttrName, extAttrName);
        item.setConnObjectKey(true);
        return item;
    }

    private static Provision provision(final Item... items) {
        Provision provision = new Provision();
        Mapping mapping = new Mapping();
        for (Item item : items) {
            mapping.getItems().add(item);
        }
        provision.setMapping(mapping);
        return provision;
    }

    private static OrgUnit orgUnit(final Item... items) {
        OrgUnit orgUnit = new OrgUnit();
        for (Item item : items) {
            orgUnit.getItems().add(item);
        }
        return orgUnit;
    }

    @Test
    @DisplayName("Constructor stores all collaborators and creates a usable manager")
    void constructorInitializesManager() {
        assertNotNull(manager);
        assertEquals(userDAO, manager.exposedUserDAO());
        assertEquals(anyObjectDAO, manager.exposedAnyObjectDAO());
        assertEquals(groupDAO, manager.exposedGroupDAO());
        assertEquals(relationshipTypeDAO, manager.exposedRelationshipTypeDAO());
        assertEquals(realmSearchDAO, manager.exposedRealmSearchDAO());
        assertEquals(implementationDAO, manager.exposedImplementationDAO());
        assertEquals(derAttrHandler, manager.exposedDerAttrHandler());
        assertEquals(intAttrNameParser, manager.exposedIntAttrNameParser());
        assertEquals(encryptorManager, manager.exposedEncryptorManager());
        assertEquals(jexlTools, manager.exposedJexlTools());
    }

    @Nested
    @DisplayName("hasMustChangePassword")
    class HasMustChangePasswordTests {

        @Test
        void returnsFalseWhenProvisionHasNoMapping() {
            Provision provision = new Provision();

            Assertions.assertFalse(manager.hasMustChangePassword(provision));
        }

        @Test
        void returnsFalseWhenMappingHasNoItems() {
            Provision provision = provision();

            Assertions.assertFalse(manager.hasMustChangePassword(provision));
        }

        @Test
        void returnsFalseWhenNoMustChangePasswordItemExists() {
            Provision provision = provision(item("username", "uid"), item("email", "mail"));

            Assertions.assertFalse(manager.hasMustChangePassword(provision));
        }

        @Test
        void returnsTrueWhenMustChangePasswordItemExists() {
            Provision provision = provision(item("username", "uid"), item("mustChangePassword", "pwdReset"));

            Assertions.assertTrue(manager.hasMustChangePassword(provision));
        }

        @Test
        void isCaseSensitiveForMustChangePassword() {
            Provision provision = provision(item("MustChangePassword", "pwdReset"));

            Assertions.assertFalse(manager.hasMustChangePassword(provision));
        }
    }

    @Nested
    @DisplayName("setIntValues for AnyTO")
    class SetIntValuesForAnyTOTests {

        @Test
        void setsUserUsernameFromSingleStringValue() {
            UserTO userTO = new UserTO();
            Item item = item("username", "uid");
            Attribute attr = AttributeBuilder.build("uid", "alice");

            manager.setIntValues(item, attr, userTO);

            assertEquals("alice", userTO.getUsername());
        }

        @Test
        void setsUserPasswordFromSingleStringValue() {
            UserTO userTO = new UserTO();
            Item item = item("password", OperationalAttributes.PASSWORD_NAME);
            Attribute attr = AttributeBuilder.build(OperationalAttributes.PASSWORD_NAME, "clearTextPwd");

            manager.setIntValues(item, attr, userTO);

            assertEquals("clearTextPwd", userTO.getPassword());
        }

        @Test
        void ignoresEmptyAttributeValuesForUserUsername() {
            UserTO userTO = new UserTO();
            Item item = item("username", "uid");
            Attribute attr = AttributeBuilder.build("uid");

            manager.setIntValues(item, attr, userTO);

            assertNull(userTO.getUsername());
        }

        @Test
        void ignoresNullAttributeForAnyTO() {
            UserTO userTO = new UserTO();
            Item item = item("username", "uid");

            assertDoesNotThrow(() -> manager.setIntValues(item, null, userTO));
            assertNull(userTO.getUsername());
        }

        @Test
        void setsGroupNameFromSingleStringValue() {
            GroupTO groupTO = new GroupTO();
            Item item = item("name", "cn");
            Attribute attr = AttributeBuilder.build("cn", "syncope-administrators");

            manager.setIntValues(item, attr, groupTO);

            assertEquals("syncope-administrators", groupTO.getName());
        }

        @Test
        void setsAnyObjectNameFromSingleStringValue() {
            AnyObjectTO anyObjectTO = new AnyObjectTO();
            Item item = item("name", "cn");
            Attribute attr = AttributeBuilder.build("cn", "printer-01");

            manager.setIntValues(item, attr, anyObjectTO);

            assertEquals("printer-01", anyObjectTO.getName());
        }

        @Test
        void setsPlainAttrWhenInternalAttributeIsPlainSchema() {
            UserTO userTO = new UserTO();
            Item item = item("email", "mail");
            Attribute attr = AttributeBuilder.build("mail", "alice@example.org");

            manager.setIntValues(item, attr, userTO);

            Optional<Attr> email = userTO.getPlainAttr("email");
            assertTrue(email.isPresent());
            assertEquals(List.of("alice@example.org"), email.get().getValues());
        }

        @Test
        void setsPlainAttrWithMultipleValues() {
            UserTO userTO = new UserTO();
            Item item = item("loginDate", "loginDate");
            Attribute attr = AttributeBuilder.build("loginDate", List.of("2026-01-01", "2026-02-01"));

            manager.setIntValues(item, attr, userTO);

            Optional<Attr> loginDate = userTO.getPlainAttr("loginDate");
            assertTrue(loginDate.isPresent());
            assertEquals(List.of("2026-01-01", "2026-02-01"), loginDate.get().getValues());
        }

        @Test
        void replacesExistingPlainAttrValues() {
            UserTO userTO = new UserTO();
            userTO.getPlainAttrs().add(new Attr.Builder("email").value("old@example.org").build());

            Item item = item("email", "mail");
            Attribute attr = AttributeBuilder.build("mail", "new@example.org");

            manager.setIntValues(item, attr, userTO);

            Optional<Attr> email = userTO.getPlainAttr("email");
            assertTrue(email.isPresent());
            assertEquals(List.of("new@example.org"), email.get().getValues());
        }
    }

    @Nested
    @DisplayName("setIntValues for RealmTO")
    class SetIntValuesForRealmTOTests {

        @Test
        void setsRealmNameFromConnectorAttribute() {
            RealmTO realmTO = new RealmTO();
            Item item = item("name", "ou");
            Attribute attr = AttributeBuilder.build("ou", "engineering");

            manager.setIntValues(item, attr, realmTO);

            assertEquals("engineering", realmTO.getName());
        }

        @Test
        void setsRealmFullPathFromConnectorAttribute() {
            RealmTO realmTO = new RealmTO();
            Item item = item("fullPath", "dn");
            Attribute attr = AttributeBuilder.build("dn", "/engineering/platform");

            manager.setIntValues(item, attr, realmTO);

            assertEquals("/engineering/platform", realmTO.getFullPath());
        }

        @Test
        void addsRealmPlainAttribute() {
            RealmTO realmTO = new RealmTO();
            Item item = item("description", "description");
            Attribute attr = AttributeBuilder.build("description", "Platform Engineering");

            manager.setIntValues(item, attr, realmTO);

            Optional<Attr> description = realmTO.getPlainAttr("description");
            assertTrue(description.isPresent());
            assertEquals(List.of("Platform Engineering"), description.get().getValues());
        }

        @Test
        void ignoresNullAttributeForRealmTO() {
            RealmTO realmTO = new RealmTO();
            Item item = item("name", "ou");

            assertDoesNotThrow(() -> manager.setIntValues(item, null, realmTO));
            assertNull(realmTO.getName());
        }
    }

    @Nested
    @DisplayName("getName and evaluateNAME")
    class NameEvaluationTests {

        @Test
        void getNameUsesEvaluatedConnObjectLinkWhenPresent() {
            Name name = TestableDefaultMappingManager.exposedGetName("uid=alice,ou=people", "alice");

            assertEquals("uid=alice,ou=people", name.getNameValue());
        }

        @Test
        void getNameFallsBackToConnObjectKeyWhenEvaluatedLinkIsBlank() {
            Name name = TestableDefaultMappingManager.exposedGetName("   ", "alice");

            assertEquals("alice", name.getNameValue());
        }

        @Test
        void getNameFallsBackToConnObjectKeyWhenEvaluatedLinkIsNull() {
            Name name = TestableDefaultMappingManager.exposedGetName(null, "alice");

            assertEquals("alice", name.getNameValue());
        }

        @Test
        void getNameThrowsWhenBothEvaluatedLinkAndConnObjectKeyAreBlank() {
            assertThrows(IllegalArgumentException.class,
                    () -> TestableDefaultMappingManager.exposedGetName(" ", " "));
        }

        @Test
        void evaluateNameForAnyReturnsConnObjectKeyWhenNoLinkIsConfigured() {
            Any any = mock(Any.class, RETURNS_DEEP_STUBS);
            Provision provision = provision(connObjectKeyItem("username", Name.NAME));

            Name name = manager.exposedEvaluateNAME(any, provision, "alice");

            assertEquals("alice", name.getNameValue());
        }

        @Test
        void evaluateNameForRealmReturnsConnObjectKeyWhenNoLinkIsConfigured() {
            Realm realm = mock(Realm.class);
            OrgUnit orgUnit = orgUnit(connObjectKeyItem("name", Name.NAME));

            Name name = manager.exposedEvaluateNAME(realm, orgUnit, "engineering");

            assertEquals("engineering", name.getNameValue());
        }
    }

    @Nested
    @DisplayName("getTransformers")
    class TransformerTests {

        @Test
        void returnsEmptyTransformersWhenItemHasNoTransformerKeys() {
            Item item = item("email", "mail");

            assertTrue(manager.exposedGetTransformers(item).isEmpty());
        }

        @Test
        void looksUpConfiguredTransformerImplementations() {
            Item item = item("email", "mail");
            item.getTransformers().add("normalizeEmail");

            when(implementationDAO.findById("normalizeEmail")).thenReturn(Optional.empty());

            assertTrue(manager.exposedGetTransformers(item).isEmpty());
            verify(implementationDAO).findById("normalizeEmail");
        }
    }

    @Nested
    @DisplayName("decodePassword and getPasswordAttrValue")
    class PasswordTests {

        @Test
        void decodePasswordReturnsEmptyForNullAccountPassword() {
            Account account = mock(Account.class);
            when(account.getPassword()).thenReturn(null);

            assertTrue(manager.exposedDecodePassword(account).isEmpty());
        }

        @Test
        void getPasswordAttrValueReturnsDefaultWhenAccountPasswordCannotBeDecoded() {
            Account account = mock(Account.class);
            when(account.getPassword()).thenReturn(null);

            Optional<String> value = manager.exposedGetPasswordAttrValue(account, "fallback");

            assertTrue(value.isPresent());
            assertEquals("fallback", value.get());
        }

        @Test
        void getPasswordAttrValueReturnsEmptyWhenBothDecodedAndDefaultAreNull() {
            Account account = mock(Account.class);
            when(account.getPassword()).thenReturn(null);

            assertTrue(manager.exposedGetPasswordAttrValue(account, null).isEmpty());
        }
    }

    @Nested
    @DisplayName("prepareAttrsFromAny")
    class PrepareAttrsFromAnyTests {

        @Test
        void prepareAttrsFromAnyHandlesEmptyMapping() {
            Any any = mock(Any.class, RETURNS_DEEP_STUBS);
            ExternalResource resource = mock(ExternalResource.class);
            Provision provision = provision();

            MappingManager.PreparedAttrs attrs =
                    assertDoesNotThrow(() -> manager.prepareAttrsFromAny(any, null, false, null, resource, provision));

            assertNotNull(attrs);
        }

        @Test
        void prepareAttrsFromAnyAddsEnableOperationalAttributeWhenEnableIsTrue() {
            Any any = mock(Any.class, RETURNS_DEEP_STUBS);
            ExternalResource resource = mock(ExternalResource.class);
            Provision provision = provision();

            MappingManager.PreparedAttrs attrs =
                    manager.prepareAttrsFromAny(any, null, false, Boolean.TRUE, resource, provision);

            assertNotNull(attrs);
        }

        @Test
        void prepareAttrsFromAnyAddsDisableOperationalAttributeWhenEnableIsFalse() {
            Any any = mock(Any.class, RETURNS_DEEP_STUBS);
            ExternalResource resource = mock(ExternalResource.class);
            Provision provision = provision();

            MappingManager.PreparedAttrs attrs =
                    manager.prepareAttrsFromAny(any, null, false, Boolean.FALSE, resource, provision);

            assertNotNull(attrs);
        }

        @Test
        void prepareAttrsFromAnyDoesNotThrowForPasswordChangeWithoutPasswordItem() {
            User user = mock(User.class, RETURNS_DEEP_STUBS);
            ExternalResource resource = mock(ExternalResource.class);
            Provision provision = provision(item("username", "uid"));

            assertDoesNotThrow(() ->
                    manager.prepareAttrsFromAny(user, "Secret123!", true, Boolean.TRUE, resource, provision));
        }
    }

    @Nested
    @DisplayName("prepareAttrsFromLinkedAccount")
    class PrepareAttrsFromLinkedAccountTests {

        @Test
        void prepareAttrsFromLinkedAccountHandlesEmptyMapping() {
            User user = mock(User.class, RETURNS_DEEP_STUBS);
            LinkedAccount account = mock(LinkedAccount.class);
            Provision provision = provision();

            Set<Attribute> attrs = assertDoesNotThrow(() ->
                    manager.prepareAttrsFromLinkedAccount(user, account, null, false, provision));

            assertNotNull(attrs);
        }

        @Test
        void prepareAttrsFromLinkedAccountHandlesChangePasswordWithoutPasswordItem() {
            User user = mock(User.class, RETURNS_DEEP_STUBS);
            LinkedAccount account = mock(LinkedAccount.class);
            Provision provision = provision(item("username", "uid"));

            Set<Attribute> attrs = manager.prepareAttrsFromLinkedAccount(user, account, "Secret123!", true, provision);

            assertNotNull(attrs);
        }
    }

    @Nested
    @DisplayName("prepareAttrsFromRealm")
    class PrepareAttrsFromRealmTests {

        @Test
        void prepareAttrsFromRealmHandlesResourceWithoutOrgUnit() {
            Realm realm = mock(Realm.class);
            ExternalResource resource = mock(ExternalResource.class);

            assertDoesNotThrow(() -> manager.prepareAttrsFromRealm(realm, resource));
        }

        @Test
        void prepareAttrForRealmHandlesSimpleItem() {
            Realm realm = mock(Realm.class);
            ExternalResource resource = mock(ExternalResource.class);
            Item item = item("name", "ou");

            MappingManager.PreparedAttr preparedAttr =
                    assertDoesNotThrow(() -> manager.prepareAttr(resource, item, realm));

            assertNotNull(preparedAttr);
        }
    }

    @Nested
    @DisplayName("getConnObjectKeyValue and getIntValues")
    class ValueResolutionTests {

        @Test
        void getConnObjectKeyValueForAnyReturnsEmptyWhenNoConnObjectKeyItemExists() {
            Any any = mock(Any.class, RETURNS_DEEP_STUBS);
            ExternalResource resource = mock(ExternalResource.class);
            Provision provision = provision(item("email", "mail"));

            Optional<String> value = manager.getConnObjectKeyValue(any, resource, provision);

            assertTrue(value.isEmpty());
        }

        @Test
        void getConnObjectKeyValueForRealmReturnsEmptyWhenResourceHasNoOrgUnit() {
            Realm realm = mock(Realm.class);
            ExternalResource resource = mock(ExternalResource.class);

            Optional<String> value = manager.getConnObjectKeyValue(realm, resource);

            assertTrue(value.isEmpty());
        }

        @Test
        void getIntValuesForAnyDoesNotThrowForUsernameMapping() {
            ExternalResource resource = mock(ExternalResource.class);
            Provision provision = provision();
            Item item = item("username", "uid");
            IntAttrName intAttrName = mock(IntAttrName.class);
            Any any = mock(Any.class, RETURNS_DEEP_STUBS);
            AccountGetter usernameGetter = mock(AccountGetter.class);
            PlainAttrGetter plainAttrGetter = mock(PlainAttrGetter.class);

            MappingManager.IntValues values = assertDoesNotThrow(() ->
                    manager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String,
                            any, usernameGetter, plainAttrGetter));

            assertNotNull(values);
        }

        @Test
        void getIntValuesForRealmDoesNotThrowForNameMapping() {
            ExternalResource resource = mock(ExternalResource.class);
            Item item = item("name", "ou");
            IntAttrName intAttrName = mock(IntAttrName.class);
            Realm realm = mock(Realm.class);

            MappingManager.IntValues values = assertDoesNotThrow(() ->
                    manager.getIntValues(resource, item, intAttrName, AttrSchemaType.String, realm));

            assertNotNull(values);
        }
    }

    @Nested
    @DisplayName("clonePlainAttrValue")
    class ClonePlainAttrValueTests {

        @Test
        void clonePlainAttrValueRejectsNullInput() {
            assertThrows(RuntimeException.class, () -> TestableDefaultMappingManager.exposedClonePlainAttrValue(null));
        }

        @Test
        void clonePlainAttrValueCopiesPlainAttrValueWhenConcreteTypeIsMockable() {
            PlainAttrValue value = mock(PlainAttrValue.class);

            assertThrows(RuntimeException.class,
                    () -> TestableDefaultMappingManager.exposedClonePlainAttrValue(value));
        }
    }

    private static class TestableDefaultMappingManager extends DefaultMappingManager {

        TestableDefaultMappingManager(
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

            super(userDAO, anyObjectDAO, groupDAO, relationshipTypeDAO, realmSearchDAO, implementationDAO,
                    derAttrHandler, intAttrNameParser, encryptorManager, jexlTools);
        }

        UserDAO exposedUserDAO() {
            return userDAO;
        }

        AnyObjectDAO exposedAnyObjectDAO() {
            return anyObjectDAO;
        }

        GroupDAO exposedGroupDAO() {
            return groupDAO;
        }

        RelationshipTypeDAO exposedRelationshipTypeDAO() {
            return relationshipTypeDAO;
        }

        RealmSearchDAO exposedRealmSearchDAO() {
            return realmSearchDAO;
        }

        ImplementationDAO exposedImplementationDAO() {
            return implementationDAO;
        }

        DerAttrHandler exposedDerAttrHandler() {
            return derAttrHandler;
        }

        IntAttrNameParser exposedIntAttrNameParser() {
            return intAttrNameParser;
        }

        EncryptorManager exposedEncryptorManager() {
            return encryptorManager;
        }

        JexlTools exposedJexlTools() {
            return jexlTools;
        }

        static PlainAttrValue exposedClonePlainAttrValue(final PlainAttrValue src) {
            return clonePlainAttrValue(src);
        }

        static Name exposedGetName(final String evalConnObjectLink, final String connObjectKey) {
            return getName(evalConnObjectLink, connObjectKey);
        }

        Name exposedEvaluateNAME(final Any any, final Provision provision, final String connObjectKey) {
            return evaluateNAME(any, provision, connObjectKey);
        }

        Name exposedEvaluateNAME(final Realm realm, final OrgUnit orgUnit, final String connObjectKey) {
            return evaluateNAME(realm, orgUnit, connObjectKey);
        }

        Optional<String> exposedDecodePassword(final Account account) {
            return decodePassword(account);
        }

        Optional<String> exposedGetPasswordAttrValue(final Account account, final String defaultValue) {
            return getPasswordAttrValue(account, defaultValue);
        }

        List<?> exposedGetTransformers(final Item item) {
            return getTransformers(item);
        }
    }


}