package org.apache.syncope.core.provisioning.java;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.apache.syncope.common.lib.Attr;
import org.apache.syncope.common.lib.to.AnyObjectTO;
import org.apache.syncope.common.lib.to.AnyTO;
import org.apache.syncope.common.lib.to.GroupTO;
import org.apache.syncope.common.lib.to.Item;
import org.apache.syncope.common.lib.to.Mapping;
import org.apache.syncope.common.lib.to.Provision;
import org.apache.syncope.common.lib.to.RealmTO;
import org.apache.syncope.common.lib.to.UserTO;
import org.apache.syncope.core.persistence.api.EncryptorManager;
import org.apache.syncope.core.persistence.api.dao.AnyObjectDAO;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.persistence.api.dao.ImplementationDAO;
import org.apache.syncope.core.persistence.api.dao.RealmSearchDAO;
import org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.provisioning.api.DerAttrHandler;
import org.apache.syncope.core.provisioning.api.IntAttrNameParser;
import org.apache.syncope.core.provisioning.api.jexl.JexlTools;
import org.apache.syncope.core.provisioning.java.DefaultMappingManager;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
 * Note:
 * Mapping does not expose setItems(...). Use mapping.getItems().add(item)
 * or mapping.add(item), depending on the Syncope version.
 *
 * The DefaultMappingManager constructor and setIntValues / getName signatures
 * are available in Apache Syncope API docs. [1](https://syncope.apache.org/apidocs/4.1/org/apache/syncope/core/provisioning/java/DefaultMappingManager.html)
 */
class LLMFewShotDefaultMappingManagerTest_1 {

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

    @Test
    void testConstructor_WithAllDependencies_CreatesInstance() {
        assertNotNull(manager);
    }

    @Test
    void testHasMustChangePassword_WithNullMapping_ReturnsFalse() {
        Provision provision = new Provision();

        boolean result = manager.hasMustChangePassword(provision);

        assertFalse(result);
    }

    @Test
    void testHasMustChangePassword_WithEmptyMapping_ReturnsFalse() {
        Provision provision = new Provision();
        provision.setMapping(new Mapping());

        boolean result = manager.hasMustChangePassword(provision);

        assertFalse(result);
    }

    @Test
    void testHasMustChangePassword_WithNonMatchingItem_ReturnsFalse() {
        Item item = new Item();
        item.setIntAttrName("username");

        Mapping mapping = mappingWithItems(item);

        Provision provision = new Provision();
        provision.setMapping(mapping);

        boolean result = manager.hasMustChangePassword(provision);

        assertFalse(result);
    }

    @Test
    void testHasMustChangePassword_WithMatchingItem_ReturnsTrue() {
        Item item = new Item();
        item.setIntAttrName("mustChangePassword");

        Mapping mapping = mappingWithItems(item);

        Provision provision = new Provision();
        provision.setMapping(mapping);

        boolean result = manager.hasMustChangePassword(provision);

        assertTrue(result);
    }

    @Test
    void testHasMustChangePassword_WithMultipleItemsAndOneMatching_ReturnsTrue() {
        Item username = new Item();
        username.setIntAttrName("username");

        Item mustChangePassword = new Item();
        mustChangePassword.setIntAttrName("mustChangePassword");

        Mapping mapping = mappingWithItems(username, mustChangePassword);

        Provision provision = new Provision();
        provision.setMapping(mapping);

        boolean result = manager.hasMustChangePassword(provision);

        assertTrue(result);
    }

    @Test
    void testSetIntValues_UserTOKeyAttribute_SetsKey() {
        UserTO userTO = new UserTO();

        Item item = new Item();
        item.setIntAttrName("key");

        Attribute attr = AttributeBuilder.build("uid", "user-001");

        manager.setIntValues(item, attr, userTO);

        assertEquals("user-001", userTO.getKey());
    }

    @Test
    void testSetIntValues_UserTOUsernameAttribute_SetsUsername() {
        UserTO userTO = new UserTO();

        Item item = new Item();
        item.setIntAttrName("username");

        Attribute attr = AttributeBuilder.build("username", "rossini");

        manager.setIntValues(item, attr, userTO);

        assertEquals("rossini", userTO.getUsername());
    }

    @Test
    void testSetIntValues_UserTOPasswordAttribute_SetsPassword() {
        UserTO userTO = new UserTO();

        Item item = new Item();
        item.setIntAttrName("password");

        Attribute attr = AttributeBuilder.buildPassword("secret".toCharArray());

        manager.setIntValues(item, attr, userTO);

        assertEquals("secret", userTO.getPassword());
    }

    @Test
    void testSetIntValues_UserTOPlainAttribute_AddsPlainAttr() {
        UserTO userTO = new UserTO();

        Item item = new Item();
        item.setIntAttrName("email");

        Attribute attr = AttributeBuilder.build("mail", "user@example.org");

        manager.setIntValues(item, attr, userTO);

        Optional<Attr> email = userTO.getPlainAttr("email");
        assertTrue(email.isPresent());
        assertEquals(List.of("user@example.org"), email.get().getValues());
    }

    @Test
    void testSetIntValues_UserTOPlainAttributeWithMultipleValues_AddsAllValues() {
        UserTO userTO = new UserTO();

        Item item = new Item();
        item.setIntAttrName("phoneNumbers");

        Attribute attr = AttributeBuilder.build("phones", "+3900001", "+3900002");

        manager.setIntValues(item, attr, userTO);

        Optional<Attr> phones = userTO.getPlainAttr("phoneNumbers");
        assertTrue(phones.isPresent());
        assertEquals(List.of("+3900001", "+3900002"), phones.get().getValues());
    }

    @Test
    void testSetIntValues_AnyObjectTOKeyAttribute_SetsKey() {
        AnyObjectTO anyObjectTO = new AnyObjectTO();

        Item item = new Item();
        item.setIntAttrName("key");

        Attribute attr = AttributeBuilder.build("id", "printer-001");

        manager.setIntValues(item, attr, anyObjectTO);

        assertEquals("printer-001", anyObjectTO.getKey());
    }

    @Test
    void testSetIntValues_AnyObjectTONameAttribute_SetsName() {
        AnyObjectTO anyObjectTO = new AnyObjectTO();

        Item item = new Item();
        item.setIntAttrName("name");

        Attribute attr = AttributeBuilder.build("cn", "network-printer");

        manager.setIntValues(item, attr, anyObjectTO);

        assertEquals("network-printer", anyObjectTO.getName());
    }

    @Test
    void testSetIntValues_AnyObjectTOPlainAttribute_AddsPlainAttr() {
        AnyObjectTO anyObjectTO = new AnyObjectTO();

        Item item = new Item();
        item.setIntAttrName("serialNumber");

        Attribute attr = AttributeBuilder.build("serial", "SN-123456");

        manager.setIntValues(item, attr, anyObjectTO);

        Optional<Attr> serialNumber = anyObjectTO.getPlainAttr("serialNumber");
        assertTrue(serialNumber.isPresent());
        assertEquals(List.of("SN-123456"), serialNumber.get().getValues());
    }

    @Test
    void testSetIntValues_GroupTOKeyAttribute_SetsKey() {
        GroupTO groupTO = new GroupTO();

        Item item = new Item();
        item.setIntAttrName("key");

        Attribute attr = AttributeBuilder.build("gid", "group-001");

        manager.setIntValues(item, attr, groupTO);

        assertEquals("group-001", groupTO.getKey());
    }

    @Test
    void testSetIntValues_GroupTONameAttribute_SetsName() {
        GroupTO groupTO = new GroupTO();

        Item item = new Item();
        item.setIntAttrName("name");

        Attribute attr = AttributeBuilder.build("cn", "developers");

        manager.setIntValues(item, attr, groupTO);

        assertEquals("developers", groupTO.getName());
    }

    @Test
    void testSetIntValues_GroupTOPlainAttribute_AddsPlainAttr() {
        GroupTO groupTO = new GroupTO();

        Item item = new Item();
        item.setIntAttrName("description");

        Attribute attr = AttributeBuilder.build("description", "Development team");

        manager.setIntValues(item, attr, groupTO);

        Optional<Attr> description = groupTO.getPlainAttr("description");
        assertTrue(description.isPresent());
        assertEquals(List.of("Development team"), description.get().getValues());
    }

    @Test
    void testSetIntValues_RealmTOKeyAttribute_SetsKey() {
        RealmTO realmTO = new RealmTO();

        Item item = new Item();
        item.setIntAttrName("key");

        Attribute attr = AttributeBuilder.build("realmKey", "realm-001");

        manager.setIntValues(item, attr, realmTO);

        assertEquals("realm-001", realmTO.getKey());
    }

    @Test
    void testSetIntValues_RealmTONameAttribute_SetsName() {
        RealmTO realmTO = new RealmTO();

        Item item = new Item();
        item.setIntAttrName("name");

        Attribute attr = AttributeBuilder.build("name", "engineering");

        manager.setIntValues(item, attr, realmTO);

        assertEquals("engineering", realmTO.getName());
    }

    @Test
    void testSetIntValues_RealmTOFullPathAttribute_SetsFullPath() {
        RealmTO realmTO = new RealmTO();

        Item item = new Item();
        item.setIntAttrName("fullPath");

        Attribute attr = AttributeBuilder.build("path", "/root/engineering");

        manager.setIntValues(item, attr, realmTO);

        assertEquals("/root/engineering", realmTO.getFullPath());
    }

    @Test
    void testSetIntValues_RealmTOPlainAttribute_AddsPlainAttr() {
        RealmTO realmTO = new RealmTO();

        Item item = new Item();
        item.setIntAttrName("displayName");

        Attribute attr = AttributeBuilder.build("displayName", "Engineering Realm");

        manager.setIntValues(item, attr, realmTO);

        Optional<Attr> displayName = realmTO.getPlainAttr("displayName");
        assertTrue(displayName.isPresent());
        assertEquals(List.of("Engineering Realm"), displayName.get().getValues());
    }

    @Test
    void testSetIntValues_NullOrEmptyAttributeValues_DoesNotFail() {
        UserTO userTO = new UserTO();

        Item item = new Item();
        item.setIntAttrName("email");

        Attribute attr = AttributeBuilder.build("mail");

        assertDoesNotThrow(() -> manager.setIntValues(item, attr, userTO));
    }

    @Test
    void testSetIntValues_UnsupportedInternalAttribute_AddsPlainAttribute() {
        AnyTO anyTO = new UserTO();

        Item item = new Item();
        item.setIntAttrName("customAttr");

        Attribute attr = AttributeBuilder.build("custom", "custom-value");

        manager.setIntValues(item, attr, anyTO);

        Optional<Attr> custom = anyTO.getPlainAttr("customAttr");
        assertTrue(custom.isPresent());
        assertEquals(List.of("custom-value"), custom.get().getValues());
    }

    @Test
    void testGetName_WithNonBlankEvaluatedConnObjectLink_UsesEvaluatedValue() {
        Name result = DefaultMappingManager.getName("uid=user-001,ou=people", "fallback-key");

        assertNotNull(result);
        assertEquals("uid=user-001,ou=people", result.getNameValue());
    }

    @Test
    void testGetName_WithBlankEvaluatedConnObjectLink_UsesConnObjectKey() {
        Name result = DefaultMappingManager.getName("   ", "fallback-key");

        assertNotNull(result);
        assertEquals("fallback-key", result.getNameValue());
    }

    @Test
    void testGetName_WithNullEvaluatedConnObjectLink_UsesConnObjectKey() {
        Name result = DefaultMappingManager.getName(null, "fallback-key");

        assertNotNull(result);
        assertEquals("fallback-key", result.getNameValue());
    }

    @Test
    void testGetConnObjectKeyValue_WithNullProvision_ThrowsException() {
        ExternalResource resource = mock(ExternalResource.class);

        assertThrows(RuntimeException.class, () -> manager.getConnObjectKeyValue(null, resource, null));
    }

    @Test
    void testPrepareAttrsFromAny_WithNullProvision_ThrowsException() {
        ExternalResource resource = mock(ExternalResource.class);

        assertThrows(RuntimeException.class, () ->
                manager.prepareAttrsFromAny(null, null, false, null, resource, null));
    }

    @Test
    void testPrepareAttrsFromRealm_WithResourceHavingNoOrgUnit_ThrowsException() {
        ExternalResource resource = mock(ExternalResource.class);
        when(resource.getOrgUnit()).thenReturn(null);

        assertThrows(RuntimeException.class, () -> manager.prepareAttrsFromRealm(null, resource));
    }

    private static Mapping mappingWithItems(final Item... items) {
        Mapping mapping = new Mapping();

        for (Item item : items) {
            mapping.getItems().add(item);
        }

        return mapping;
    }
}