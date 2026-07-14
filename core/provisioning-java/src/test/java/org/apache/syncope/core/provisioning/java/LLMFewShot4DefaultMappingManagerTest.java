package org.apache.syncope.core.provisioning.java;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
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
import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.syncope.common.lib.types.AttrSchemaType;
import org.apache.syncope.common.lib.types.CipherAlgorithm;
import org.apache.syncope.core.persistence.api.Encryptor;
import org.apache.syncope.core.persistence.api.EncryptorManager;
import org.apache.syncope.core.persistence.api.dao.AnyObjectDAO;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.persistence.api.dao.ImplementationDAO;
import org.apache.syncope.core.persistence.api.dao.RealmSearchDAO;
import org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.Any;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.Implementation;
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
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * JUnit 5 tests for {@link DefaultMappingManager}.
 *
 * Notes:
 * - Protected methods are exposed through the local TestableDefaultMappingManager subclass.
 * - Reflection helpers are used only for MappingManager.IntValues and MappingManager.PreparedAttr accessors,
 *   because Syncope versions may expose accessor names differently.
 * - doReturn(...).when(...) is used for ImplementationDAO.findById(...) to avoid Mockito generic inference issues.
 */
@Disabled
class LLMFewShot4DefaultMappingManagerTest {

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

            super(
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

        Optional<String> exposedDecodePassword(final Account account) {
            return decodePassword(account);
        }

        Optional<String> exposedGetPasswordAttrValue(final Account account, final String defaultValue) {
            return getPasswordAttrValue(account, defaultValue);
        }

        List<Implementation> exposedGetTransformers(final Item item) {
            return getTransformers(item);
        }

        Name exposedEvaluateNAME(final Any any, final Provision provision, final String connObjectKey) {
            return evaluateNAME(any, provision, connObjectKey);
        }

        Name exposedEvaluateNAME(final Realm realm, final OrgUnit orgUnit, final String connObjectKey) {
            return evaluateNAME(realm, orgUnit, connObjectKey);
        }

        static Name exposedGetName(final String evaluatedConnObjectLink, final String connObjectKey) {
            return getName(evaluatedConnObjectLink, connObjectKey);
        }

        static Optional<String> exposedProcessPreparedAttr(
                final MappingManager.PreparedAttr preparedAttr,
                final Set<Attribute> attributes) {

            return processPreparedAttr(preparedAttr, attributes);
        }

        static PlainAttrValue exposedClonePlainAttrValue(final PlainAttrValue src) {
            return clonePlainAttrValue(src);
        }
    }

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

