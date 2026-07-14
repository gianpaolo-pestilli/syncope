package org.apache.syncope.core.provisioning.java;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.syncope.common.lib.to.Item;
import org.apache.syncope.common.lib.to.Mapping;
import org.apache.syncope.common.lib.to.OrgUnit;
import org.apache.syncope.common.lib.to.Provision;
import org.apache.syncope.common.lib.to.RealmTO;
import org.apache.syncope.common.lib.to.UserTO;
import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.syncope.core.persistence.api.EncryptorManager;
import org.apache.syncope.core.persistence.api.dao.AnyObjectDAO;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.persistence.api.dao.ImplementationDAO;
import org.apache.syncope.core.persistence.api.dao.RealmSearchDAO;
import org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.Any;
import org.apache.syncope.core.persistence.api.entity.AnyType;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.Implementation;
import org.apache.syncope.core.persistence.api.entity.PlainAttrValue;
import org.apache.syncope.core.persistence.api.entity.Realm;
import org.apache.syncope.core.persistence.api.entity.user.Account;
import org.apache.syncope.core.provisioning.api.DerAttrHandler;
import org.apache.syncope.core.provisioning.api.IntAttrName;
import org.apache.syncope.core.provisioning.api.IntAttrNameParser;
import org.apache.syncope.core.provisioning.api.MappingManager;
import org.apache.syncope.core.provisioning.api.jexl.JexlTools;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@Disabled

//RICORDATI CHE HAI COMMENTATO UNA RIGA
@ExtendWith(MockitoExtension.class)
public class LLMC3Test {
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
    private ApplicationContext ctx;
    private TestableDefaultMappingManager manager;

    @BeforeEach
    void setUp() throws Exception {
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
        ctx = mock(ApplicationContext.class);

        lenient().when(intAttrNameParser.parse(anyString())).thenAnswer(invocation -> parsed(invocation.getArgument(0)));
        lenient().when(intAttrNameParser.parse(anyString(), nullable(AnyTypeKind.class)))
                .thenAnswer(invocation -> parsed(invocation.getArgument(0)));

        manager = new TestableDefaultMappingManager(userDAO, anyObjectDAO, groupDAO, relationshipTypeDAO,
                realmSearchDAO, implementationDAO, derAttrHandler, intAttrNameParser, encryptorManager, jexlTools);

        // Risolve la chiamata interna getSelf() mockando il contesto Spring[cite: 12]
        //manager.setApplicationContext(ctx);
        lenient().when(ctx.getBean(MappingManager.class)).thenReturn(manager);
    }

    private static IntAttrName parsed(final String name) {
        IntAttrName parsed = mock(IntAttrName.class);
        if ("username".equals(name) || "name".equals(name) || "mustChangePassword".equals(name) || "password".equals(name) || "uManager".equals(name) || "gManager".equals(name)) {
            lenient().when(parsed.getField()).thenReturn(name);
        } else {
            lenient().when(parsed.getField()).thenReturn(null);
        }
        lenient().when(parsed.getSchemaInfo()).thenReturn(null);
        return parsed;
    }

    private static Item item(final String intAttrName, final String extAttrName) {
        Item item = new Item();
        item.setIntAttrName(intAttrName);
        item.setExtAttrName(extAttrName);
        return item;
    }

    private static Provision provisionWith(final Item... items) {
        Provision provision = new Provision();
        Mapping mapping = new Mapping();
        Arrays.stream(items).forEach(mapping.getItems()::add);
        provision.setMapping(mapping);
        return provision;
    }

    private static Any anyWithUserType() {
        Any any = mock(Any.class);
        AnyType anyType = mock(AnyType.class);
        lenient().when(anyType.getKey()).thenReturn("USER");
        lenient().when(anyType.getKind()).thenReturn(AnyTypeKind.USER);
        lenient().when(any.getType()).thenReturn(anyType);
        lenient().when(any.getKey()).thenReturn("any-key");
        return any;
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

        static Name exposedGetName(final String evaluatedConnObjectLink, final String connObjectKey) {
            return DefaultMappingManager.getName(evaluatedConnObjectLink, connObjectKey);
        }

        static PlainAttrValue exposedClonePlainAttrValue(final PlainAttrValue src) {
            return DefaultMappingManager.clonePlainAttrValue(src);
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

        List<Implementation> exposedGetTransformers(final Item item) {
            return getTransformers(item);
        }
    }

    @Test
    void publicAndProtectedMethodsStillExposeExpectedMappingApi() {
        Set<String> actual = Arrays.stream(DefaultMappingManager.class.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers()))
                .map(Method::getName)
                .collect(Collectors.toSet());

        // Verifica che l'API non sia stata rotta o cambiata visibilità[cite: 12]
        assertTrue(actual.containsAll(Set.of("getConnObjectKeyValue", "getIntValues", "getName",
                "hasMustChangePassword", "prepareAttrsFromAny", "prepareAttrsFromLinkedAccount",
                "prepareAttrsFromRealm", "processPreparedAttr", "setIntValues")));
    }

    @Test
    void getNameFallsBackToConnObjectKeyWhenLinkIsBlank() {
        assertEquals("jsmith", DefaultMappingManager.getName("   ", "jsmith").getNameValue());
    }

    @Test
    void processPreparedAttrAddsAttributeAndReturnsConnObjectKey() {
        Attribute attr = AttributeBuilder.build("uid", "jsmith");
        Set<Attribute> attributes = new HashSet<>();

        // Invoca il metodo come public static a seguito dell'ultimo aggiornamento della classe[cite: 12]
        Optional<String> result = DefaultMappingManager.processPreparedAttr(
                new MappingManager.PreparedAttr("jsmith", attr), attributes);

        assertEquals(Optional.of("jsmith"), result);
        assertTrue(attributes.contains(attr));
    }

