//package org.apache.syncope.core.provisioning.java;
//
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.jupiter.api.Disabled;
//import org.junit.runners.MethodSorters;
//
//// COMMENTATO TUTTO perché basato su metodi che esistono solo in C2
//
//@Disabled
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//public class RandoopC2Pt3Test {
//
//    public static boolean debug = false;
//
//    public void assertBooleanArrayEquals(boolean[] expectedArray, boolean[] actualArray) {
//        if (expectedArray.length != actualArray.length) {
//            throw new AssertionError("Array lengths differ: " + expectedArray.length + " != " + actualArray.length);
//        }
//        for (int i = 0; i < expectedArray.length; i++) {
//            if (expectedArray[i] != actualArray[i]) {
//                throw new AssertionError("Arrays differ at index " + i + ": " + expectedArray[i] + " != " + actualArray[i]);
//            }
//        }
//    }
//
//    @Test
//    public void test1001() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1001");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.springframework.context.ApplicationContext applicationContext67 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext67);
//        org.apache.syncope.core.persistence.api.entity.Any any69 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource73 = null;
//        org.apache.syncope.common.lib.to.Provision provision74 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs75 = defaultMappingManager10.prepareAttrsFromAny(any69, "hi!", false, (java.lang.Boolean) true, externalResource73, provision74);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1002() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1002");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.apache.syncope.core.persistence.api.entity.Any any59 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource63 = null;
//        org.apache.syncope.common.lib.to.Provision provision64 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs65 = defaultMappingManager10.prepareAttrsFromAny(any59, "", false, (java.lang.Boolean) true, externalResource63, provision64);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1003() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1003");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.apache.syncope.core.persistence.api.entity.Any any43 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource47 = null;
//        org.apache.syncope.common.lib.to.Provision provision48 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs49 = defaultMappingManager10.prepareAttrsFromAny(any43, "hi!", false, (java.lang.Boolean) true, externalResource47, provision48);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1004() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1004");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.springframework.context.ApplicationContext applicationContext67 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext67);
//        org.springframework.context.ApplicationContext applicationContext69 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext69);
//        org.springframework.context.ApplicationContext applicationContext71 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext71);
//        org.springframework.context.ApplicationContext applicationContext73 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext73);
//        org.springframework.context.ApplicationContext applicationContext75 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext75);
//        org.springframework.context.ApplicationContext applicationContext77 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext77);
//        org.apache.syncope.core.persistence.api.entity.Any any79 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource83 = null;
//        org.apache.syncope.common.lib.to.Provision provision84 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs85 = defaultMappingManager10.prepareAttrsFromAny(any79, "hi!", false, (java.lang.Boolean) true, externalResource83, provision84);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1005() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1005");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.springframework.context.ApplicationContext applicationContext67 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext67);
//        org.springframework.context.ApplicationContext applicationContext69 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext69);
//        org.springframework.context.ApplicationContext applicationContext71 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext71);
//        org.springframework.context.ApplicationContext applicationContext73 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext73);
//        org.springframework.context.ApplicationContext applicationContext75 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext75);
//        org.springframework.context.ApplicationContext applicationContext77 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext77);
//        org.springframework.context.ApplicationContext applicationContext79 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext79);
//        org.apache.syncope.core.persistence.api.entity.Any any81 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource85 = null;
//        org.apache.syncope.common.lib.to.Provision provision86 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs87 = defaultMappingManager10.prepareAttrsFromAny(any81, "hi!", true, (java.lang.Boolean) true, externalResource85, provision86);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1006() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1006");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.springframework.context.ApplicationContext applicationContext67 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext67);
//        org.springframework.context.ApplicationContext applicationContext69 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext69);
//        org.springframework.context.ApplicationContext applicationContext71 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext71);
//        org.springframework.context.ApplicationContext applicationContext73 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext73);
//        org.springframework.context.ApplicationContext applicationContext75 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext75);
//        org.springframework.context.ApplicationContext applicationContext77 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext77);
//        org.springframework.context.ApplicationContext applicationContext79 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext79);
//        org.springframework.context.ApplicationContext applicationContext81 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext81);
//        org.springframework.context.ApplicationContext applicationContext83 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext83);
//        org.springframework.context.ApplicationContext applicationContext85 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext85);
//        org.springframework.context.ApplicationContext applicationContext87 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext87);
//        org.springframework.context.ApplicationContext applicationContext89 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext89);
//        org.apache.syncope.core.persistence.api.entity.Any any91 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource95 = null;
//        org.apache.syncope.common.lib.to.Provision provision96 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs97 = defaultMappingManager10.prepareAttrsFromAny(any91, "hi!", true, (java.lang.Boolean) false, externalResource95, provision96);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1007() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1007");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.springframework.context.ApplicationContext applicationContext67 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext67);
//        org.springframework.context.ApplicationContext applicationContext69 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext69);
//        org.springframework.context.ApplicationContext applicationContext71 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext71);
//        org.springframework.context.ApplicationContext applicationContext73 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext73);
//        org.springframework.context.ApplicationContext applicationContext75 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext75);
//        org.springframework.context.ApplicationContext applicationContext77 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext77);
//        org.springframework.context.ApplicationContext applicationContext79 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext79);
//        org.springframework.context.ApplicationContext applicationContext81 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext81);
//        org.springframework.context.ApplicationContext applicationContext83 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext83);
//        org.springframework.context.ApplicationContext applicationContext85 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext85);
//        org.apache.syncope.core.persistence.api.entity.Any any87 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource91 = null;
//        org.apache.syncope.common.lib.to.Provision provision92 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs93 = defaultMappingManager10.prepareAttrsFromAny(any87, "", false, (java.lang.Boolean) false, externalResource91, provision92);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1008() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1008");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.springframework.context.ApplicationContext applicationContext67 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext67);
//        org.springframework.context.ApplicationContext applicationContext69 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext69);
//        org.springframework.context.ApplicationContext applicationContext71 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext71);
//        org.springframework.context.ApplicationContext applicationContext73 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext73);
//        org.springframework.context.ApplicationContext applicationContext75 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext75);
//        org.springframework.context.ApplicationContext applicationContext77 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext77);
//        org.apache.syncope.core.persistence.api.entity.Any any79 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource83 = null;
//        org.apache.syncope.common.lib.to.Provision provision84 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs85 = defaultMappingManager10.prepareAttrsFromAny(any79, "", false, (java.lang.Boolean) true, externalResource83, provision84);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1009() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1009");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.springframework.context.ApplicationContext applicationContext67 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext67);
//        org.springframework.context.ApplicationContext applicationContext69 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext69);
//        org.springframework.context.ApplicationContext applicationContext71 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext71);
//        org.springframework.context.ApplicationContext applicationContext73 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext73);
//        org.springframework.context.ApplicationContext applicationContext75 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext75);
//        org.springframework.context.ApplicationContext applicationContext77 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext77);
//        org.apache.syncope.core.persistence.api.entity.Any any79 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource83 = null;
//        org.apache.syncope.common.lib.to.Provision provision84 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs85 = defaultMappingManager10.prepareAttrsFromAny(any79, "hi!", false, (java.lang.Boolean) false, externalResource83, provision84);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1010() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1010");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.springframework.context.ApplicationContext applicationContext67 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext67);
//        org.springframework.context.ApplicationContext applicationContext69 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext69);
//        org.springframework.context.ApplicationContext applicationContext71 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext71);
//        org.springframework.context.ApplicationContext applicationContext73 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext73);
//        org.springframework.context.ApplicationContext applicationContext75 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext75);
//        org.springframework.context.ApplicationContext applicationContext77 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext77);
//        org.springframework.context.ApplicationContext applicationContext79 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext79);
//        org.springframework.context.ApplicationContext applicationContext81 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext81);
//        org.springframework.context.ApplicationContext applicationContext83 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext83);
//        org.apache.syncope.core.persistence.api.entity.Any any85 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource89 = null;
//        org.apache.syncope.common.lib.to.Provision provision90 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs91 = defaultMappingManager10.prepareAttrsFromAny(any85, "hi!", false, (java.lang.Boolean) true, externalResource89, provision90);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1011() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1011");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.springframework.context.ApplicationContext applicationContext67 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext67);
//        org.apache.syncope.core.persistence.api.entity.Any any69 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource73 = null;
//        org.apache.syncope.common.lib.to.Provision provision74 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs75 = defaultMappingManager10.prepareAttrsFromAny(any69, "", true, (java.lang.Boolean) true, externalResource73, provision74);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1012() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1012");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.springframework.context.ApplicationContext applicationContext67 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext67);
//        org.springframework.context.ApplicationContext applicationContext69 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext69);
//        org.springframework.context.ApplicationContext applicationContext71 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext71);
//        org.springframework.context.ApplicationContext applicationContext73 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext73);
//        org.springframework.context.ApplicationContext applicationContext75 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext75);
//        org.springframework.context.ApplicationContext applicationContext77 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext77);
//        org.springframework.context.ApplicationContext applicationContext79 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext79);
//        org.springframework.context.ApplicationContext applicationContext81 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext81);
//        org.springframework.context.ApplicationContext applicationContext83 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext83);
//        org.springframework.context.ApplicationContext applicationContext85 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext85);
//        org.apache.syncope.core.persistence.api.entity.Any any87 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource91 = null;
//        org.apache.syncope.common.lib.to.Provision provision92 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs93 = defaultMappingManager10.prepareAttrsFromAny(any87, "hi!", true, (java.lang.Boolean) true, externalResource91, provision92);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1013() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1013");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.springframework.context.ApplicationContext applicationContext67 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext67);
//        org.springframework.context.ApplicationContext applicationContext69 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext69);
//        org.springframework.context.ApplicationContext applicationContext71 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext71);
//        org.springframework.context.ApplicationContext applicationContext73 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext73);
//        org.springframework.context.ApplicationContext applicationContext75 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext75);
//        org.springframework.context.ApplicationContext applicationContext77 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext77);
//        org.springframework.context.ApplicationContext applicationContext79 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext79);
//        org.springframework.context.ApplicationContext applicationContext81 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext81);
//        org.springframework.context.ApplicationContext applicationContext83 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext83);
//        org.springframework.context.ApplicationContext applicationContext85 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext85);
//        org.springframework.context.ApplicationContext applicationContext87 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext87);
//        org.springframework.context.ApplicationContext applicationContext89 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext89);
//        org.springframework.context.ApplicationContext applicationContext91 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext91);
//        org.apache.syncope.core.persistence.api.entity.Any any93 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource97 = null;
//        org.apache.syncope.common.lib.to.Provision provision98 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs99 = defaultMappingManager10.prepareAttrsFromAny(any93, "hi!", true, (java.lang.Boolean) true, externalResource97, provision98);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1014() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1014");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.springframework.context.ApplicationContext applicationContext67 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext67);
//        org.springframework.context.ApplicationContext applicationContext69 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext69);
//        org.springframework.context.ApplicationContext applicationContext71 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext71);
//        org.springframework.context.ApplicationContext applicationContext73 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext73);
//        org.springframework.context.ApplicationContext applicationContext75 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext75);
//        org.apache.syncope.core.persistence.api.entity.Any any77 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource81 = null;
//        org.apache.syncope.common.lib.to.Provision provision82 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs83 = defaultMappingManager10.prepareAttrsFromAny(any77, "hi!", true, (java.lang.Boolean) true, externalResource81, provision82);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1015() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1015");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.springframework.context.ApplicationContext applicationContext67 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext67);
//        org.springframework.context.ApplicationContext applicationContext69 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext69);
//        org.springframework.context.ApplicationContext applicationContext71 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext71);
//        org.springframework.context.ApplicationContext applicationContext73 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext73);
//        org.springframework.context.ApplicationContext applicationContext75 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext75);
//        org.springframework.context.ApplicationContext applicationContext77 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext77);
//        org.springframework.context.ApplicationContext applicationContext79 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext79);
//        org.springframework.context.ApplicationContext applicationContext81 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext81);
//        org.springframework.context.ApplicationContext applicationContext83 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext83);
//        org.springframework.context.ApplicationContext applicationContext85 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext85);
//        org.apache.syncope.core.persistence.api.entity.Any any87 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource91 = null;
//        org.apache.syncope.common.lib.to.Provision provision92 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs93 = defaultMappingManager10.prepareAttrsFromAny(any87, "hi!", false, (java.lang.Boolean) false, externalResource91, provision92);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1016() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1016");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.springframework.context.ApplicationContext applicationContext67 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext67);
//        org.springframework.context.ApplicationContext applicationContext69 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext69);
//        org.springframework.context.ApplicationContext applicationContext71 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext71);
//        org.springframework.context.ApplicationContext applicationContext73 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext73);
//        org.springframework.context.ApplicationContext applicationContext75 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext75);
//        org.springframework.context.ApplicationContext applicationContext77 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext77);
//        org.springframework.context.ApplicationContext applicationContext79 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext79);
//        org.springframework.context.ApplicationContext applicationContext81 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext81);
//        org.springframework.context.ApplicationContext applicationContext83 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext83);
//        org.springframework.context.ApplicationContext applicationContext85 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext85);
//        org.apache.syncope.core.persistence.api.entity.Any any87 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource91 = null;
//        org.apache.syncope.common.lib.to.Provision provision92 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs93 = defaultMappingManager10.prepareAttrsFromAny(any87, "hi!", false, (java.lang.Boolean) true, externalResource91, provision92);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1017() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1017");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.springframework.context.ApplicationContext applicationContext67 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext67);
//        org.springframework.context.ApplicationContext applicationContext69 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext69);
//        org.springframework.context.ApplicationContext applicationContext71 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext71);
//        org.springframework.context.ApplicationContext applicationContext73 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext73);
//        org.springframework.context.ApplicationContext applicationContext75 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext75);
//        org.apache.syncope.core.persistence.api.entity.Any any77 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource81 = null;
//        org.apache.syncope.common.lib.to.Provision provision82 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs83 = defaultMappingManager10.prepareAttrsFromAny(any77, "", true, (java.lang.Boolean) true, externalResource81, provision82);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1018() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1018");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.springframework.context.ApplicationContext applicationContext61 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext61);
//        org.springframework.context.ApplicationContext applicationContext63 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext63);
//        org.springframework.context.ApplicationContext applicationContext65 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext65);
//        org.apache.syncope.core.persistence.api.entity.Any any67 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource71 = null;
//        org.apache.syncope.common.lib.to.Provision provision72 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs73 = defaultMappingManager10.prepareAttrsFromAny(any67, "", true, (java.lang.Boolean) false, externalResource71, provision72);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//
//    @Test
//    public void test1019() throws Throwable {
//        if (debug)
//            System.out.format("%n%s%n", "RegressionTest2.test1019");
//        org.apache.syncope.core.persistence.api.dao.UserDAO userDAO0 = null;
//        org.apache.syncope.core.persistence.api.dao.AnyObjectDAO anyObjectDAO1 = null;
//        org.apache.syncope.core.persistence.api.dao.GroupDAO groupDAO2 = null;
//        org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO relationshipTypeDAO3 = null;
//        org.apache.syncope.core.persistence.api.dao.RealmSearchDAO realmSearchDAO4 = null;
//        org.apache.syncope.core.persistence.api.dao.ImplementationDAO implementationDAO5 = null;
//        org.apache.syncope.core.provisioning.api.DerAttrHandler derAttrHandler6 = null;
//        org.apache.syncope.core.provisioning.api.IntAttrNameParser intAttrNameParser7 = null;
//        org.apache.syncope.core.persistence.api.EncryptorManager encryptorManager8 = null;
//        org.apache.syncope.core.provisioning.api.jexl.JexlTools jexlTools9 = null;
//        org.apache.syncope.core.provisioning.java.DefaultMappingManager defaultMappingManager10 = new org.apache.syncope.core.provisioning.java.DefaultMappingManager(userDAO0, anyObjectDAO1, groupDAO2, relationshipTypeDAO3, realmSearchDAO4, implementationDAO5, derAttrHandler6, intAttrNameParser7, encryptorManager8, jexlTools9);
//        org.springframework.context.ApplicationContext applicationContext11 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext11);
//        org.springframework.context.ApplicationContext applicationContext13 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext13);
//        org.springframework.context.ApplicationContext applicationContext15 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext15);
//        org.springframework.context.ApplicationContext applicationContext17 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext17);
//        org.springframework.context.ApplicationContext applicationContext19 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext19);
//        org.springframework.context.ApplicationContext applicationContext21 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext21);
//        org.springframework.context.ApplicationContext applicationContext23 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext23);
//        org.springframework.context.ApplicationContext applicationContext25 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext25);
//        org.springframework.context.ApplicationContext applicationContext27 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext27);
//        org.springframework.context.ApplicationContext applicationContext29 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext29);
//        org.springframework.context.ApplicationContext applicationContext31 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext31);
//        org.springframework.context.ApplicationContext applicationContext33 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext33);
//        org.springframework.context.ApplicationContext applicationContext35 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext35);
//        org.springframework.context.ApplicationContext applicationContext37 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext37);
//        org.springframework.context.ApplicationContext applicationContext39 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext39);
//        org.springframework.context.ApplicationContext applicationContext41 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext41);
//        org.springframework.context.ApplicationContext applicationContext43 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext43);
//        org.springframework.context.ApplicationContext applicationContext45 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext45);
//        org.springframework.context.ApplicationContext applicationContext47 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext47);
//        org.springframework.context.ApplicationContext applicationContext49 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext49);
//        org.springframework.context.ApplicationContext applicationContext51 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext51);
//        org.springframework.context.ApplicationContext applicationContext53 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext53);
//        org.springframework.context.ApplicationContext applicationContext55 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext55);
//        org.springframework.context.ApplicationContext applicationContext57 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext57);
//        org.springframework.context.ApplicationContext applicationContext59 = null;
//        defaultMappingManager10.setApplicationContext(applicationContext59);
//        org.apache.syncope.core.persistence.api.entity.Any any61 = null;
//        org.apache.syncope.core.persistence.api.entity.ExternalResource externalResource65 = null;
//        org.apache.syncope.common.lib.to.Provision provision66 = null;
//        // The following exception was thrown during execution in test generation
//        try {
//            org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttrs preparedAttrs67 = defaultMappingManager10.prepareAttrsFromAny(any61, "hi!", true, (java.lang.Boolean) false, externalResource65, provision66);
//            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: Cannot invoke \"org.apache.syncope.core.persistence.api.entity.Any.getPlainAttrs()\" because \"any\" is null");
//        } catch (java.lang.NullPointerException e) {
//            // Expected exception.
//        }
//    }
//}
//
