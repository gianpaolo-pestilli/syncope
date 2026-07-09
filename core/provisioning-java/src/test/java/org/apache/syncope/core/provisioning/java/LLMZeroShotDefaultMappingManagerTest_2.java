package org.apache.syncope.core.provisioning.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.apache.syncope.common.lib.Attr;
import org.apache.syncope.common.lib.to.AnyObjectTO;
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
import org.apache.syncope.core.provisioning.api.DerAttrHandler;
import org.apache.syncope.core.provisioning.api.IntAttrNameParser;
import org.apache.syncope.core.provisioning.api.jexl.JexlTools;
import org.apache.syncope.core.provisioning.java.DefaultMappingManager;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LLMZeroShotDefaultMappingManagerTest_2 {

    private DefaultMappingManager mappingManager;

    @BeforeEach
    void setUp() {
        mappingManager = new DefaultMappingManager(
                mock(UserDAO.class),
                mock(AnyObjectDAO.class),
                mock(GroupDAO.class),
                mock(RelationshipTypeDAO.class),
                mock(RealmSearchDAO.class),
                mock(ImplementationDAO.class),
                mock(DerAttrHandler.class),
                mock(IntAttrNameParser.class),
                mock(EncryptorManager.class),
                mock(JexlTools.class));
    }

    @Test
    void hasMustChangePasswordReturnsTrueWhenMapped() {
        Item item = item("mustChangePassword", "mustChangePassword");

        Mapping mapping = new Mapping();
        mapping.add(item);

        Provision provision = new Provision();
        provision.setMapping(mapping);

        assertTrue(mappingManager.hasMustChangePassword(provision));
    }

    @Test
    void hasMustChangePasswordReturnsFalseWhenNotMapped() {
        Item item = item("username", "__NAME__");

        Mapping mapping = new Mapping();
        mapping.add(item);

        Provision provision = new Provision();
        provision.setMapping(mapping);

        assertFalse(mappingManager.hasMustChangePassword(provision));
    }

    @Test
    void setIntValuesSetsUserKey() {
        UserTO userTO = new UserTO();

        mappingManager.setIntValues(
                item("key", "uid"),
                AttributeBuilder.build("uid", "user-key-1"),
                userTO);

        assertEquals("user-key-1", userTO.getKey());
    }

    @Test
    void setIntValuesSetsUsername() {
        UserTO userTO = new UserTO();

        mappingManager.setIntValues(
                item("username", "__NAME__"),
                AttributeBuilder.build("__NAME__", "alice"),
                userTO);

        assertEquals("alice", userTO.getUsername());
    }

    @Test
    void setIntValuesSetsMustChangePassword() {
        UserTO userTO = new UserTO();

        mappingManager.setIntValues(
                item("mustChangePassword", "pwdReset"),
                AttributeBuilder.build("pwdReset", "true"),
                userTO);

        assertTrue(userTO.isMustChangePassword());
    }

    @Test
    void setIntValuesAddsPlainAttributeToUser() {
        UserTO userTO = new UserTO();

        mappingManager.setIntValues(
                item("firstname", "givenName"),
                AttributeBuilder.build("givenName", "Alice"),
                userTO);

        Attr attr = userTO.getPlainAttr("firstname").orElseThrow();
        assertEquals("firstname", attr.getSchema());
        assertEquals(List.of("Alice"), attr.getValues());
    }

    @Test
    void setIntValuesAddsMultiValuePlainAttributeToUser() {
        UserTO userTO = new UserTO();

        mappingManager.setIntValues(
                item("email", "mail"),
                AttributeBuilder.build("mail", List.of("alice@example.org", "a.smith@example.org")),
                userTO);

        Attr attr = userTO.getPlainAttr("email").orElseThrow();
        assertEquals(List.of("alice@example.org", "a.smith@example.org"), attr.getValues());
    }

    @Test
    void setIntValuesSetsAnyObjectName() {
        AnyObjectTO anyObjectTO = new AnyObjectTO();

        mappingManager.setIntValues(
                item("name", "__NAME__"),
                AttributeBuilder.build("__NAME__", "printer-01"),
                anyObjectTO);

        assertEquals("printer-01", anyObjectTO.getName());
    }

    @Test
    void setIntValuesSetsGroupName() {
        GroupTO groupTO = new GroupTO();

        mappingManager.setIntValues(
                item("name", "__NAME__"),
                AttributeBuilder.build("__NAME__", "employees"),
                groupTO);

        assertEquals("employees", groupTO.getName());
    }

    @Test
    void setIntValuesAddsPlainAttributeToRealm() {
        RealmTO realmTO = new RealmTO();

        mappingManager.setIntValues(
                item("description", "description"),
                AttributeBuilder.build("description", "Root realm"),
                realmTO);

        Attr attr = realmTO.getPlainAttr("description").orElseThrow();
        assertEquals("description", attr.getSchema());
        assertEquals(List.of("Root realm"), attr.getValues());
    }

    private static Item item(final String intAttrName, final String extAttrName) {
        Item item = new Item();
        item.setIntAttrName(intAttrName);
        item.setExtAttrName(extAttrName);
        return item;
    }
}