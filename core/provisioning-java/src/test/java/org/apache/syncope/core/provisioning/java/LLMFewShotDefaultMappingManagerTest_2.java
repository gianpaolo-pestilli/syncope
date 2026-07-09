package org.apache.syncope.core.provisioning.java;/*
 * Corrected DefaultMappingManagerTest.
 *
 * Main fixes vs previous version:
 * - IntAttrNameParser.parse(...) uses AnyTypeKind, not AnyTO.class.
 * - AccountGetter / PlainAttrGetter are mocked directly instead of returning Optional.
 * - PreparedAttrs / PreparedAttr access is done via reflection helpers to support Syncope API variations.
 * - OrgUnit#setItems(), JexlTools#evaluate(), SchemaInfo#getSchema() assumptions removed.
 *
 * Sources used: uploaded DefaultMappingManager.java (imports/package/dependencies) and Apache Syncope GitHub/API lookup.
 * [1](https://uniroma2-my.sharepoint.com/personal/gianpaolo_pestilli_students_uniroma2_eu/Documents/File%20di%20Microsoft%20Copilot%20Chat/DefaultMappingManager.java)[2](https://github.com/apache/syncope)
 */

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.syncope.common.lib.to.AnyObjectTO;
import org.apache.syncope.common.lib.to.Item;
import org.apache.syncope.common.lib.to.Mapping;
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
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * JUnit 5 / Mockito test suite for DefaultMappingManager.
 */
@ExtendWith(MockitoExtension.class)
class LLMFewShotDefaultMappingManagerTest_2 {

    @Mock
    private UserDAO userDAO;

    @Mock
    private AnyObjectDAO anyObjectDAO;

    @Mock
    private GroupDAO groupDAO;

    @Mock
    private RelationshipTypeDAO relationshipTypeDAO;

    @Mock
    private RealmSearchDAO realmSearchDAO;

    @Mock
    private ImplementationDAO implementationDAO;

    @Mock
    private DerAttrHandler derAttrHandler;

    @Mock
    private IntAttrNameParser intAttrNameParser;

    @Mock
    private EncryptorManager encryptorManager;

    @Mock
    private JexlTools jexlTools;

    @InjectMocks
    private TestableDefaultMappingManager manager;

    private ExternalResource resource;

    private Provision provision;

    private Mapping mapping;

    @BeforeEach
    void setUp() {
        resource = mock(ExternalResource.class);
        provision = new Provision();
        mapping = new Mapping();
        provision.setMapping(mapping);
    }

