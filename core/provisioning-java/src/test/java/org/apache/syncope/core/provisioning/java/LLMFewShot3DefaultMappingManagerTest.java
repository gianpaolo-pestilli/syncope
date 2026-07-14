package org.apache.syncope.core.provisioning.java;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.syncope.common.lib.to.AnyObjectTO;
import org.apache.syncope.common.lib.to.AnyTO;
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
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Disabled
class LLMFewShot3DefaultMappingManagerTest {

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

    @Mock
    private ExternalResource resource;

    @Mock
    private Provision provision;

    @Mock
    private Mapping mapping;

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

            super(userDAO, anyObjectDAO, groupDAO, relationshipTypeDAO, realmSearchDAO, implementationDAO,
                    derAttrHandler, intAttrNameParser, encryptorManager, jexlTools);
        }

        Name exposedEvaluateNAME(final Any any, final Provision provision, final String connObjectKey) {
            return evaluateNAME(any, provision, connObjectKey);
        }

        Name exposedEvaluateNAME(final Realm realm, final OrgUnit orgUnit, final String connObjectKey) {
            return evaluateNAME(realm, orgUnit, connObjectKey);
        }
    }

    @BeforeEach
    void setUp() {
        manager = spy(new TestableDefaultMappingManager(
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

        lenient().when(provision.getMapping()).thenReturn(mapping);
        lenient().when(mapping.getItems()).thenReturn(Collections.emptyList());
    }

    @Test
    void testHasMustChangePassword_WithMappedItem_ReturnsTrue() {
        Item item = mock(Item.class);
        when(item.getIntAttrName()).thenReturn("mustChangePassword");
        when(mapping.getItems()).thenReturn(List.of(item));

        boolean result = manager.hasMustChangePassword(provision);

        assertTrue(result);
    }

    @Test
    void testHasMustChangePassword_WithNoMappedItem_ReturnsFalse() {
        Item item = mock(Item.class);
        when(item.getIntAttrName()).thenReturn("username");
        when(mapping.getItems()).thenReturn(List.of(item));

        boolean result = manager.hasMustChangePassword(provision);

        assertFalse(result);
    }

    @Test
    void testHasMustChangePassword_WithEmptyItems_ReturnsFalse() {
        when(mapping.getItems()).thenReturn(Collections.emptyList());

        boolean result = manager.hasMustChangePassword(provision);

        assertFalse(result);
    }

    @Test
    void testPrepareAttrsFromAny_WithValidUser() throws ParseException {
        User user = mock(User.class);
        Item item = mock(Item.class);
        Attribute attr = AttributeBuilder.build("uid", "user123");

        when(mapping.getItems()).thenReturn(List.of(item));
        doReturn(new MappingManager.PreparedAttr(null, attr)).when(manager).prepareAttr(
                eq(resource),
                eq(provision),
                eq(item),
                eq(user),
                isNull(),
                any(AccountGetter.class),
                any(AccountGetter.class),
                any(PlainAttrGetter.class));

        MappingManager.PreparedAttrs result =
                manager.prepareAttrsFromAny(user, null, false, null, resource, provision);

        assertNotNull(result);
    }

    @Test
    void testPrepareAttrsFromAny_WithEnableTrue_DoesNotThrow() {
        User user = mock(User.class);
        when(mapping.getItems()).thenReturn(Collections.emptyList());

        Assertions.assertDoesNotThrow(() -> manager.prepareAttrsFromAny(user, null, false, Boolean.TRUE, resource, provision));
    }

    @Test
    void testPrepareAttrsFromAny_WithEnableFalse_DoesNotThrow() {
        User user = mock(User.class);
        when(mapping.getItems()).thenReturn(Collections.emptyList());

        Assertions.assertDoesNotThrow(() -> manager.prepareAttrsFromAny(user, null, false, Boolean.FALSE, resource, provision));
    }

    @Test
    void testPrepareAttrsFromAny_WithPasswordAndChangePwd_DoesNotThrow() {
        User user = mock(User.class);
        when(mapping.getItems()).thenReturn(Collections.emptyList());

        Assertions.assertDoesNotThrow(() -> manager.prepareAttrsFromAny(user, "clearPassword", true, null, resource, provision));
    }

    @Test
    void testPrepareAttrsFromAny_WithNullPasswordAndChangePwd_DoesNotThrow() {
        User user = mock(User.class);
        when(mapping.getItems()).thenReturn(Collections.emptyList());

        Assertions.assertDoesNotThrow(() -> manager.prepareAttrsFromAny(user, null, true, null, resource, provision));
    }

    @Test
    void testPrepareAttrsFromAny_WhenPrepareAttrReturnsNull_SkipsInvalidItem() throws ParseException {
        User user = mock(User.class);
        Item item = mock(Item.class);

        when(mapping.getItems()).thenReturn(List.of(item));
        doReturn(null).when(manager).prepareAttr(
                eq(resource),
                eq(provision),
                eq(item),
                eq(user),
                isNull(),
                any(AccountGetter.class),
                any(AccountGetter.class),
                any(PlainAttrGetter.class));

        MappingManager.PreparedAttrs result =
                manager.prepareAttrsFromAny(user, null, false, null, resource, provision);

        assertNotNull(result);
    }

    @Test
    void testPrepareAttrsFromAny_WithMultipleItems_DoesNotThrow() throws ParseException {
        User user = mock(User.class);
        Item first = mock(Item.class);
        Item second = mock(Item.class);

        when(mapping.getItems()).thenReturn(List.of(first, second));
        doReturn(new MappingManager.PreparedAttr(null, AttributeBuilder.build("username", "rossini")))
                .when(manager).prepareAttr(
                        eq(resource),
                        eq(provision),
                        eq(first),
                        eq(user),
                        isNull(),
                        any(AccountGetter.class),
                        any(AccountGetter.class),
                        any(PlainAttrGetter.class));
        doReturn(new MappingManager.PreparedAttr(null, AttributeBuilder.build("email", "rossini@example.org")))
                .when(manager).prepareAttr(
                        eq(resource),
                        eq(provision),
                        eq(second),
                        eq(user),
                        isNull(),
                        any(AccountGetter.class),
                        any(AccountGetter.class),
                        any(PlainAttrGetter.class));

        Assertions.assertDoesNotThrow(() -> manager.prepareAttrsFromAny(user, null, false, null, resource, provision));
    }

    @Test
    void testPrepareAttrsFromLinkedAccount_WithEmptyMapping_ReturnsSet() {
        User user = mock(User.class);
        LinkedAccount account = mock(LinkedAccount.class);
        when(mapping.getItems()).thenReturn(Collections.emptyList());

        Set<Attribute> result = manager.prepareAttrsFromLinkedAccount(user, account, null, false, provision);

        assertNotNull(result);
    }

    @Test
    void testPrepareAttrsFromLinkedAccount_WithPasswordAndChangePwd_DoesNotThrow() {
        User user = mock(User.class);
        LinkedAccount account = mock(LinkedAccount.class);
        when(mapping.getItems()).thenReturn(Collections.emptyList());

        Assertions.assertDoesNotThrow(() ->
                manager.prepareAttrsFromLinkedAccount(user, account, "newPassword", true, provision));
    }

    @Test
    void testPrepareAttr_WithInvalidIntAttrName() throws ParseException {
        Any any = mock(Any.class);
        Item item = mock(Item.class);

        when(item.getIntAttrName()).thenReturn("invalid.field");
        when(intAttrNameParser.parse(eq("invalid.field"), any())).thenThrow(new ParseException("invalid", 0));

        MappingManager.PreparedAttr result = manager.prepareAttr(
                resource,
                provision,
                item,
                any,
                "password",
                account -> null,
                account -> null,
                (owner, schema) -> null);

        assertNull(result);
    }

    @Test
    void testPrepareAttr_WithEmptyInternalValues_ReturnsNull() throws ParseException {
        Any any = mock(Any.class);
        Item item = mock(Item.class);
        IntAttrName intAttrName = mock(IntAttrName.class);

        when(item.getIntAttrName()).thenReturn("username");
        when(item.getExtAttrName()).thenReturn("uid");
        when(intAttrNameParser.parse(eq("username"), any())).thenReturn(intAttrName);

        doReturn(new MappingManager.IntValues(AttrSchemaType.String, Collections.emptyList()))
                .when(manager).getIntValues(
                        eq(resource),
                        eq(provision),
                        eq(item),
                        eq(intAttrName),
                        any(),
                        eq(any),
                        any(AccountGetter.class),
                        any(PlainAttrGetter.class));

        MappingManager.PreparedAttr result = manager.prepareAttr(
                resource,
                provision,
                item,
                any,
                null,
                account -> null,
                account -> null,
                (owner, schema) -> null);

        assertNull(result);
    }

    @Test
    void testGetConnObjectKeyValue_WithNoConnObjectKeyItem_ReturnsEmptyOptional() throws ParseException {
        Any any = mock(Any.class);
        when(mapping.getConnObjectKeyItem()).thenReturn(Optional.empty());

        Optional<String> result = manager.getConnObjectKeyValue(any, resource, provision);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetConnObjectKeyValue_WithInvalidConnObjectKey_ReturnsEmptyOptional() throws ParseException {
        Any any = mock(Any.class);
        Item connObjectKeyItem = mock(Item.class);

        when(mapping.getConnObjectKeyItem()).thenReturn(Optional.of(connObjectKeyItem));
        when(connObjectKeyItem.getIntAttrName()).thenReturn("bad.key");
        when(intAttrNameParser.parse(eq("bad.key"), any())).thenThrow(new ParseException("bad key", 0));

        Optional<String> result = manager.getConnObjectKeyValue(any, resource, provision);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetIntValues_WithNullExternalUser() {
        IntAttrName intAttrName = mock(IntAttrName.class);
        Item item = mock(Item.class);
        Any any = mock(Any.class);

        when(intAttrName.getExternalUser()).thenReturn(null);

        MappingManager.IntValues result = manager.getIntValues(
                resource,
                provision,
                item,
                intAttrName,
                AttrSchemaType.String,
                any,
                account -> null,
                (owner, schema) -> null);

        boolean condition = result != null && result.values().size() == 1;
        assertTrue(condition);
    }

    @Test
    void testGetIntValues_WithFieldKey_ReturnsResult() {
        IntAttrName intAttrName = mock(IntAttrName.class);
        Item item = mock(Item.class);
        User user = mock(User.class);

        when(intAttrName.getField()).thenReturn("key");
        when(user.getKey()).thenReturn("user-key");

        MappingManager.IntValues result = manager.getIntValues(
                resource,
                provision,
                item,
                intAttrName,
                AttrSchemaType.String,
                user,
                account -> null,
                (owner, schema) -> null);

        assertNotNull(result);
    }

    @Test
    void testGetIntValues_WithFieldUsernameAndAccountGetter_ReturnsResult() {
        IntAttrName intAttrName = mock(IntAttrName.class);
        Item item = mock(Item.class);
        User user = mock(User.class);
        Account account = mock(Account.class);

        when(intAttrName.getField()).thenReturn("username");
        when(account.getUsername()).thenReturn("linked-username");

        MappingManager.IntValues result = manager.getIntValues(
                resource,
                provision,
                item,
                intAttrName,
                AttrSchemaType.String,
                user,
                ignored -> account,
                (owner, schema) -> null);

        assertNotNull(result);
    }

    @Test
    void testPrepareAttrsFromRealm_WithNoOrgUnit_ReturnsPreparedAttrs() {
        Realm realm = mock(Realm.class);
        when(resource.getOrgUnit()).thenReturn(null);

        MappingManager.PreparedAttrs result = manager.prepareAttrsFromRealm(realm, resource);

        assertNotNull(result);
    }

    @Test
    void testPrepareAttrForRealm_WithInvalidIntAttrName_ReturnsNull() throws ParseException {
        Realm realm = mock(Realm.class);
        Item item = mock(Item.class);

        when(item.getIntAttrName()).thenReturn("invalid.realm.field");
        when(intAttrNameParser.parse(eq("invalid.realm.field"), isNull()))
                .thenThrow(new ParseException("invalid realm field", 0));

        MappingManager.PreparedAttr result = manager.prepareAttr(resource, item, realm);

        assertNull(result);
    }

    @Test
    void testSetIntValues_UserTO_WithNullAttribute_DoesNotThrow() {
        UserTO userTO = new UserTO();
        Item item = mock(Item.class);

        assertDoesNotThrow(() -> manager.setIntValues(item, null, userTO));
    }

    @Test
    void testSetIntValues_UserTO_WithKeyAttribute_SetsKey() {
        UserTO userTO = new UserTO();
        Item item = mock(Item.class);
        Attribute attr = AttributeBuilder.build("uid", "user-key");

        when(item.getIntAttrName()).thenReturn("key");

        manager.setIntValues(item, attr, userTO);

        assertEquals("user-key", userTO.getKey());
    }

    @Test
    void testSetIntValues_UserTO_WithUsernameAttribute_SetsUsername() {
        UserTO userTO = new UserTO();
        Item item = mock(Item.class);
        Attribute attr = AttributeBuilder.build("username", "vivaldi");

        when(item.getIntAttrName()).thenReturn("username");

        manager.setIntValues(item, attr, userTO);

        assertEquals("vivaldi", userTO.getUsername());
    }

    @Test
    void testSetIntValues_GroupTO_WithNameAttribute_SetsName() {
        GroupTO groupTO = new GroupTO();
        Item item = mock(Item.class);
        Attribute attr = AttributeBuilder.build("name", "engineering");

        when(item.getIntAttrName()).thenReturn("name");

        manager.setIntValues(item, attr, groupTO);

        assertEquals("engineering", groupTO.getName());
    }

    @Test
    void testSetIntValues_AnyObjectTO_WithNameAttribute_SetsName() {
        AnyObjectTO anyObjectTO = new AnyObjectTO();
        Item item = mock(Item.class);
        Attribute attr = AttributeBuilder.build("name", "printer-01");

        when(item.getIntAttrName()).thenReturn("name");

        manager.setIntValues(item, attr, anyObjectTO);

        assertEquals("printer-01", anyObjectTO.getName());
    }

    @Test
    void testSetIntValues_AnyTO_WithPlainAttribute_AddsPlainAttr() {
        AnyTO anyTO = new UserTO();
        Item item = mock(Item.class);
        Attribute attr = AttributeBuilder.build("email", "user@example.org");

        when(item.getIntAttrName()).thenReturn("email");

        manager.setIntValues(item, attr, anyTO);

        boolean condition = anyTO.getPlainAttrs().stream()
                .anyMatch(plainAttr -> "email".equals(plainAttr.getSchema()));
        assertTrue(condition);
    }

    @Test
    void testSetIntValues_RealmTO_WithFullPathAttribute_SetsFullPath() {
        RealmTO realmTO = new RealmTO();
        Item item = mock(Item.class);
        Attribute attr = AttributeBuilder.build("fullPath", "/root/test");

        when(item.getIntAttrName()).thenReturn("fullPath");

        manager.setIntValues(item, attr, realmTO);

        assertEquals("/root/test", realmTO.getFullPath());
    }

    @Test
    void testSetIntValues_RealmTO_WithPlainAttribute_AddsPlainAttr() {
        RealmTO realmTO = new RealmTO();
        Item item = mock(Item.class);
        Attribute attr = AttributeBuilder.build("description", "test realm");

        when(item.getIntAttrName()).thenReturn("description");

        manager.setIntValues(item, attr, realmTO);

        boolean condition = realmTO.getPlainAttrs().stream()
                .anyMatch(plainAttr -> "description".equals(plainAttr.getSchema()));
        assertTrue(condition);
    }

    @Test
    void testEvaluateName_WithNullConnObjectLink_UsesConnObjectKey() {
        Any any = mock(Any.class);

        when(provision.getMapping()).thenReturn(mapping);
        when(mapping.getConnObjectLink()).thenReturn(null);

        Name result = manager.exposedEvaluateNAME(any, provision, "conn-key");

        assertEquals("conn-key", result.getNameValue());
    }

    @Test
    void testEvaluateName_WithBlankConnObjectLink_UsesConnObjectKey() {
        Any any = mock(Any.class);

        when(provision.getMapping()).thenReturn(mapping);
        when(mapping.getConnObjectLink()).thenReturn(" ");

        Name result = manager.exposedEvaluateNAME(any, provision, "conn-key");

        assertEquals("conn-key", result.getNameValue());
    }

    @Test
    void testSetIntValues_WithEmptyAttributeValues_DoesNotThrow() {
        UserTO userTO = new UserTO();
        Item item = mock(Item.class);
        Attribute attr = AttributeBuilder.build("username");

        when(item.getIntAttrName()).thenReturn("username");

        assertDoesNotThrow(() -> manager.setIntValues(item, attr, userTO));
    }
}