    @Test
    void processPreparedAttrToleratesNullAttribute() {
        Set<Attribute> attributes = new HashSet<>();

        Optional<String> result = DefaultMappingManager.processPreparedAttr(
                new MappingManager.PreparedAttr(null, null), attributes);

        assertTrue(result.isEmpty());
        assertTrue(attributes.isEmpty());
    }

    @Test
    void clonePlainAttrValueReturnsDifferentInstance() {
        PlainAttrValue src = mock(PlainAttrValue.class);
        when(src.getStringValue()).thenReturn("value");
        when(src.getBooleanValue()).thenReturn(Boolean.TRUE);

        PlainAttrValue clone = TestableDefaultMappingManager.exposedClonePlainAttrValue(src);

        assertNotNull(clone);
        assertNotSame(src, clone);
    }

    @Test
    void passwordDefaultIsUsedWhenAccountPasswordIsUnavailable() {
        Account account = mock(Account.class);

        assertEquals(Optional.of("clearPassword"), manager.exposedGetPasswordAttrValue(account, "clearPassword"));
    }

    @Test
    void decodePasswordReturnsEmptyWhenPasswordIsMissing() {
        Account account = mock(Account.class);

        assertTrue(manager.exposedDecodePassword(account).isEmpty());
    }

    @Test
    void itemWithoutTransformersReturnsEmptyList() {
        List<Implementation> transformers = manager.exposedGetTransformers(new Item());

        assertNotNull(transformers);
        assertTrue(transformers.isEmpty());
        verifyNoInteractions(implementationDAO);
    }

    @Test
    void hasMustChangePasswordReflectsMappedField() throws Exception {
        assertTrue(manager.hasMustChangePassword(provisionWith(item("mustChangePassword", "pwdReset"))));
        assertFalse(manager.hasMustChangePassword(provisionWith(item("username", "uid"))));
    }

    @Test
    void nullProvisionIsNotAValidHasMustChangePasswordInput() {
        assertThrows(NullPointerException.class, () -> manager.hasMustChangePassword(null));
    }

    @Test
    void setIntValuesSetsUsernameField() throws Exception {
        UserTO userTO = new UserTO();

        manager.setIntValues(item("username", "uid"), AttributeBuilder.build("uid", "jsmith"), userTO);

        assertEquals("jsmith", userTO.getUsername());
    }

    @Test
    void setIntValuesSetsRealmNameField() throws Exception {
        RealmTO realmTO = new RealmTO();

        manager.setIntValues(item("name", "ou"), AttributeBuilder.build("ou", "engineering"), realmTO);

        assertEquals("engineering", realmTO.getName());
    }

    @Test
    void setIntValuesSetsManagerFields() throws Exception {
        UserTO userTO = new UserTO();

        // Testa le nuove costanti U_MANAGER introdotte nell'ultimo update[cite: 12]
        manager.setIntValues(item("uManager", "managerId"), AttributeBuilder.build("managerId", "boss123"), userTO);

        assertEquals("boss123", userTO.getUManager());
    }

    @Test
    void setIntValuesIgnoresNullAndEmptyConnectorAttributes() {
        UserTO userTO = new UserTO();

        assertDoesNotThrow(() -> manager.setIntValues(item("username", "uid"), null, userTO));
        assertDoesNotThrow(() -> manager.setIntValues(item("username", "uid"), AttributeBuilder.build("uid"), userTO));

        assertNull(userTO.getUsername());
    }

    @Test
    void getConnObjectKeyValueReturnsEmptyWhenNoConnObjectKeyItemIsConfigured() throws Exception {
        Optional<String> value = manager.getConnObjectKeyValue(anyWithUserType(), mock(ExternalResource.class), provisionWith());

        assertTrue(value.isEmpty());
    }

    @Test
    void evaluateNameFallsBackToAnyConnObjectKey() {
        Name name = manager.exposedEvaluateNAME(anyWithUserType(), new Provision(), "user-key");

        assertEquals("user-key", name.getNameValue());
    }

    @Test
    void evaluateNameFallsBackToRealmConnObjectKey() {
        Name name = manager.exposedEvaluateNAME(mock(Realm.class), new OrgUnit(), "realm-key");

        assertEquals("realm-key", name.getNameValue());
    }

    @Test
    void prepareAttrsFromAnyReturnsStructureForEmptyMapping() throws Exception {
        MappingManager.PreparedAttrs prepared = manager.prepareAttrsFromAny(
                anyWithUserType(), null, false, null, mock(ExternalResource.class), provisionWith());

        assertNotNull(prepared);
        assertNotNull(prepared.attributes());
    }

    @Test
    void prepareAttrsFromAnyIncludesEnableOperationalAttributeWhenRequested() throws Exception {
        MappingManager.PreparedAttrs prepared = manager.prepareAttrsFromAny(
                anyWithUserType(), null, false, Boolean.FALSE, mock(ExternalResource.class), provisionWith());

        assertTrue(prepared.attributes().stream().anyMatch(attr -> OperationalAttributes.ENABLE_NAME.equals(attr.getName())));
    }

    @Test
    void prepareAttrsFromRealmReturnsStructureWhenResourceHasNoOrgUnit() throws Exception {
        ExternalResource resource = mock(ExternalResource.class);
        when(resource.getOrgUnit()).thenReturn(null);

        MappingManager.PreparedAttrs prepared = manager.prepareAttrsFromRealm(mock(Realm.class), resource);

        assertNotNull(prepared);
        assertNotNull(prepared.attributes());
    }
}