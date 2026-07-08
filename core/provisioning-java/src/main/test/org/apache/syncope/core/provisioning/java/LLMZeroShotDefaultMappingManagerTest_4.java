/*
 * Boundary-value and edge-case tests for DefaultMappingManager.
 *
 * Notes:
 * - DefaultMappingManager exposes setIntValues overloads for both AnyTO and RealmTO, so null TO
 *   parameters are explicitly cast to avoid ambiguous method calls. The overloads and constructor
 *   signature are documented in the Apache Syncope 4.1 API. [1](https://syncope.apache.org/apidocs/4.1/org/apache/syncope/core/provisioning/java/DefaultMappingManager.html)[1](https://syncope.apache.org/apidocs/4.1/org/apache/syncope/core/provisioning/java/DefaultMappingManager.html)
 * - processPreparedAttr is a protected static method and getName is a protected static method, so
 *   these tests invoke them with reflection. Their signatures are documented in the API. [1](https://syncope.apache.org/apidocs/4.1/org/apache/syncope/core/provisioning/java/DefaultMappingManager.html)
 * - PreparedAttr is used with constructor arguments compatible with MappingManager.PreparedAttr
 *   as exposed by the Syncope MappingManager API. [2](https://syncope.apache.org/apidocs/4.1/org/apache/syncope/core/provisioning/api/MappingManager.html)
 */