    @SuppressWarnings("unchecked")
    private static List<PlainAttrValue> intValues(final MappingManager.IntValues result) throws Exception {
        for (String accessor : List.of("values", "getValues")) {
            try {
                Method method = result.getClass().getMethod(accessor);
                return (List<PlainAttrValue>) method.invoke(result);
            } catch (NoSuchMethodException ignored) {
                // Try the next known accessor name.
            }
        }

        fail("Unable to read MappingManager.IntValues values");
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private static List<Object> preparedValues(final MappingManager.PreparedAttr preparedAttr) throws Exception {
        for (String accessor : List.of("values", "getValues")) {
            try {
                Method method = preparedAttr.getClass().getMethod(accessor);
                return (List<Object>) method.invoke(preparedAttr);
            } catch (NoSuchMethodException ignored) {
                // Try the next known accessor name.
            }
        }

        fail("Unable to read MappingManager.PreparedAttr values");
        return List.of();
    }

    private static String preparedExtAttrName(final MappingManager.PreparedAttr preparedAttr) throws Exception {
        for (String accessor : List.of("extAttrName", "getExtAttrName", "name", "getName")) {
            try {
                Method method = preparedAttr.getClass().getMethod(accessor);
                return String.valueOf(method.invoke(preparedAttr));
            } catch (NoSuchMethodException ignored) {
                // Try the next known accessor name.
            }
        }

        fail("Unable to read MappingManager.PreparedAttr external attribute name");
        return null;
    }

    private static MappingManager.PreparedAttr newPreparedAttr(final String name, final List<Object> values)
            throws Exception {

        Constructor<MappingManager.PreparedAttr> constructor =
                MappingManager.PreparedAttr.class.getDeclaredConstructor(String.class, List.class);
        constructor.setAccessible(true);
        return constructor.newInstance(name, values);
    }

    // ==================== TC1: Get int values for Any key field ====================
    // Tests that getIntValues returns the Any key when the internal attribute name is a field named "key".
    @Test
    void testGetIntValues_WithAnyKeyField() throws Exception {
        ExternalResource resource = mock(ExternalResource.class);
        Provision provision = new Provision();

        Item item = new Item();
        item.setIntAttrName("key");
        item.setExtAttrName("externalKey");

        IntAttrName intAttrName = mock(IntAttrName.class);
        Any any = mock(Any.class);

        when(intAttrName.getField()).thenReturn("key");
        when(any.getKey()).thenReturn("key123");

        MappingManager.IntValues result = manager.getIntValues(
                resource,
                provision,
                item,
                intAttrName,
                AttrSchemaType.String,
                any,
                mock(AccountGetter.class),
                mock(PlainAttrGetter.class));

        assertEquals(1, intValues(result).size());
        assertEquals("key123", intValues(result).get(0).getStringValue());
    }

    // ==================== TC2: Get int values for Realm key field ====================
    // Tests that the Realm overload of getIntValues returns the Realm key.
    @Test
    void testGetIntValues_WithRealmKeyField() throws Exception {
        ExternalResource resource = mock(ExternalResource.class);
        Realm realm = mock(Realm.class);

        Item item = new Item();
        item.setIntAttrName("key");
        item.setExtAttrName("realmKey");

        IntAttrName intAttrName = mock(IntAttrName.class);

        when(intAttrName.getField()).thenReturn("key");
        when(realm.getKey()).thenReturn("realm-key-1");

        MappingManager.IntValues result = manager.getIntValues(
                resource,
                item,
                intAttrName,
                AttrSchemaType.String,
                realm);

        assertEquals(1, intValues(result).size());
        assertEquals("realm-key-1", intValues(result).get(0).getStringValue());
    }

    // ==================== TC3: Get int values for Realm fullPath field ====================
    // Tests that the Realm overload of getIntValues returns the Realm full path.
    @Test
    void testGetIntValues_WithRealmFullPathField() throws Exception {
        ExternalResource resource = mock(ExternalResource.class);
        Realm realm = mock(Realm.class);

        Item item = new Item();
        item.setIntAttrName("fullPath");
        item.setExtAttrName("path");

        IntAttrName intAttrName = mock(IntAttrName.class);

        when(intAttrName.getField()).thenReturn("fullPath");
        when(realm.getFullPath()).thenReturn("/root/tuscany");

        MappingManager.IntValues result = manager.getIntValues(
                resource,
                item,
                intAttrName,
                AttrSchemaType.String,
                realm);

        assertEquals(1, intValues(result).size());
        assertEquals("/root/tuscany", intValues(result).get(0).getStringValue());
    }

    // ==================== TC4: Decode encrypted account password ====================
    // Tests that decodePassword asks EncryptorManager for an Encryptor and decodes with the account cipher algorithm.
    @Test
    void testDecodePassword_WithStoredEncryptedPassword() throws Exception {
        Account account = mock(Account.class);
        Encryptor encryptor = mock(Encryptor.class);

        when(account.getPassword()).thenReturn("encrypted-value");
        when(account.getCipherAlgorithm()).thenReturn(CipherAlgorithm.AES);
        when(encryptorManager.getInstance()).thenReturn(encryptor);
        when(encryptor.decode("encrypted-value", CipherAlgorithm.AES)).thenReturn("clear-value");

        Optional<String> decoded = manager.exposedDecodePassword(account);

        assertTrue(decoded.isPresent());
        assertEquals("clear-value", decoded.get());
        verify(encryptor).decode("encrypted-value", CipherAlgorithm.AES);
    }

    // ==================== TC5: Decode blank account password ====================
    // Tests that decodePassword returns Optional.empty when the account password is blank.
    @Test
    void testDecodePassword_WithBlankPassword() {
        Account account = mock(Account.class);

        when(account.getPassword()).thenReturn("");

        Optional<String> decoded = manager.exposedDecodePassword(account);

        assertTrue(decoded.isEmpty());
        verifyNoInteractions(encryptorManager);
    }

    // ==================== TC6: Password attribute value prefers explicit default ====================
    // Tests that getPasswordAttrValue returns the supplied propagation password without decoding the stored password.
    @Test
    void testGetPasswordAttrValue_WithExplicitDefaultPassword() {
        Account account = mock(Account.class);

        Optional<String> value = manager.exposedGetPasswordAttrValue(account, "newPassword!");

        assertTrue(value.isPresent());
        assertEquals("newPassword!", value.get());
        verifyNoInteractions(encryptorManager);
    }

    // ==================== TC7: Password attribute value falls back to decoded password ====================
    // Tests that getPasswordAttrValue decodes the account password when no explicit default password is supplied.
    @Test
    void testGetPasswordAttrValue_FallsBackToDecodedAccountPassword() throws Exception {
        Account account = mock(Account.class);
        Encryptor encryptor = mock(Encryptor.class);

        when(account.getPassword()).thenReturn("encrypted");
        when(account.getCipherAlgorithm()).thenReturn(CipherAlgorithm.AES);
        when(encryptorManager.getInstance()).thenReturn(encryptor);
        when(encryptor.decode("encrypted", CipherAlgorithm.AES)).thenReturn("decoded");

        Optional<String> value = manager.exposedGetPasswordAttrValue(account, null);

        assertTrue(value.isPresent());
        assertEquals("decoded", value.get());
    }

    // ==================== TC8: getName prefers evaluated connector object link ====================
    // Tests that getName builds __NAME__ from the evaluated connObjectLink when available.
    @Test
    void testGetName_WithEvaluatedConnObjectLink() {
        Name name = TestableDefaultMappingManager.exposedGetName("uid=user1,ou=people", "fallback-key");

        assertEquals("uid=user1,ou=people", name.getNameValue());
    }

    // ==================== TC9: getName falls back to connector object key ====================
    // Tests that getName builds __NAME__ from connObjectKey when evaluated connObjectLink is blank.
    @Test
    void testGetName_FallbackToConnObjectKey() {
        Name name = TestableDefaultMappingManager.exposedGetName("   ", "fallback-key");

        assertEquals("fallback-key", name.getNameValue());
    }

    // ==================== TC10: hasMustChangePassword handles null mapping ====================
    // Tests that hasMustChangePassword safely returns false when Provision has no Mapping.
    @Test
    void testHasMustChangePassword_WithNullMapping() {
        Provision provision = new Provision();

        assertFalse(manager.hasMustChangePassword(provision));
    }

    // ==================== TC11: hasMustChangePassword detects mapped item ====================
    // Tests that hasMustChangePassword returns true when a mapping item targets mustChangePassword.
    @Test
    void testHasMustChangePassword_WhenMapped() {
        Provision provision = new Provision();
        Mapping mapping = new Mapping();

        Item item = new Item();
        item.setIntAttrName("mustChangePassword");
        item.setExtAttrName("pwdReset");

        mapping.getItems().add(item);
        provision.setMapping(mapping);

        assertTrue(manager.hasMustChangePassword(provision));
    }

    // ==================== TC12: getTransformers resolves transformer implementations ====================
    // Tests that getTransformers loads each configured transformer key through ImplementationDAO.
    @Test
    void testGetTransformers_WithConfiguredTransformerKeys() {
        Item item = new Item();
        item.getTransformers().add("trimTransformer");
        item.getTransformers().add("lowercaseTransformer");

        Implementation first = mock(Implementation.class);
        Implementation second = mock(Implementation.class);

        // doReturn avoids compile-time generic inference issues around findById(...).
        doReturn(Optional.of(first)).when(implementationDAO).findById("trimTransformer");
        doReturn(Optional.of(second)).when(implementationDAO).findById("lowercaseTransformer");

        List<Implementation> transformers = manager.exposedGetTransformers(item);

        assertEquals(List.of(first, second), transformers);
        verify(implementationDAO).findById("trimTransformer");
        verify(implementationDAO).findById("lowercaseTransformer");
    }

    // ==================== TC13: processPreparedAttr adds regular connector attribute ====================
    // Tests that a normal PreparedAttr is converted into a ConnId Attribute and does not return connObjectKey.
    @Test
    void testProcessPreparedAttr_WithRegularAttribute() throws Exception {
        MappingManager.PreparedAttr preparedAttr = newPreparedAttr("givenName", List.of("Giacomo"));

        Set<Attribute> attributes = new HashSet<>();

        Optional<String> connObjectKey =
                TestableDefaultMappingManager.exposedProcessPreparedAttr(preparedAttr, attributes);

        assertTrue(connObjectKey.isEmpty());
        assertEquals(1, attributes.size());
        assertTrue(attributes.stream().anyMatch(attr ->
                "givenName".equals(attr.getName()) && List.of("Giacomo").equals(attr.getValue())));
    }

    // ==================== TC14: processPreparedAttr returns connector object key for __NAME__ ====================
    // Tests that a PreparedAttr targeting __NAME__ returns the first value as connector object key.
    @Test
    void testProcessPreparedAttr_WithNameAttribute() throws Exception {
        MappingManager.PreparedAttr preparedAttr = newPreparedAttr(Name.NAME, List.of("uid=user1"));

        Set<Attribute> attributes = new HashSet<>();

        Optional<String> connObjectKey =
                TestableDefaultMappingManager.exposedProcessPreparedAttr(preparedAttr, attributes);

        assertTrue(connObjectKey.isPresent());
        assertEquals("uid=user1", connObjectKey.get());
        assertTrue(attributes.stream().anyMatch(attr -> Name.NAME.equals(attr.getName())));
    }

    // ==================== TC15: prepareAttr creates guarded password ====================
    // Tests that password mapping produces a PreparedAttr containing a GuardedString.
    @Test
    void testPrepareAttr_ForPasswordMapping() throws Exception {
        ExternalResource resource = mock(ExternalResource.class);
        Provision provision = new Provision();

        Item item = new Item();
        item.setIntAttrName("password");
        item.setExtAttrName(OperationalAttributes.PASSWORD_NAME);

        Any any = mock(Any.class);

        MappingManager.PreparedAttr preparedAttr = manager.prepareAttr(
                resource,
                provision,
                item,
                any,
                "secret",
                mock(AccountGetter.class),
                mock(AccountGetter.class),
                mock(PlainAttrGetter.class));

        assertEquals(OperationalAttributes.PASSWORD_NAME, preparedExtAttrName(preparedAttr));
        assertEquals(1, preparedValues(preparedAttr).size());
        assertInstanceOf(GuardedString.class, preparedValues(preparedAttr).get(0));
    }

    // ==================== TC16: prepareAttr for Realm fullPath ====================
    // Tests that realm prepareAttr parses the internal attribute name and prepares the mapped field value.
    @Test
    void testPrepareAttr_ForRealmFullPath() throws Exception {
        ExternalResource resource = mock(ExternalResource.class);
        Realm realm = mock(Realm.class);

        Item item = new Item();
        item.setIntAttrName("fullPath");
        item.setExtAttrName("path");

        IntAttrName parsed = mock(IntAttrName.class);

        when(intAttrNameParser.parse(eq("fullPath"), any(AnyTypeKind.class))).thenReturn(parsed);
        when(parsed.getField()).thenReturn("fullPath");
        when(realm.getFullPath()).thenReturn("/root/team");

        MappingManager.PreparedAttr preparedAttr = manager.prepareAttr(resource, item, realm);

        assertEquals("path", preparedExtAttrName(preparedAttr));
        assertEquals(List.of("/root/team"), preparedValues(preparedAttr));
    }

    // ==================== TC17: prepareAttrsFromAny adds ENABLE operational attribute ====================
    // Tests that prepareAttrsFromAny includes the ConnId ENABLE attribute when enable is explicitly supplied.
    @Test
    void testPrepareAttrsFromAny_WithEnableFlag() {
        Any any = mock(Any.class);
        ExternalResource resource = mock(ExternalResource.class);
        Provision provision = new Provision();

        Mapping mapping = new Mapping();
        provision.setMapping(mapping);

        MappingManager.PreparedAttrs preparedAttrs =
                manager.prepareAttrsFromAny(any, null, false, Boolean.TRUE, resource, provision);

        assertTrue(preparedAttrs.attributes().stream().anyMatch(attr ->
                OperationalAttributes.ENABLE_NAME.equals(attr.getName())
                        && attr.getValue().contains(Boolean.TRUE)));
    }

    // ==================== TC18: getConnObjectKeyValue for Any without key mapping ====================
    // Tests that getConnObjectKeyValue returns Optional.empty when no connObjectKey item is configured.
    @Test
    void testGetConnObjectKeyValue_ForAnyWithoutConnObjectKeyItem() {
        Any any = mock(Any.class);
        ExternalResource resource = mock(ExternalResource.class);
        Provision provision = new Provision();

        Mapping mapping = new Mapping();
        provision.setMapping(mapping);

        Optional<String> value = manager.getConnObjectKeyValue(any, resource, provision);

        assertTrue(value.isEmpty());
    }

    // ==================== TC19: setIntValues adds AnyTO plain attribute ====================
    // Tests that connector values are copied into AnyTO plain attributes.
    @Test
    void testSetIntValues_OnAnyTOPlainAttribute() {
        Item item = new Item();
        item.setIntAttrName("email");

        Attribute attr = AttributeBuilder.build("mail", "user@example.org");

        UserTO userTO = new UserTO();

        manager.setIntValues(item, attr, userTO);

        Optional<Attr> email = userTO.getPlainAttr("email");
        assertTrue(email.isPresent());
        assertEquals(List.of("user@example.org"), email.get().getValues());
    }

    // ==================== TC20: setIntValues writes AnyObjectTO key field ====================
    // Tests that field-based mapping writes directly to AnyObjectTO.key.
    @Test
    void testSetIntValues_OnAnyObjectTOKeyField() {
        Item item = new Item();
        item.setIntAttrName("key");

        Attribute attr = AttributeBuilder.build("id", "new-key");

        AnyObjectTO anyObjectTO = new AnyObjectTO();

        manager.setIntValues(item, attr, anyObjectTO);

        assertEquals("new-key", anyObjectTO.getKey());
    }

    // ==================== TC21: setIntValues writes GroupTO name field ====================
    // Tests that field-based mapping writes directly to GroupTO.name.
    @Test
    void testSetIntValues_OnGroupTONameField() {
        Item item = new Item();
        item.setIntAttrName("name");

        Attribute attr = AttributeBuilder.build("cn", "engineering");

        GroupTO groupTO = new GroupTO();

        manager.setIntValues(item, attr, groupTO);

        assertEquals("engineering", groupTO.getName());
    }

    // ==================== TC22: setIntValues adds RealmTO plain attribute ====================
    // Tests that connector attribute values are written into RealmTO plain attributes.
    @Test
    void testSetIntValues_OnRealmTOPlainAttribute() {
        Item item = new Item();
        item.setIntAttrName("description");

        Attribute attr = AttributeBuilder.build("description", "Production realm");

        RealmTO realmTO = new RealmTO();

        manager.setIntValues(item, attr, realmTO);

        Optional<Attr> description = realmTO.getPlainAttr("description");
        assertTrue(description.isPresent());
        assertEquals(List.of("Production realm"), description.get().getValues());
    }

    // ==================== TC23: evaluateNAME for Any falls back to connector object key ====================
    // Tests that evaluateNAME returns a Name based on the fallback connObjectKey when no link is configured.
    @Test
    void testEvaluateNAME_ForAnyFallback() {
        Any any = mock(Any.class);
        Provision provision = new Provision();

        Name name = manager.exposedEvaluateNAME(any, provision, "fallback-key");

        assertEquals("fallback-key", name.getNameValue());
    }

    // ==================== TC24: evaluateNAME for Realm falls back to connector object key ====================
    // Tests that the Realm overload of evaluateNAME returns a Name based on the fallback connObjectKey.
    @Test
    void testEvaluateNAME_ForRealmFallback() {
        Realm realm = mock(Realm.class);
        OrgUnit orgUnit = new OrgUnit();

        Name name = manager.exposedEvaluateNAME(realm, orgUnit, "realm-fallback-key");

        assertEquals("realm-fallback-key", name.getNameValue());
    }

    // ==================== TC25: prepareAttrsFromLinkedAccount with empty mapping ====================
    // Tests that linked account attribute preparation returns an empty set when no mapping items exist.
    @Test
    void testPrepareAttrsFromLinkedAccount_WithEmptyMapping() {
        User user = mock(User.class);
        LinkedAccount linkedAccount = mock(LinkedAccount.class);

        Provision provision = new Provision();
        Mapping mapping = new Mapping();
        provision.setMapping(mapping);

        Set<Attribute> attributes =
                manager.prepareAttrsFromLinkedAccount(user, linkedAccount, null, false, provision);

        assertNotNull(attributes);
        assertTrue(attributes.isEmpty());
    }
}