package org.apache.syncope.core.provisioning.java;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RandoopAfterFilterDefaultMappingManagerTest {

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
}
