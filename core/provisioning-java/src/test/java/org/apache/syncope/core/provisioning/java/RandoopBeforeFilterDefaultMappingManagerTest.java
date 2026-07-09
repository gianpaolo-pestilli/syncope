package org.apache.syncope.core.provisioning.java;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RandoopBeforeFilterDefaultMappingManagerTest {
    public static boolean debug = false;

    public void assertBooleanArrayEquals(boolean[] expectedArray, boolean[] actualArray) {
        if (expectedArray.length != actualArray.length) {
            throw new AssertionError("Array lengths differ: " + expectedArray.length + " != " + actualArray.length);
        }
        for (int i = 0; i < expectedArray.length; i++) {
            if (expectedArray[i] != actualArray[i]) {
                throw new AssertionError("Arrays differ at index " + i + ": " + expectedArray[i] + " != " + actualArray[i]);
            }
        }
    }

    @Test
    public void test01() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test01");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.Realm realm11 = null;
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource12 = null;
        // The following exception was thrown during execution in test generation
        try {
            java.util.Optional<java.lang.String> strOptional13 = defaultMappingManager10.getConnObjectKeyValue(realm11, externalResource12);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.ExternalResource.getOrgUnit()\" because \"resource\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test02() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test02");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource11 = null;
        org.apache.syncope.common.lib.to.Item item12 = null;
        org.apache.syncope.core.provisioning.api.IntAttrName intAttrName13 = null;
        org.apache.syncope.common.lib.types.AttrSchemaType attrSchemaType14 = null;
        org.apache.syncope.core.persistence.api.entity.Realm realm15 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.syncope.core.provisioning.api.MappingManager.IntValues intValues16 = defaultMappingManager10.getIntValues(externalResource11, item12, intAttrName13, attrSchemaType14, realm15);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.common.lib.to.Item.getIntAttrName()\" because \"item\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test03() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test03");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.common.lib.to.Item item11 = null;
        org.identityconnectors.framework.common.objects.Attribute attribute12 = null;
        org.apache.syncope.common.lib.to.AnyTO anyTO13 = null;
        // The following exception was thrown during execution in test generation
        try {
            defaultMappingManager10.setIntValues(item11, attribute12, anyTO13);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.common.lib.to.Item.getIntAttrName()\" because \"item\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test04() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test04");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.Realm realm11 = null;
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource12 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs13 = defaultMappingManager10.prepareAttrsFromRealm(realm11, externalResource12);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.ExternalResource.getOrgUnit()\" because \"resource\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test05() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test05");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource11 = null;
        org.apache.syncope.common.lib.to.Item item12 = null;
        org.apache.syncope.core.persistence.api.entity.Realm realm13 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttr preparedAttr14 = defaultMappingManager10.prepareAttr(externalResource11, item12, realm13);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.common.lib.to.Item.getIntAttrName()\" because \"item\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test06() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test06");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.user.User user11 = null;
        org.apache.syncope.core.persistence.api.entity.user.LinkedAccount linkedAccount12 = null;
        org.apache.syncope.common.lib.to.Provision provision15 = null;
        // The following exception was thrown during execution in test generation
        try {
            java.util.Set<org.identityconnectors.framework.common.objects.Attribute> attributeSet16 = defaultMappingManager10.prepareAttrsFromLinkedAccount(user11, linkedAccount12, "", false, provision15);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.user.User.getPlainAttrs()\" because \"user\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test07() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test07");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.Any any11 = null;
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource15 = null;
        org.apache.syncope.common.lib.to.Provision provision16 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs17 = defaultMappingManager10.prepareAttrsFromAny(any11, "", true, (java.lang.Boolean) false, externalResource15, provision16);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test08() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test08");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.Any any11 = null;
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource15 = null;
        org.apache.syncope.common.lib.to.Provision provision16 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs17 = defaultMappingManager10.prepareAttrsFromAny(any11, "hi!", true, (java.lang.Boolean) true, externalResource15, provision16);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test09() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test09");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource11 = null;
        org.apache.syncope.common.lib.to.Provision provision12 = null;
        org.apache.syncope.common.lib.to.Item item13 = null;
        org.apache.syncope.core.persistence.api.entity.Any any14 = null;
        org.apache.syncope.core.provisioning.api.AccountGetter accountGetter16 = null;
        org.apache.syncope.core.provisioning.api.AccountGetter accountGetter17 = null;
        org.apache.syncope.core.provisioning.api.PlainAttrGetter plainAttrGetter18 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttr preparedAttr19 = defaultMappingManager10.prepareAttr(externalResource11, provision12, item13, any14, "", accountGetter16, accountGetter17, plainAttrGetter18);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.common.lib.to.Item.getIntAttrName()\" because \"item\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test10() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test10");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.user.User user11 = null;
        org.apache.syncope.core.persistence.api.entity.user.LinkedAccount linkedAccount12 = null;
        org.apache.syncope.common.lib.to.Provision provision15 = null;
        // The following exception was thrown during execution in test generation
        try {
            java.util.Set<org.identityconnectors.framework.common.objects.Attribute> attributeSet16 = defaultMappingManager10.prepareAttrsFromLinkedAccount(user11, linkedAccount12, "hi!", true, provision15);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.user.User.getPlainAttrs()\" because \"user\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test11() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test11");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource11 = null;
        org.apache.syncope.common.lib.to.Provision provision12 = null;
        org.apache.syncope.common.lib.to.Item item13 = null;
        org.apache.syncope.core.provisioning.api.IntAttrName intAttrName14 = null;
        org.apache.syncope.common.lib.types.AttrSchemaType attrSchemaType15 = null;
        org.apache.syncope.core.persistence.api.entity.Any any16 = null;
        org.apache.syncope.core.provisioning.api.AccountGetter accountGetter17 = null;
        org.apache.syncope.core.provisioning.api.PlainAttrGetter plainAttrGetter18 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.syncope.core.provisioning.api.MappingManager.IntValues intValues19 = defaultMappingManager10.getIntValues(externalResource11, provision12, item13, intAttrName14, attrSchemaType15, any16, accountGetter17, plainAttrGetter18);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.common.lib.to.Item.getIntAttrName()\" because \"item\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test12() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test12");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.common.lib.to.Provision provision11 = null;
        // The following exception was thrown during execution in test generation
        try {
            boolean boolean12 = defaultMappingManager10.hasMustChangePassword(provision11);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.common.lib.to.Provision.getMapping()\" because \"provision\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test13() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test13");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.user.User user11 = null;
        org.apache.syncope.core.persistence.api.entity.user.LinkedAccount linkedAccount12 = null;
        org.apache.syncope.common.lib.to.Provision provision15 = null;
        // The following exception was thrown during execution in test generation
        try {
            java.util.Set<org.identityconnectors.framework.common.objects.Attribute> attributeSet16 = defaultMappingManager10.prepareAttrsFromLinkedAccount(user11, linkedAccount12, "", true, provision15);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.user.User.getPlainAttrs()\" because \"user\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test14() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test14");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.Any any11 = null;
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource12 = null;
        org.apache.syncope.common.lib.to.Provision provision13 = null;
        // The following exception was thrown during execution in test generation
        try {
            java.util.Optional<java.lang.String> strOptional14 = defaultMappingManager10.getConnObjectKeyValue(any11, externalResource12, provision13);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.common.lib.to.Provision.getMapping()\" because \"provision\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test15() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test15");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.Any any11 = null;
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource15 = null;
        org.apache.syncope.common.lib.to.Provision provision16 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs17 = defaultMappingManager10.prepareAttrsFromAny(any11, "hi!", false, (java.lang.Boolean) true, externalResource15, provision16);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test16() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test16");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        java.lang.Class<?> wildcardClass11 = defaultMappingManager10.getClass();
        org.junit.Assert.assertNotNull(wildcardClass11);
    }

    @Test
    public void test17() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test17");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.common.lib.to.Item item11 = null;
        org.identityconnectors.framework.common.objects.Attribute attribute12 = null;
        org.apache.syncope.common.lib.to.RealmTO realmTO13 = null;
        // The following exception was thrown during execution in test generation
        try {
            defaultMappingManager10.setIntValues(item11, attribute12, realmTO13);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.common.lib.to.Item.getIntAttrName()\" because \"item\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test18() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test18");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.Any any11 = null;
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource15 = null;
        org.apache.syncope.common.lib.to.Provision provision16 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs17 = defaultMappingManager10.prepareAttrsFromAny(any11, "hi!", true, (java.lang.Boolean) false, externalResource15, provision16);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test19() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test19");
        java.lang.Object obj0 = new java.lang.Object();
        java.lang.Class<?> wildcardClass1 = obj0.getClass();
        org.junit.Assert.assertNotNull(wildcardClass1);
    }

    @Test
    public void test20() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test20");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource11 = null;
        org.apache.syncope.common.lib.to.Provision provision12 = null;
        org.apache.syncope.common.lib.to.Item item13 = null;
        org.apache.syncope.core.persistence.api.entity.Any any14 = null;
        org.apache.syncope.core.provisioning.api.AccountGetter accountGetter16 = null;
        org.apache.syncope.core.provisioning.api.AccountGetter accountGetter17 = null;
        org.apache.syncope.core.provisioning.api.PlainAttrGetter plainAttrGetter18 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttr preparedAttr19 = defaultMappingManager10.prepareAttr(externalResource11, provision12, item13, any14, "hi!", accountGetter16, accountGetter17, plainAttrGetter18);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.common.lib.to.Item.getIntAttrName()\" because \"item\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test21() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test21");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.user.User user11 = null;
        org.apache.syncope.core.persistence.api.entity.user.LinkedAccount linkedAccount12 = null;
        org.apache.syncope.common.lib.to.Provision provision15 = null;
        // The following exception was thrown during execution in test generation
        try {
            java.util.Set<org.identityconnectors.framework.common.objects.Attribute> attributeSet16 = defaultMappingManager10.prepareAttrsFromLinkedAccount(user11, linkedAccount12, "hi!", false, provision15);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.user.User.getPlainAttrs()\" because \"user\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test22() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test22");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.Any any11 = null;
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource15 = null;
        org.apache.syncope.common.lib.to.Provision provision16 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs17 = defaultMappingManager10.prepareAttrsFromAny(any11, "hi!", false, (java.lang.Boolean) false, externalResource15, provision16);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test23() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test23");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.Any any11 = null;
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource15 = null;
        org.apache.syncope.common.lib.to.Provision provision16 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs17 = defaultMappingManager10.prepareAttrsFromAny(any11, "", true, (java.lang.Boolean) true, externalResource15, provision16);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test24() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test24");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.Any any11 = null;
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource15 = null;
        org.apache.syncope.common.lib.to.Provision provision16 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs17 = defaultMappingManager10.prepareAttrsFromAny(any11, "", false, (java.lang.Boolean) false, externalResource15, provision16);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }

    @Test
    public void test25() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest.test25");
        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
        org.apache.syncope.core.persistence.api.entity.Any any11 = null;
        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource15 = null;
        org.apache.syncope.common.lib.to.Provision provision16 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs17 = defaultMappingManager10.prepareAttrsFromAny(any11, "", false, (java.lang.Boolean) true, externalResource15, provision16);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
    }
}