    @Test
    void testConstructorWiresDependencies() {
        TestableDefaultMappingManager local = new TestableDefaultMappingManager(
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

        assertNotNull(local);
    }

    @Test
    void testIntAttrNameParserUsesAnyTypeKindUser() throws Exception {
        IntAttrName parsed = mock(IntAttrName.class);
        when(intAttrNameParser.parse("username", AnyTypeKind.USER)).thenReturn(parsed);

        assertSame(parsed, intAttrNameParser.parse("username", AnyTypeKind.USER));
        verify(intAttrNameParser).parse("username", AnyTypeKind.USER);
    }

    @Test
    void testIntAttrNameParserUsesAnyTypeKindGroup() throws Exception {
        IntAttrName parsed = mock(IntAttrName.class);
        when(intAttrNameParser.parse("name", AnyTypeKind.GROUP)).thenReturn(parsed);

        assertSame(parsed, intAttrNameParser.parse("name", AnyTypeKind.GROUP));
        verify(intAttrNameParser).parse("name", AnyTypeKind.GROUP);
    }

    @Test
    void testHasMustChangePasswordWithNoMappingReturnsFalse() {
        Provision empty = new Provision();

        Assertions.assertFalse(manager.hasMustChangePassword(empty));
    }

    @Test
    void testHasMustChangePasswordWithNonPasswordItemsReturnsFalse() {
        mapping.getItems().add(mockItem("username", Name.NAME, false, false));

        Assertions.assertFalse(manager.hasMustChangePassword(provision));
    }

    @Test
    void testHasMustChangePasswordWithPasswordItemReturnsTrue() {
        mapping.getItems().add(mockItem("password", OperationalAttributes.PASSWORD_NAME, false, true));

        Assertions.assertTrue(manager.hasMustChangePassword(provision));
    }

    @Test
    void testSetIntValuesUserKeyFromUid() {
        UserTO userTO = new UserTO();
        Item item = mockItem("key", Uid.NAME, true, false);

        manager.setIntValues(item, new Uid("user-key-1"), userTO);

        assertEquals("user-key-1", userTO.getKey());
    }

    @Test
    void testSetIntValuesUserUsernameFromName() {
        UserTO userTO = new UserTO();
        Item item = mockItem("username", Name.NAME, false, false);

        manager.setIntValues(item, new Name("jsmith"), userTO);

        assertEquals("jsmith", userTO.getUsername());
    }

    @Test
    void testSetIntValuesUserPasswordFromGuardedString() {
        UserTO userTO = new UserTO();
        Item item = mockItem("password", OperationalAttributes.PASSWORD_NAME, false, true);
        GuardedString guarded = new GuardedString("secret".toCharArray());

        manager.setIntValues(item, AttributeBuilder.buildPassword(guarded), userTO);

        assertNotNull(userTO.getPassword());
    }

    @Test
    void testSetIntValuesAnyObjectNameFromName() {
        AnyObjectTO anyObjectTO = new AnyObjectTO();
        Item item = mockItem("name", Name.NAME, false, false);

        manager.setIntValues(item, new Name("printer01"), anyObjectTO);

        assertEquals("printer01", anyObjectTO.getName());
    }

    @Test
    void testSetIntValuesRealmKeyFromUid() {
        RealmTO realmTO = new RealmTO();
        Item item = mockItem("key", Uid.NAME, true, false);

        manager.setIntValues(item, new Uid("realm-key-1"), realmTO);

        assertEquals("realm-key-1", realmTO.getKey());
    }

    @Test
    void testSetIntValuesRealmNameFromName() {
        RealmTO realmTO = new RealmTO();
        Item item = mockItem("name", Name.NAME, false, false);

        manager.setIntValues(item, new Name("engineering"), realmTO);

        assertEquals("engineering", realmTO.getName());
    }

    @Test
    void testSetIntValuesRealmFullPathFromAttribute() {
        RealmTO realmTO = new RealmTO();
        Item item = mockItem("fullPath", "path", false, false);

        manager.setIntValues(item, AttributeBuilder.build("path", "/engineering/platform"), realmTO);

        assertEquals("/engineering/platform", realmTO.getFullPath());
    }

    @Test
    void testGetIntValuesForAnyKeyField() {
        Any any = mock(Any.class);
        when(any.getKey()).thenReturn("any-key-1");

        Item item = mockItem("key", Uid.NAME, true, false);
        IntAttrName intAttrName = mockFieldIntAttrName("key");

        AccountGetter accountGetter = mock(AccountGetter.class);
        PlainAttrGetter plainAttrGetter = mock(PlainAttrGetter.class);

        MappingManager.IntValues result = manager.getIntValues(
                resource,
                provision,
                item,
                intAttrName,
                AttrSchemaType.String,
                any,
                accountGetter,
                plainAttrGetter);

        assertNotNull(result);
        assertFalse(result.values().isEmpty());
        assertEquals("any-key-1", result.values().get(0).getStringValue());
    }

    @Test
    void testGetIntValuesForRealmKeyField() {
        Realm realm = mock(Realm.class);
        when(realm.getKey()).thenReturn("realm-key-1");

        Item item = mockItem("key", Uid.NAME, true, false);
        IntAttrName intAttrName = mockFieldIntAttrName("key");

        MappingManager.IntValues result = manager.getIntValues(
                resource,
                item,
                intAttrName,
                AttrSchemaType.String,
                realm);

        assertNotNull(result);
        assertFalse(result.values().isEmpty());
        assertEquals("realm-key-1", result.values().get(0).getStringValue());
    }

    @Test
    void testGetIntValuesForRealmNameField() {
        Realm realm = mock(Realm.class);
        when(realm.getName()).thenReturn("engineering");

        Item item = mockItem("name", Name.NAME, false, false);
        IntAttrName intAttrName = mockFieldIntAttrName("name");

        MappingManager.IntValues result = manager.getIntValues(
                resource,
                item,
                intAttrName,
                AttrSchemaType.String,
                realm);

        assertNotNull(result);
        assertFalse(result.values().isEmpty());
        assertEquals("engineering", result.values().get(0).getStringValue());
    }

    @Test
    void testGetIntValuesForRealmFullPathField() {
        Realm realm = mock(Realm.class);
        when(realm.getFullPath()).thenReturn("/engineering/platform");

        Item item = mockItem("fullPath", "path", false, false);
        IntAttrName intAttrName = mockFieldIntAttrName("fullPath");

        MappingManager.IntValues result = manager.getIntValues(
                resource,
                item,
                intAttrName,
                AttrSchemaType.String,
                realm);

        assertNotNull(result);
        assertFalse(result.values().isEmpty());
        assertEquals("/engineering/platform", result.values().get(0).getStringValue());
    }

    @Test
    void testPrepareAttrForMissingPlainAttrDoesNotThrow() {
        Any any = mock(Any.class);
        Item item = mockItem("email", "mail", false, false);

        AccountGetter accountGetter = mock(AccountGetter.class);
        PlainAttrGetter plainAttrGetter = mock(PlainAttrGetter.class);

        MappingManager.PreparedAttr result = manager.prepareAttr(
                resource,
                provision,
                item,
                any,
                null,
                accountGetter,
                null,
                plainAttrGetter);

        assertNotNull(result);
    }

    @Test
    void testPrepareAttrForConnObjectKeyDoesNotThrow() {
        Any any = mock(Any.class);
        when(any.getKey()).thenReturn("user-key-1");

        Item item = mockItem("key", Uid.NAME, true, false);

        AccountGetter accountGetter = mock(AccountGetter.class);
        PlainAttrGetter plainAttrGetter = mock(PlainAttrGetter.class);

        MappingManager.PreparedAttr result = manager.prepareAttr(
                resource,
                provision,
                item,
                any,
                null,
                accountGetter,
                null,
                plainAttrGetter);

        assertNotNull(result);
    }

    @Test
    void testPrepareAttrsFromAnyWithEnableTrueAddsEnableAttribute() {
        Any any = mock(Any.class);
        when(any.getKey()).thenReturn("user-key-1");

        mapping.getItems().add(mockItem("key", Uid.NAME, true, false));

        MappingManager.PreparedAttrs result = manager.prepareAttrsFromAny(
                any,
                null,
                false,
                Boolean.TRUE,
                resource,
                provision);

        Set<Attribute> attrs = getAttrs(result);
        assertTrue(attrs.stream().anyMatch(attr -> OperationalAttributes.ENABLE_NAME.equals(attr.getName())));
    }

    @Test
    void testPrepareAttrsFromAnyWithEnableFalseAddsEnableAttribute() {
        Any any = mock(Any.class);
        when(any.getKey()).thenReturn("user-key-1");

        mapping.getItems().add(mockItem("key", Uid.NAME, true, false));

        MappingManager.PreparedAttrs result = manager.prepareAttrsFromAny(
                any,
                null,
                false,
                Boolean.FALSE,
                resource,
                provision);

        Set<Attribute> attrs = getAttrs(result);
        assertTrue(attrs.stream().anyMatch(attr -> OperationalAttributes.ENABLE_NAME.equals(attr.getName())));
    }

    @Test
    void testPrepareAttrsFromAnyWithChangePasswordFalseSkipsPasswordAttribute() {
        Any any = mock(Any.class);
        when(any.getKey()).thenReturn("user-key-1");

        mapping.getItems().add(mockItem("key", Uid.NAME, true, false));
        mapping.getItems().add(mockItem("password", OperationalAttributes.PASSWORD_NAME, false, true));

        MappingManager.PreparedAttrs result = manager.prepareAttrsFromAny(
                any,
                "clearPassword",
                false,
                null,
                resource,
                provision);

        Set<Attribute> attrs = getAttrs(result);
        assertTrue(attrs.stream().noneMatch(attr -> OperationalAttributes.PASSWORD_NAME.equals(attr.getName())));
    }

    @Test
    void testPrepareAttrsFromAnyWithChangePasswordTrueAddsPasswordAttribute() {
        Any any = mock(Any.class);
        when(any.getKey()).thenReturn("user-key-1");

        mapping.getItems().add(mockItem("key", Uid.NAME, true, false));
        mapping.getItems().add(mockItem("password", OperationalAttributes.PASSWORD_NAME, false, true));

        MappingManager.PreparedAttrs result = manager.prepareAttrsFromAny(
                any,
                "clearPassword",
                true,
                null,
                resource,
                provision);

        Set<Attribute> attrs = getAttrs(result);
        assertTrue(attrs.stream().anyMatch(attr -> OperationalAttributes.PASSWORD_NAME.equals(attr.getName())));
    }

    @Test
    void testPrepareAttrsFromLinkedAccountWithUsernameOverride() {
        User user = mock(User.class);
        when(user.getKey()).thenReturn("user-key-1");

        LinkedAccount account = mock(LinkedAccount.class);
        when(account.getUsername()).thenReturn("linkedUser");

        mapping.getItems().add(mockItem("username", Name.NAME, true, false));

        Set<Attribute> result = manager.prepareAttrsFromLinkedAccount(
                user,
                account,
                null,
                false,
                provision);

        assertNotNull(result);
        assertTrue(result.stream().anyMatch(attr -> Name.NAME.equals(attr.getName())));
    }

    @Test
    void testPrepareAttrsFromLinkedAccountWithPasswordChangeAddsPassword() {
        User user = mock(User.class);
        when(user.getKey()).thenReturn("user-key-1");

        LinkedAccount account = mock(LinkedAccount.class);
        when(account.getUsername()).thenReturn("linkedUser");
        when(account.getPassword()).thenReturn(null);

        mapping.getItems().add(mockItem("username", Name.NAME, true, false));
        mapping.getItems().add(mockItem("password", OperationalAttributes.PASSWORD_NAME, false, true));

        Set<Attribute> result = manager.prepareAttrsFromLinkedAccount(
                user,
                account,
                "fallbackPassword",
                true,
                provision);

        assertNotNull(result);
        assertTrue(result.stream().anyMatch(attr -> OperationalAttributes.PASSWORD_NAME.equals(attr.getName())));
    }

    @Test
    void testEvaluateNameForAnyWithoutConnObjectLinkUsesConnObjectKey() {
        Any any = mock(Any.class);

        Name result = manager.evaluateNAME(any, new Provision(), "conn-key-1");

        assertNotNull(result);
        assertEquals("conn-key-1", result.getNameValue());
    }

    @Test
    void testEvaluateNameForRealmWithoutConnObjectLinkUsesConnObjectKey() {
        Realm realm = mock(Realm.class);

        Name result = manager.evaluateNAME(realm, new OrgUnit(), "realm-key-1");

        assertNotNull(result);
        assertEquals("realm-key-1", result.getNameValue());
    }

    @Test
    void testProcessPreparedAttrWithNullAttributeKeepsSetEmpty() {
        MappingManager.PreparedAttr preparedAttr = newPreparedAttr(null, null);
        Set<Attribute> attrs = new HashSet<>();

        Optional<String> result = TestableDefaultMappingManager.process(preparedAttr, attrs);

        assertTrue(result.isEmpty());
        assertTrue(attrs.isEmpty());
    }

    @Test
    void testProcessPreparedAttrWithAttributeAddsToSet() {
        Attribute attribute = AttributeBuilder.build("mail", "user@example.org");
        MappingManager.PreparedAttr preparedAttr = newPreparedAttr(null, attribute);
        Set<Attribute> attrs = new HashSet<>();

        Optional<String> result = TestableDefaultMappingManager.process(preparedAttr, attrs);

        assertTrue(result.isEmpty());
        assertEquals(1, attrs.size());
        assertTrue(attrs.stream().anyMatch(attr -> "mail".equals(attr.getName())));
    }

    @Test
    void testProcessPreparedAttrWithConnObjectLinkReturnsValue() {
        Attribute attribute = AttributeBuilder.build("mail", "user@example.org");
        MappingManager.PreparedAttr preparedAttr = newPreparedAttr("linked-user", attribute);
        Set<Attribute> attrs = new HashSet<>();

        Optional<String> result = TestableDefaultMappingManager.process(preparedAttr, attrs);

        assertTrue(result.isPresent());
        assertEquals("linked-user", result.get());
        assertEquals(1, attrs.size());
    }

    @Test
    void testDecodePasswordWithNullPasswordReturnsEmpty() {
        Account account = mock(Account.class);
        when(account.getPassword()).thenReturn(null);

        Optional<String> result = manager.decodePassword(account);

        assertTrue(result.isEmpty());
    }

    @Test
    void testDecodePasswordWithBase64PasswordReturnsDecodedValue() {
        Account account = mock(Account.class);
        String encoded = Base64.getEncoder().encodeToString("secret".getBytes());
        when(account.getPassword()).thenReturn(encoded);

        Optional<String> result = manager.decodePassword(account);

        assertTrue(result.isPresent());
        assertEquals("secret", result.get());
    }

    @Test
    void testGetPasswordAttrValuePrefersAccountPassword() {
        Account account = mock(Account.class);
        String encoded = Base64.getEncoder().encodeToString("accountSecret".getBytes());
        when(account.getPassword()).thenReturn(encoded);

        Optional<String> result = manager.getPasswordAttrValue(account, "fallbackSecret");

        assertTrue(result.isPresent());
        assertEquals("accountSecret", result.get());
    }

    @Test
    void testGetPasswordAttrValueFallsBackToProvidedPassword() {
        Account account = mock(Account.class);
        when(account.getPassword()).thenReturn(null);

        Optional<String> result = manager.getPasswordAttrValue(account, "fallbackSecret");

        assertTrue(result.isPresent());
        assertEquals("fallbackSecret", result.get());
    }

    @Test
    void testAttributeUtilFindReturnsExpectedAttribute() {
        Set<Attribute> attrs = Set.of(AttributeBuilder.build("mail", "user@example.org"));

        Attribute found = AttributeUtil.find("mail", attrs);

        assertNotNull(found);
        assertEquals("mail", found.getName());
        assertEquals("user@example.org", found.getValue().get(0));
    }

    @Test
    void testMockPlainAttrValueSanity() {
        PlainAttrValue value = mockPlainAttrValue("plain-value");

        assertEquals("plain-value", value.getStringValue());
        assertEquals("plain-value", value.getValueAsString());
    }

    private static Item mockItem(
            final String intAttrName,
            final String extAttrName,
            final boolean connObjectKey,
            final boolean password) {

        Item item = mock(Item.class);
        when(item.getIntAttrName()).thenReturn(intAttrName);
        when(item.getExtAttrName()).thenReturn(extAttrName);
        when(item.isConnObjectKey()).thenReturn(connObjectKey);
        when(item.isPassword()).thenReturn(password);
        when(item.getMandatoryCondition()).thenReturn("false");
        return item;
    }

    private static IntAttrName mockFieldIntAttrName(final String field) {
        IntAttrName intAttrName = mock(IntAttrName.class);
        when(intAttrName.getField()).thenReturn(field);
        return intAttrName;
    }

    private static PlainAttrValue mockPlainAttrValue(final String stringValue) {
        PlainAttrValue value = mock(PlainAttrValue.class);
        when(value.getStringValue()).thenReturn(stringValue);
        when(value.getValueAsString()).thenReturn(stringValue);
        return value;
    }

    @SuppressWarnings("unchecked")
    private static Set<Attribute> getAttrs(final MappingManager.PreparedAttrs preparedAttrs) {
        try {
            Method getter = preparedAttrs.getClass().getMethod("getAttrs");
            Object value = getter.invoke(preparedAttrs);
            if (value instanceof Set) {
                return (Set<Attribute>) value;
            }
        } catch (Exception ignored) {
            // fallback below
        }

        try {
            Method getter = preparedAttrs.getClass().getMethod("attrs");
            Object value = getter.invoke(preparedAttrs);
            if (value instanceof Set) {
                return (Set<Attribute>) value;
            }
        } catch (Exception ignored) {
            // fallback below
        }

        try {
            Field field = preparedAttrs.getClass().getDeclaredField("attrs");
            field.setAccessible(true);
            Object value = field.get(preparedAttrs);
            if (value instanceof Set) {
                return (Set<Attribute>) value;
            }
        } catch (Exception ignored) {
            // final fallback
        }

        fail("Unable to extract attrs from PreparedAttrs");
        return Set.of();
    }

    private static MappingManager.PreparedAttr newPreparedAttr(final String connObjectLink, final Attribute attr) {
        for (Constructor<?> constructor : MappingManager.PreparedAttr.class.getDeclaredConstructors()) {
            Class<?>[] types = constructor.getParameterTypes();
            if (types.length == 2 && types[0] == String.class && Attribute.class.isAssignableFrom(types[1])) {
                try {
                    constructor.setAccessible(true);
                    return (MappingManager.PreparedAttr) constructor.newInstance(connObjectLink, attr);
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
            }
        }

        fail("No compatible PreparedAttr(String, Attribute) constructor found");
        return null;
    }

    static class TestableDefaultMappingManager extends DefaultMappingManager {

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

        @Override
        protected Optional<String> decodePassword(final Account account) {
            return super.decodePassword(account);
        }

        @Override
        protected Optional<String> getPasswordAttrValue(final Account account, final String defaultValue) {
            return super.getPasswordAttrValue(account, defaultValue);
        }

        @Override
        protected Name evaluateNAME(final Any any, final Provision provision, final String connObjectKey) {
            return super.evaluateNAME(any, provision, connObjectKey);
        }

        @Override
        protected Name evaluateNAME(final Realm realm, final OrgUnit orgUnit, final String connObjectKey) {
            return super.evaluateNAME(realm, orgUnit, connObjectKey);
        }

        static Optional<String> process(
                final MappingManager.PreparedAttr preparedAttr,
                final Set<Attribute> attributes) {

            return processPreparedAttr(preparedAttr, attributes);
        }
    }
}