package org.apache.syncope.core.provisioning.java;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.syncope.common.lib.Attr;
import org.apache.syncope.common.lib.to.AnyObjectTO;
import org.apache.syncope.common.lib.to.AnyTO;
import org.apache.syncope.common.lib.to.GroupTO;
import org.apache.syncope.common.lib.to.Item;
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
import org.apache.syncope.core.provisioning.api.MappingManager;
import org.apache.syncope.core.provisioning.api.jexl.JexlTools;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LLMZeroShotDefaultMappingManagerTest_4 {

    private DefaultMappingManager manager;

    @BeforeEach
    void setUp() {
        manager = new DefaultMappingManager(
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

    private static Item item(final String intAttrName, final String extAttrName) {
        Item item = new Item();
        item.setIntAttrName(intAttrName);
        item.setExtAttrName(extAttrName);
        return item;
    }

    private static Attribute attr(final String name, final Object... values) {
        return AttributeBuilder.build(name, values);
    }

    private static Optional<String> invokeGetNameValue(
            final String evalConnObjectLink,
            final String connObjectKey) throws Exception {

        Method method = DefaultMappingManager.class.getDeclaredMethod(
                "getName",
                String.class,
                String.class);
        method.setAccessible(true);

        Name name = (Name) method.invoke(null, evalConnObjectLink, connObjectKey);
        return name == null
                ? Optional.empty()
                : Optional.ofNullable(name.getNameValue());
    }

    private static Optional<String> invokeProcessPreparedAttr(
            final MappingManager.PreparedAttr preparedAttr,
            final Set<Attribute> attributes) throws Exception {

        Method method = DefaultMappingManager.class.getDeclaredMethod(
                "processPreparedAttr",
                MappingManager.PreparedAttr.class,
                Set.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Optional<String> result = (Optional<String>) method.invoke(null, preparedAttr, attributes);
        return result;
    }

    @Test
    @DisplayName("01 - getName: null link and null connObjectKey returns empty")
    void getNameNullNull() throws Exception {
        assertTrue(invokeGetNameValue(null, null).isEmpty());
    }

    @Test
    @DisplayName("02 - getName: empty link falls back to one-character connObjectKey")
    void getNameEmptyLinkOneCharKey() throws Exception {
        assertEquals(Optional.of("x"), invokeGetNameValue("", "x"));
    }

    @Test
    @DisplayName("03 - getName: blank link falls back to blank connObjectKey consistently")
    void getNameBlankLinkBlankKey() throws Exception {
        assertEquals(Optional.of(" "), invokeGetNameValue(" ", " "));
    }

    @Test
    @DisplayName("04 - getName: non-empty link has priority over connObjectKey")
    void getNameLinkOverridesKey() throws Exception {
        assertEquals(Optional.of("linked-name"), invokeGetNameValue("linked-name", "key-name"));
    }

    @Test
    @DisplayName("05 - getName: empty link with empty connObjectKey is preserved")
    void getNameEmptyLinkEmptyKey() throws Exception {
        assertEquals(Optional.of(""), invokeGetNameValue("", ""));
    }

    @Test
    @DisplayName("06 - setIntValues AnyTO: null attribute is rejected")
    void setIntValuesAnyTONullAttribute() {
        assertThrows(NullPointerException.class,
                () -> manager.setIntValues(
                        item("username", "__NAME__"),
                        null,
                        new UserTO()));
    }

    @Test
    @DisplayName("07 - setIntValues AnyTO: null item is rejected")
    void setIntValuesAnyTONullItem() {
        assertThrows(NullPointerException.class,
                () -> manager.setIntValues(
                        null,
                        attr("__NAME__", "alice"),
                        new UserTO()));
    }

    @Test
    @DisplayName("08 - setIntValues AnyTO: null target is rejected with explicit overload selection")
    void setIntValuesAnyTONullAnyTO() {
        assertThrows(NullPointerException.class,
                () -> manager.setIntValues(
                        item("username", "__NAME__"),
                        attr("__NAME__", "alice"),
                        (AnyTO) null));
    }

    @Test
    @DisplayName("09 - setIntValues UserTO: one value maps to username lower boundary")
    void setIntValuesUserNameSingleValue() {
        UserTO userTO = new UserTO();

        manager.setIntValues(
                item("username", "__NAME__"),
                attr("__NAME__", "a"),
                userTO);

        assertEquals("a", userTO.getUsername());
    }

    @Test
    @DisplayName("10 - setIntValues UserTO: zero values leaves username unset")
    void setIntValuesUserNameZeroValues() {
        UserTO userTO = new UserTO();

        manager.setIntValues(
                item("username", "__NAME__"),
                AttributeBuilder.build("__NAME__"),
                userTO);

        assertNull(userTO.getUsername());
    }

    @Test
    @DisplayName("11 - setIntValues UserTO: multiple values uses first value")
    void setIntValuesUserNameMultipleValuesFirstWins() {
        UserTO userTO = new UserTO();

        manager.setIntValues(
                item("username", "__NAME__"),
                attr("__NAME__", "first", "second"),
                userTO);

        assertEquals("first", userTO.getUsername());
    }

    @Test
    @DisplayName("12 - setIntValues GroupTO: one-character group name")
    void setIntValuesGroupNameOneCharacter() {
        GroupTO groupTO = new GroupTO();

        manager.setIntValues(
                item("name", "__NAME__"),
                attr("__NAME__", "g"),
                groupTO);

        assertEquals("g", groupTO.getName());
    }

    @Test
    @DisplayName("13 - setIntValues AnyObjectTO: empty name boundary")
    void setIntValuesAnyObjectNameEmptyString() {
        AnyObjectTO anyObjectTO = new AnyObjectTO();

        manager.setIntValues(
                item("name", "__NAME__"),
                attr("__NAME__", ""),
                anyObjectTO);

        assertEquals("", anyObjectTO.getName());
    }

    @Test
    @DisplayName("14 - setIntValues AnyTO: one plain attribute value creates exactly one Attr")
    void setIntValuesPlainAttrSingleValue() {
        UserTO userTO = new UserTO();

        manager.setIntValues(
                item("email", "mail"),
                attr("mail", "a@example.org"),
                userTO);

        assertEquals(1, userTO.getPlainAttrs().size());

        Attr plain = userTO.getPlainAttr("email").orElseThrow();
        assertEquals("email", plain.getSchema());
        assertEquals(1, plain.getValues().size());
        assertEquals("a@example.org", plain.getValues().get(0));
    }

    @Test
    @DisplayName("15 - setIntValues AnyTO: duplicate plain values preserve connector cardinality")
    void setIntValuesPlainAttrDuplicateValues() {
        UserTO userTO = new UserTO();

        manager.setIntValues(
                item("email", "mail"),
                attr("mail", "dup@example.org", "dup@example.org"),
                userTO);

        Attr plain = userTO.getPlainAttr("email").orElseThrow();
        assertEquals(2, plain.getValues().size());
        assertEquals("dup@example.org", plain.getValues().get(0));
        assertEquals("dup@example.org", plain.getValues().get(1));
    }

    @Test
    @DisplayName("16 - setIntValues AnyTO: zero-value plain attribute creates empty value list")
    void setIntValuesPlainAttrZeroValues() {
        UserTO userTO = new UserTO();

        manager.setIntValues(
                item("emptySchema", "emptyExternal"),
                AttributeBuilder.build("emptyExternal"),
                userTO);

        Attr plain = userTO.getPlainAttr("emptySchema").orElseThrow();
        assertTrue(plain.getValues().isEmpty());
    }

    @Test
    @DisplayName("17 - setIntValues AnyTO: null connector value does not corrupt target")
    void setIntValuesPlainAttrNullValueDoesNotCorrupt() {
        UserTO userTO = new UserTO();

        assertDoesNotThrow(() -> manager.setIntValues(
                item("nullableSchema", "nullableExternal"),
                AttributeBuilder.build("nullableExternal", new Object[] { null }),
                userTO));

        assertNotNull(userTO.getPlainAttr("nullableSchema").orElse(null));
    }

    @Test
    @DisplayName("18 - setIntValues RealmTO: one-character realm name")
    void setIntValuesRealmNameOneCharacter() {
        RealmTO realmTO = new RealmTO();

        manager.setIntValues(
                item("name", "__NAME__"),
                attr("__NAME__", "r"),
                realmTO);

        assertEquals("r", realmTO.getName());
    }

    @Test
    @DisplayName("19 - setIntValues RealmTO: multiple plain values are preserved")
    void setIntValuesRealmPlainAttrMultipleValues() {
        RealmTO realmTO = new RealmTO();

        manager.setIntValues(
                item("realmSchema", "externalRealm"),
                attr("externalRealm", "v1", "v2"),
                realmTO);

        Attr plain = realmTO.getPlainAttr("realmSchema").orElseThrow();
        assertEquals(2, plain.getValues().size());
        assertEquals("v1", plain.getValues().get(0));
        assertEquals("v2", plain.getValues().get(1));
    }

    @Test
    @DisplayName("20 - processPreparedAttr: connObjectKey/name and ordinary attributes interact consistently")
    void processPreparedAttrNameAndAttributesInteraction() throws Exception {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(attr("ordinary", "before"));

        MappingManager.PreparedAttr preparedAttr = new MappingManager.PreparedAttr(
                "boundary-name",
                new Name("boundary-name"));

        Optional<String> connObjectKeyValue = invokeProcessPreparedAttr(preparedAttr, attributes);

        assertEquals(Optional.of("boundary-name"), connObjectKeyValue);
        assertTrue(attributes.stream().anyMatch(attribute -> "__NAME__".equals(attribute.getName())));
        assertTrue(attributes.stream().anyMatch(attribute -> "ordinary".equals(attribute.getName())));
        assertFalse(attributes.stream().anyMatch(attribute ->
                OperationalAttributes.PASSWORD_NAME.equals(attribute.getName())));
    }
}