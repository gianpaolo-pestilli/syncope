package org.apache.syncope.core.provisioning.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.apache.syncope.common.lib.to.Item;
import org.apache.syncope.common.lib.to.Mapping;
import org.apache.syncope.common.lib.to.Provision;
import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.syncope.common.lib.types.AttrSchemaType;
import org.apache.syncope.common.lib.types.CipherAlgorithm;
import org.apache.syncope.common.lib.types.SchemaType;
import org.apache.syncope.core.persistence.api.Encryptor;
import org.apache.syncope.core.persistence.api.EncryptorManager;
import org.apache.syncope.core.persistence.api.dao.AnyObjectDAO;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.persistence.api.dao.ImplementationDAO;
import org.apache.syncope.core.persistence.api.dao.RealmSearchDAO;
import org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.Any;
import org.apache.syncope.core.persistence.api.entity.AnyType;
import org.apache.syncope.core.persistence.api.entity.DerSchema;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.Membership;
import org.apache.syncope.core.persistence.api.entity.PlainAttr;
import org.apache.syncope.core.persistence.api.entity.PlainAttrValue;
import org.apache.syncope.core.persistence.api.entity.PlainSchema;
import org.apache.syncope.core.persistence.api.entity.Realm;
import org.apache.syncope.core.persistence.api.entity.Relationship;
import org.apache.syncope.core.persistence.api.entity.RelationshipType;
import org.apache.syncope.core.persistence.api.entity.anyobject.AnyObject;
import org.apache.syncope.core.persistence.api.entity.group.Group;
import org.apache.syncope.core.persistence.api.entity.user.Account;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.provisioning.api.AccountGetter;
import org.apache.syncope.core.provisioning.api.DerAttrHandler;
import org.apache.syncope.core.provisioning.api.IntAttrName;
import org.apache.syncope.core.provisioning.api.IntAttrNameParser;
import org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttr;
import org.apache.syncope.core.provisioning.api.PlainAttrGetter;
import org.apache.syncope.core.provisioning.api.data.ItemTransformer;
import org.apache.syncope.core.provisioning.api.jexl.JexlTools;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ManualAfterJacocoDefaultMappingManagerTest {

    @InjectMocks
    private DefaultMappingManager mappingManager;

    @Mock private ImplementationDAO implementationDAO;
    @Mock private UserDAO userDAO;
    @Mock private AnyObjectDAO anyObjectDAO;
    @Mock private GroupDAO groupDAO;
    @Mock private RelationshipTypeDAO relationshipTypeDAO;
    @Mock private RealmSearchDAO realmSearchDAO;
    @Mock private DerAttrHandler derAttrHandler;
    @Mock private EncryptorManager encryptorManager;
    @Mock private JexlTools jexlTools;
    @Mock private IntAttrNameParser intAttrNameParser;

    @Mock private IntAttrName intAttrName;
    @Mock private IntAttrName.SchemaInfo schemaInfo;
    @Mock private PlainSchema plainSchema;
    @Mock private Any any;
    @Mock private AnyType anyType;
    @Mock private ExternalResource resource;
    @Mock private Provision provision;
    @Mock private Item item;
    @Mock private AccountGetter accGetter;
    @Mock private PlainAttrGetter attrGetter;

    @Mock private Realm realmMock;
    @Mock private Account accountMock;
    @Mock private Membership<?> membershipMock;
    @Mock private Relationship<?, ?> relationshipMock;
    @Mock private RelationshipType relationshipTypeMock;
    @Mock private Mapping mappingMock;
    @Mock private ItemTransformer itemTransformerMock;
    @Mock private DerSchema derSchemaMock;
    @Mock private PlainSchema plainSchemaMock;
    @Mock private IntAttrName.SchemaInfo schemaInfoMock;
    @Mock private IntAttrName.RelationshipInfo relationshipInfoMock;
    @Mock private AnyType rightEndAnyTypeMock;
    @Mock private AnyObject rightEndMock;
    @Mock private PlainAttr plainAttrMock;

    @BeforeEach
    public void setUp() throws Exception {
        when(any.getType()).thenReturn(anyType);
        when(anyType.getKind()).thenReturn(AnyTypeKind.USER);

        when(item.getIntAttrName()).thenReturn("dummyField");
        when(item.getExtAttrName()).thenReturn("extName");
        when(item.getTransformers()).thenReturn(List.of());
        when(intAttrNameParser.parse(anyString(), any(AnyTypeKind.class))).thenReturn(intAttrName);

        when(intAttrName.getSchemaInfo()).thenReturn(schemaInfo);
        when(schemaInfo.type()).thenReturn(SchemaType.PLAIN);
        when(schemaInfo.schema()).thenReturn(plainSchema);
        when(plainSchema.getKey()).thenReturn("schemaKey");
        when(plainSchema.getType()).thenReturn(AttrSchemaType.String);
    }

    private void mockRealAttrValue(String stringValue) {
        PlainAttrValue realValue = new PlainAttrValue();
        realValue.setStringValue(stringValue);
        PlainAttr mockAttr = mock(PlainAttr.class);
        when(mockAttr.getValues()).thenReturn(List.of(realValue));
        when(attrGetter.apply(any(), anyString())).thenReturn(mockAttr);
    }

    @Test
    public void parseException() throws ParseException {
        when(intAttrNameParser.parse(anyString(), any(AnyTypeKind.class)))
                .thenThrow(new ParseException("Mocked Parsing Error", 0));
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertNull(res);
    }

    @Test
    public void encryptedValue() throws Exception {
        when(plainSchema.getType()).thenReturn(AttrSchemaType.Encrypted);
        when(plainSchema.getSecretKey()).thenReturn("mySecretKey");
        when(plainSchema.getCipherAlgorithm()).thenReturn(CipherAlgorithm.AES);
        mockRealAttrValue("encodedValue");
        Encryptor encryptor = mock(Encryptor.class);
        when(encryptorManager.getInstance(anyString())).thenReturn(encryptor);
        when(encryptor.decode("encodedValue", CipherAlgorithm.AES)).thenReturn("decodedValue");
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals("decodedValue", res.attribute().getValue().get(0));
    }

    @Test
    public void encryptedValueDecodeException() throws Exception {
        when(plainSchema.getType()).thenReturn(AttrSchemaType.Encrypted);
        when(plainSchema.getSecretKey()).thenReturn("mySecretKey");
        when(plainSchema.getCipherAlgorithm()).thenReturn(CipherAlgorithm.AES);
        mockRealAttrValue("encodedValue");
        Encryptor encryptor = mock(Encryptor.class);
        when(encryptorManager.getInstance(anyString())).thenReturn(encryptor);
        when(encryptor.decode(anyString(), any())).thenThrow(new RuntimeException("Decryption failed"));
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals("encodedValue", res.attribute().getValue().get(0));
    }

    @Test
    public void unsupportedSchemaType() {
        when(plainSchema.getType()).thenReturn(AttrSchemaType.Enum);
        mockRealAttrValue("EnumStringValue");
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals("EnumStringValue", res.attribute().getValue().get(0));
    }

    @Test
    public void unsupportedSchemaTypeTypeMismatch() {
        when(plainSchema.getType()).thenReturn(AttrSchemaType.Enum, AttrSchemaType.Binary);
        mockRealAttrValue("SchemaTypeValue");
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals("SchemaTypeValue", res.attribute().getValue().get(0));
    }

    @Test
    public void isPasswordAndUser() {
        when(item.isPassword()).thenReturn(true);
        User user = mock(User.class);
        when(user.getType()).thenReturn(anyType);
        Account account = mock(Account.class);
        when(accGetter.apply(user)).thenReturn(account);
        when(account.getPassword()).thenReturn("accountSpecificPassword");
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, user, "pwd", accGetter, accGetter, attrGetter);
        assertEquals(OperationalAttributes.PASSWORD_NAME, res.attribute().getName());
    }

    @Test
    public void isPasswordNotUser() {
        when(item.isPassword()).thenReturn(true);
        Group group = mock(Group.class);
        when(group.getType()).thenReturn(anyType);
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, group, "pwd", accGetter, accGetter, attrGetter);
        assertNull(res.attribute().getValue());
    }

    @Test
    public void operationalPasswordName() {
        when(item.getExtAttrName()).thenReturn(OperationalAttributes.PASSWORD_NAME);
        mockRealAttrValue("myPlainPassword");
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals(OperationalAttributes.PASSWORD_NAME, res.attribute().getName());
    }

    @Test
    public void connObjectKeyWithValues() {
        when(item.isConnObjectKey()).thenReturn(true);
        mockRealAttrValue("myKeyValue");
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals("myKeyValue", res.connObjectLink());
    }

    @Test
    public void testUnsupportedAttributeType_HitsLine556() {
        try (org.mockito.MockedStatic<org.identityconnectors.framework.common.FrameworkUtil> frameworkUtil = mockStatic(org.identityconnectors.framework.common.FrameworkUtil.class)) {
            frameworkUtil.when(() -> org.identityconnectors.framework.common.FrameworkUtil.isSupportedAttributeType(any())).thenReturn(false);
            when(plainSchema.getType()).thenReturn(AttrSchemaType.String);
            PlainAttrValue realValue = new PlainAttrValue();
            realValue.setStringValue("valueLine556");
            PlainAttr mockAttr = mock(PlainAttr.class);
            when(mockAttr.getValues()).thenReturn(List.of(realValue));
            when(attrGetter.apply(any(), anyString())).thenReturn(mockAttr);
            PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
            assertEquals("valueLine556", res.attribute().getValue().get(0));
        }
    }

    @Test
    public void testUnsupportedAttributeType_HitsLine554() {
        try (org.mockito.MockedStatic<org.identityconnectors.framework.common.FrameworkUtil> frameworkUtil = mockStatic(org.identityconnectors.framework.common.FrameworkUtil.class)) {
            frameworkUtil.when(() -> org.identityconnectors.framework.common.FrameworkUtil.isSupportedAttributeType(any())).thenReturn(false);
            DerSchema derSchema = mock(DerSchema.class);
            when(schemaInfo.schema()).thenReturn(derSchema);
            when(schemaInfo.type()).thenReturn(SchemaType.DERIVED);
            when(derAttrHandler.getValue((Any)any(), any(DerSchema.class))).thenReturn("valueLine554");
            PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
            assertEquals("valueLine554", res.attribute().getValue().get(0));
        }
    }

    @Test
    public void testEncryptedType_NotPlainSchema_HitsLine535Yellow() {
        DerSchema derSchema = mock(DerSchema.class);
        when(schemaInfo.schema()).thenReturn(derSchema);
        when(schemaInfo.type()).thenReturn(SchemaType.DERIVED);
        when(derAttrHandler.getValue((Any)any(), any(DerSchema.class))).thenReturn("DerivedValue");
        try (org.mockito.MockedStatic<org.apache.syncope.core.provisioning.java.utils.MappingUtils> mappingUtils = mockStatic(org.apache.syncope.core.provisioning.java.utils.MappingUtils.class)) {
            ItemTransformer transformer = mock(ItemTransformer.class);
            mappingUtils.when(() -> org.apache.syncope.core.provisioning.java.utils.MappingUtils.getItemTransformers(any(), any())).thenReturn(List.of(transformer));
            org.apache.syncope.core.provisioning.api.MappingManager.IntValues intValues = new org.apache.syncope.core.provisioning.api.MappingManager.IntValues(AttrSchemaType.Encrypted, List.of(new PlainAttrValue()));
            when(transformer.beforePropagation(any(), any(), any(), any())).thenReturn(intValues);
            mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        }
    }

    @Test
    public void line553PlainSchemaIsNull() {
        try (org.mockito.MockedStatic<org.identityconnectors.framework.common.FrameworkUtil> frameworkUtil = mockStatic(org.identityconnectors.framework.common.FrameworkUtil.class)) {
            frameworkUtil.when(() -> org.identityconnectors.framework.common.FrameworkUtil.isSupportedAttributeType(any())).thenReturn(false);
            DerSchema derSchema = mock(DerSchema.class);
            when(schemaInfo.schema()).thenReturn(derSchema);
            when(schemaInfo.type()).thenReturn(SchemaType.DERIVED);
            when(derAttrHandler.getValue((Any)any(), any(DerSchema.class))).thenReturn("ValoreSchemaNullo");
            PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
            assertEquals("ValoreSchemaNullo", res.attribute().getValue().get(0));
        }
    }

    @Test
    public void line553PlainSchemaTypeMismatch() {
        try (org.mockito.MockedStatic<org.identityconnectors.framework.common.FrameworkUtil> frameworkUtil = mockStatic(org.identityconnectors.framework.common.FrameworkUtil.class)) {
            frameworkUtil.when(() -> org.identityconnectors.framework.common.FrameworkUtil.isSupportedAttributeType(any())).thenReturn(false);
            when(plainSchema.getType()).thenReturn(AttrSchemaType.Enum, AttrSchemaType.Binary);
            PlainAttrValue realValue = new PlainAttrValue();
            realValue.setStringValue("ValoreMismatch");
            PlainAttr mockAttr = mock(PlainAttr.class);
            when(mockAttr.getValues()).thenReturn(List.of(realValue));
            when(attrGetter.apply(any(), anyString())).thenReturn(mockAttr);
            PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
            assertEquals("ValoreMismatch", res.attribute().getValue().get(0));
        }
    }


    @Test
    void testExternalUserFound() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getExternalUser()).thenReturn("extUser");
        doReturn(Optional.of(user)).when(userDAO).findByUsername("extUser");
        when(intAttrName.getField()).thenReturn("key");
        when(user.getKey()).thenReturn("extUserKey");
        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
        assertEquals("extUserKey", result.values().get(0).getStringValue());
    }

    @Test
    void testExternalUserNotFound() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getExternalUser()).thenReturn("extUserNotFound");
        doReturn(Optional.empty()).when(userDAO).findByUsername("extUserNotFound");
        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
        assertTrue(result.values().isEmpty());
    }

    @Test
    void testExternalGroupFound() throws Exception {
        User user = mock(User.class);
        Group group = mock(Group.class);
        when(intAttrName.getExternalGroup()).thenReturn("extGroup");
        doReturn(Optional.of(group)).when(groupDAO).findByName("extGroup");
        when(intAttrName.getField()).thenReturn("key");
        when(group.getKey()).thenReturn("extGroupKey");
        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
        assertEquals("extGroupKey", result.values().get(0).getStringValue());
    }

    @Test
    void testExternalAnyObjectFound() throws Exception {
        User user = mock(User.class);
        AnyObject anyObject = mock(AnyObject.class);
        when(intAttrName.getExternalAnyObject()).thenReturn("extAny");
        when(anyObjectDAO.findByName("extAny")).thenReturn(List.of(anyObject));
        when(intAttrName.getField()).thenReturn("key");
        when(anyObject.getKey()).thenReturn("extAnyKey");
        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
        assertEquals("extAnyKey", result.values().get(0).getStringValue());
    }

    @Test
    void testMembershipFound() throws Exception {
        User user = mock(User.class);
        Group group = mock(Group.class);
        when(intAttrName.getMembership()).thenReturn("groupName");
        doReturn(Optional.of(group)).when(groupDAO).findByName("groupName");
        when(group.getKey()).thenReturn("groupKey");
        doReturn(Optional.of(membershipMock)).when(user).getMembership("groupKey");
        when(intAttrName.getField()).thenReturn("key");
        when(user.getKey()).thenReturn("userKey");
        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
        assertEquals("userKey", result.values().get(0).getStringValue());
    }

    @Test
    void testRelationshipFound() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getRelationshipInfo()).thenReturn(relationshipInfoMock);
        when(relationshipInfoMock.type()).thenReturn("relType");
        doReturn(Optional.of(relationshipTypeMock)).when(relationshipTypeDAO).findById("relType");
        when(relationshipTypeMock.getRightEndAnyType()).thenReturn(rightEndAnyTypeMock);
        when(rightEndAnyTypeMock.getKey()).thenReturn("rightEndKey");
        when(relationshipInfoMock.anyObject()).thenReturn("rightEndName");
        doReturn(Optional.of(rightEndMock)).when(anyObjectDAO).findByName("rightEndKey", "rightEndName");
        when(rightEndMock.getKey()).thenReturn("otherEndKey");
        doReturn(Optional.of(relationshipMock)).when(user).getRelationship(relationshipTypeMock, "otherEndKey");
        when(intAttrName.getField()).thenReturn("key");
        when(user.getKey()).thenReturn("userKey");
        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
        assertEquals("userKey", result.values().get(0).getStringValue());
    }

    @Test
    void testFieldUsername() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getField()).thenReturn("username");
        when(accGetter.apply(user)).thenReturn(accountMock);
        when(accountMock.getUsername()).thenReturn("myAccountUsername");
        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
        assertEquals("myAccountUsername", result.values().get(0).getStringValue());
    }

    @Test
    void testFieldRealm() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getField()).thenReturn("realm");
        when(user.getRealm()).thenReturn(realmMock);
        when(realmMock.getFullPath()).thenReturn("/test/realm");
        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
        assertEquals("/test/realm", result.values().get(0).getStringValue());
    }

    @Test
    void testFieldPassword() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getField()).thenReturn("password");
        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
        assertTrue(result.values().isEmpty());
    }

    @Test
    void testFieldUManager() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getField()).thenReturn("uManager");
        when(provision.getAnyType()).thenReturn(AnyTypeKind.USER.name());
        when(provision.getMapping()).thenReturn(mappingMock);
        when(user.getUManager()).thenReturn(user);
        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
        assertTrue(result.values().isEmpty());
    }

    @Test
    void testFieldGManager() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getField()).thenReturn("gManager");
        when(provision.getAnyType()).thenReturn(AnyTypeKind.GROUP.name());
        when(provision.getMapping()).thenReturn(mappingMock);
        doReturn(java.util.Optional.of(user)).when(userDAO).findByUsername("extUser");
        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
        assertTrue(result.values().isEmpty());
    }

    @Test
    void testFieldMustChangePassword() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getField()).thenReturn("mustChangePassword");
        when(user.isMustChangePassword()).thenReturn(true);
        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
        assertTrue(result.values().get(0).getBooleanValue());
    }

    @Test
    void testReflectionTemporalAccessor() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getField()).thenReturn("customDate");
        try (org.mockito.MockedStatic<org.apache.commons.lang3.reflect.FieldUtils> fieldUtils = mockStatic(org.apache.commons.lang3.reflect.FieldUtils.class);
             org.mockito.MockedStatic<org.apache.syncope.core.persistence.api.utils.FormatUtils> formatUtils = mockStatic(org.apache.syncope.core.persistence.api.utils.FormatUtils.class)) {
            java.time.temporal.TemporalAccessor dateMock = mock(java.time.temporal.TemporalAccessor.class);
            fieldUtils.when(() -> org.apache.commons.lang3.reflect.FieldUtils.readField(user, "customDate", true)).thenReturn(dateMock);
            formatUtils.when(() -> org.apache.syncope.core.persistence.api.utils.FormatUtils.format(dateMock)).thenReturn("2023-12-31");
            org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
            assertEquals("2023-12-31", result.values().get(0).getStringValue());
        }
    }

    @Test
    void testReflectionBoolean() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getField()).thenReturn("customBool");

        try (org.mockito.MockedStatic<org.apache.commons.lang3.reflect.FieldUtils> fieldUtils = mockStatic(org.apache.commons.lang3.reflect.FieldUtils.class)) {
            fieldUtils.when(() -> org.apache.commons.lang3.reflect.FieldUtils.readField(user, "customBool", true)).thenReturn(Boolean.TRUE);

            org.apache.syncope.core.provisioning.api.MappingManager.IntValues result =
                    mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.Boolean, user, accGetter, attrGetter);

            assertEquals("true", result.values().get(0).getStringValue());
        }
    }

    @Test
    void testReflectionDouble() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getField()).thenReturn("customDouble");

        try (org.mockito.MockedStatic<org.apache.commons.lang3.reflect.FieldUtils> fieldUtils = mockStatic(org.apache.commons.lang3.reflect.FieldUtils.class)) {
            fieldUtils.when(() -> org.apache.commons.lang3.reflect.FieldUtils.readField(user, "customDouble", true)).thenReturn(99.9d);

            org.apache.syncope.core.provisioning.api.MappingManager.IntValues result =
                    mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.Double, user, accGetter, attrGetter);

            assertEquals("99.9", result.values().get(0).getStringValue());
        }
    }

    @Test
    void testReflectionLong() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getField()).thenReturn("customLong");

        try (org.mockito.MockedStatic<org.apache.commons.lang3.reflect.FieldUtils> fieldUtils = mockStatic(org.apache.commons.lang3.reflect.FieldUtils.class)) {
            fieldUtils.when(() -> org.apache.commons.lang3.reflect.FieldUtils.readField(user, "customLong", true)).thenReturn(100L);

            org.apache.syncope.core.provisioning.api.MappingManager.IntValues result =
                    mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.Long, user, accGetter, attrGetter);

            assertEquals("100", result.values().get(0).getStringValue());
        }
    }

    @Test
    void testReflectionString() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getField()).thenReturn("customString");
        try (org.mockito.MockedStatic<org.apache.commons.lang3.reflect.FieldUtils> fieldUtils = mockStatic(org.apache.commons.lang3.reflect.FieldUtils.class)) {
            fieldUtils.when(() -> org.apache.commons.lang3.reflect.FieldUtils.readField(user, "customString", true)).thenReturn(new Object() {
                @Override
                public String toString() { return "FallbackString"; }
            });
            org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
            assertEquals("FallbackString", result.values().get(0).getStringValue());
        }
    }

    @Test
    void testReflectionException() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getField()).thenReturn("errorField");
        try (org.mockito.MockedStatic<org.apache.commons.lang3.reflect.FieldUtils> fieldUtils = mockStatic(org.apache.commons.lang3.reflect.FieldUtils.class)) {
            fieldUtils.when(() -> org.apache.commons.lang3.reflect.FieldUtils.readField(user, "errorField", true)).thenThrow(new IllegalArgumentException("Field error"));
            org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
            assertTrue(result.values().isEmpty());
        }
    }

    @Test
    void testSchemaPlainWithMembership() throws Exception {
        User user = mock(User.class);
        Group group = mock(Group.class);
        when(intAttrName.getSchemaInfo()).thenReturn(schemaInfoMock);
        when(schemaInfoMock.type()).thenReturn(SchemaType.PLAIN);
        when(schemaInfoMock.schema()).thenReturn(plainSchemaMock);
        when(plainSchemaMock.getKey()).thenReturn("attrKey");
        when(intAttrName.getMembership()).thenReturn("groupName");
        doReturn(Optional.of(group)).when(groupDAO).findByName("groupName");
        when(group.getKey()).thenReturn("groupKey");
        doReturn(Optional.of(membershipMock)).when(user).getMembership("groupKey");
        doReturn(Optional.of(plainAttrMock)).when(user).getPlainAttr("attrKey", membershipMock);
        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
        assertTrue(result.values().isEmpty());
    }

    @Test
    void testSchemaDerivedWithRelationship() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getSchemaInfo()).thenReturn(schemaInfoMock);
        when(schemaInfoMock.type()).thenReturn(SchemaType.DERIVED);
        when(schemaInfoMock.schema()).thenReturn(derSchemaMock);
        when(intAttrName.getRelationshipInfo()).thenReturn(relationshipInfoMock);
        when(relationshipInfoMock.type()).thenReturn("relType");
        doReturn(Optional.of(relationshipTypeMock)).when(relationshipTypeDAO).findById("relType");
        when(relationshipTypeMock.getRightEndAnyType()).thenReturn(rightEndAnyTypeMock);
        when(rightEndAnyTypeMock.getKey()).thenReturn("rightEndKey");
        when(relationshipInfoMock.anyObject()).thenReturn("rightEndName");
        doReturn(Optional.of(rightEndMock)).when(anyObjectDAO).findByName("rightEndKey", "rightEndName");
        when(rightEndMock.getKey()).thenReturn("otherEndKey");
        doReturn(Optional.of(relationshipMock)).when(user).getRelationship(relationshipTypeMock, "otherEndKey");
        when(derAttrHandler.getValue(user, relationshipMock, derSchemaMock)).thenReturn("derValueRel");
        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
        assertEquals("derValueRel", result.values().get(0).getStringValue());
    }

    @Test
    void testItemTransformersAreExecuted() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getField()).thenReturn("key");
        when(user.getKey()).thenReturn("baseKey");
        try (org.mockito.MockedStatic<org.apache.syncope.core.provisioning.java.utils.MappingUtils> mappingUtils = mockStatic(org.apache.syncope.core.provisioning.java.utils.MappingUtils.class)) {
            mappingUtils.when(() -> org.apache.syncope.core.provisioning.java.utils.MappingUtils.getItemTransformers(any(), any())).thenReturn(List.of(itemTransformerMock));
            org.apache.syncope.core.provisioning.api.MappingManager.IntValues transformedValues = new org.apache.syncope.core.provisioning.api.MappingManager.IntValues(AttrSchemaType.String, List.of());
            when(itemTransformerMock.beforePropagation(any(), any(), any(), any())).thenReturn(transformedValues);
            org.apache.syncope.core.provisioning.api.MappingManager.IntValues result = mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);
            assertTrue(result.values().isEmpty());
        }
    }


    @Test
    void testExternalGroupNotFound() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getExternalGroup()).thenReturn("missingGroup");
        doReturn(Optional.empty()).when(groupDAO).findByName("missingGroup");

        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result =
                mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);

        assertTrue(result.values().isEmpty());
    }

    @Test
    void testRelationshipTypeNotFound() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getRelationshipInfo()).thenReturn(relationshipInfoMock);
        when(relationshipInfoMock.type()).thenReturn("missingRelType");
        doReturn(Optional.empty()).when(relationshipTypeDAO).findById("missingRelType");

        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result =
                mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);

        assertTrue(result.values().isEmpty());
    }

    @Test
    void testUsernameNotAccount() throws Exception {
        Group group = mock(Group.class);
        when(intAttrName.getField()).thenReturn("username");

        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result =
                mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, group, accGetter, attrGetter);

        assertTrue(result.values().isEmpty());
    }

    @Test
    void testGManagerWithValue() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getField()).thenReturn("gManager");
        when(provision.getAnyType()).thenReturn(AnyTypeKind.GROUP.name());
        when(provision.getMapping()).thenReturn(mappingMock);

        Group gManagerMock = mock(Group.class);
        doReturn(gManagerMock).when(user).getGManager();

        DefaultMappingManager spyManager = org.mockito.Mockito.spy(mappingManager);
        doReturn("managerKeyValue").when(spyManager).getManagerValue(any(), any(), any());

        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result =
                spyManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);

        assertEquals("managerKeyValue", result.values().get(0).getStringValue());
    }

    @Test
    void testPlainWithRelationship() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getSchemaInfo()).thenReturn(schemaInfoMock);
        when(schemaInfoMock.type()).thenReturn(SchemaType.PLAIN);
        when(schemaInfoMock.schema()).thenReturn(plainSchemaMock);
        when(plainSchemaMock.getKey()).thenReturn("attrKey");

        when(intAttrName.getRelationshipInfo()).thenReturn(relationshipInfoMock);
        when(relationshipInfoMock.type()).thenReturn("relType");
        doReturn(Optional.of(relationshipTypeMock)).when(relationshipTypeDAO).findById("relType");
        when(relationshipTypeMock.getRightEndAnyType()).thenReturn(rightEndAnyTypeMock);
        when(rightEndAnyTypeMock.getKey()).thenReturn("rightEndKey");
        when(relationshipInfoMock.anyObject()).thenReturn("rightEndName");
        doReturn(Optional.of(rightEndMock)).when(anyObjectDAO).findByName("rightEndKey", "rightEndName");
        when(rightEndMock.getKey()).thenReturn("otherEndKey");
        doReturn(Optional.of(relationshipMock)).when(user).getRelationship(relationshipTypeMock, "otherEndKey");

        PlainAttr attrMock = mock(PlainAttr.class);
        PlainAttrValue val = new PlainAttrValue();
        val.setStringValue("relValue");
        doReturn(val).when(attrMock).getUniqueValue();

        doReturn(Optional.of(attrMock)).when(user).getPlainAttr(anyString(), any(Relationship.class));

        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result =
                mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);

        assertEquals("relValue", result.values().get(0).getStringValue());
    }

    @Test
    void testPlainAttrValuesNull() throws Exception {
        User user = mock(User.class);
        when(intAttrName.getSchemaInfo()).thenReturn(schemaInfoMock);
        when(schemaInfoMock.type()).thenReturn(SchemaType.PLAIN);
        when(schemaInfoMock.schema()).thenReturn(plainSchemaMock);
        when(plainSchemaMock.getKey()).thenReturn("attrKey");

        PlainAttr attrMock = mock(PlainAttr.class);
        doReturn(null).when(attrMock).getUniqueValue();
        doReturn(null).when(attrMock).getValues();

        doReturn(attrMock).when(attrGetter).apply(any(), anyString());

        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result =
                mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);

        assertTrue(result.values().isEmpty());
    }

    @Test
    void testDerivedWithMembership() throws Exception {
        User user = mock(User.class);
        Group group = mock(Group.class);

        when(intAttrName.getSchemaInfo()).thenReturn(schemaInfoMock);
        when(schemaInfoMock.type()).thenReturn(SchemaType.DERIVED);
        when(schemaInfoMock.schema()).thenReturn(derSchemaMock);

        when(intAttrName.getMembership()).thenReturn("groupName");
        doReturn(Optional.of(group)).when(groupDAO).findByName("groupName");
        when(group.getKey()).thenReturn("groupKey");
        doReturn(Optional.of(membershipMock)).when(user).getMembership("groupKey");
        doReturn("derMembValue").when(derAttrHandler).getValue(
                any(org.apache.syncope.core.persistence.api.entity.Groupable.class),
                any(org.apache.syncope.core.persistence.api.entity.Membership.class),
                any(org.apache.syncope.core.persistence.api.entity.DerSchema.class)
        );

        org.apache.syncope.core.provisioning.api.MappingManager.IntValues result =
                mappingManager.getIntValues(resource, provision, item, intAttrName, AttrSchemaType.String, user, accGetter, attrGetter);

        assertEquals("derMembValue", result.values().get(0).getStringValue());
    }


    @Test
    void testSetIntValues_Transformers() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        doReturn(List.of("oldValue")).when(attrMock).getValue();

        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn("username").when(nameMock).getField();
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));

        org.apache.syncope.common.lib.to.UserTO userTO = new org.apache.syncope.common.lib.to.UserTO();
        ItemTransformer transformerMock = mock(ItemTransformer.class);
        doReturn(List.of("transformedValue")).when(transformerMock).beforePull(any(), any(), any());

        try (org.mockito.MockedStatic<org.apache.syncope.core.provisioning.java.utils.MappingUtils> mappingUtils = mockStatic(org.apache.syncope.core.provisioning.java.utils.MappingUtils.class)) {
            mappingUtils.when(() -> org.apache.syncope.core.provisioning.java.utils.MappingUtils.getItemTransformers(any(), any())).thenReturn(List.of(transformerMock));
            mappingManager.setIntValues(itemMock, attrMock, userTO);
        }

        assertEquals("transformedValue", userTO.getUsername());
    }

    @Test
    void testSetIntValues_FieldPassword() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);

        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));
        doReturn(List.of("mySecretPwd")).when(attrMock).getValue();
        doReturn("password").when(nameMock).getField();

        org.apache.syncope.common.lib.to.UserTO userTO = new org.apache.syncope.common.lib.to.UserTO();
        mappingManager.setIntValues(itemMock, attrMock, userTO);

        assertEquals("mySecretPwd", userTO.getPassword());
    }

    @Test
    void testSetIntValues_FieldUsername() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);

        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));
        doReturn(List.of("myUsername")).when(attrMock).getValue();
        doReturn("username").when(nameMock).getField();

        org.apache.syncope.common.lib.to.UserTO userTO = new org.apache.syncope.common.lib.to.UserTO();
        mappingManager.setIntValues(itemMock, attrMock, userTO);

        assertEquals("myUsername", userTO.getUsername());
    }

    @Test
    void testSetIntValues_FieldName_Group() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        doReturn(List.of("TheName")).when(attrMock).getValue();

        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn("name").when(nameMock).getField();
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));

        org.apache.syncope.common.lib.to.GroupTO groupTO = new org.apache.syncope.common.lib.to.GroupTO();
        mappingManager.setIntValues(itemMock, attrMock, groupTO);

        assertEquals("TheName", groupTO.getName());
    }

    @Test
    void testSetIntValues_FieldName_AnyObject() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        doReturn(List.of("TheName")).when(attrMock).getValue();

        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn("name").when(nameMock).getField();
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));

        org.apache.syncope.common.lib.to.AnyObjectTO anyObjectTO = new org.apache.syncope.common.lib.to.AnyObjectTO();
        mappingManager.setIntValues(itemMock, attrMock, anyObjectTO);

        assertEquals("TheName", anyObjectTO.getName());
    }

    @Test
    void testSetIntValues_FieldMustChangePassword() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        doReturn(List.of("true")).when(attrMock).getValue();

        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn("mustChangePassword").when(nameMock).getField();
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));

        org.apache.syncope.common.lib.to.UserTO userTO = new org.apache.syncope.common.lib.to.UserTO();
        mappingManager.setIntValues(itemMock, attrMock, userTO);

        assertTrue(userTO.isMustChangePassword());
    }

    @Test
    void testSetIntValues_FieldUManager() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        doReturn(List.of("ManagerKey")).when(attrMock).getValue();

        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));
        doReturn("uManager").when(nameMock).getField();

        org.apache.syncope.common.lib.to.UserTO userTO = new org.apache.syncope.common.lib.to.UserTO();
        mappingManager.setIntValues(itemMock, attrMock, userTO);

        assertEquals("ManagerKey", userTO.getUManager());
    }

    @Test
    void testSetIntValues_FieldGManager() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        doReturn(List.of("ManagerKey")).when(attrMock).getValue();

        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));
        doReturn("gManager").when(nameMock).getField();

        org.apache.syncope.common.lib.to.UserTO userTO = new org.apache.syncope.common.lib.to.UserTO();
        mappingManager.setIntValues(itemMock, attrMock, userTO);

        assertEquals("ManagerKey", userTO.getGManager());
    }

    @Test
    void testSetIntValues_PlainSchema_Binary() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);

        byte[] binaryData = new byte[] { 1, 2, 3 };
        doReturn(List.of(binaryData)).when(attrMock).getValue();

        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(null).when(nameMock).getField();

        IntAttrName.SchemaInfo schemaInfoMock = mock(IntAttrName.SchemaInfo.class);
        doReturn(SchemaType.PLAIN).when(schemaInfoMock).type();

        PlainSchema plainSchemaMock = mock(PlainSchema.class);
        doReturn("binaryAttr").when(plainSchemaMock).getKey();
        doReturn(AttrSchemaType.Binary).when(plainSchemaMock).getType();

        doReturn(plainSchemaMock).when(schemaInfoMock).schema();
        doReturn(schemaInfoMock).when(nameMock).getSchemaInfo();
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));

        org.apache.syncope.common.lib.to.UserTO userTO = new org.apache.syncope.common.lib.to.UserTO();
        mappingManager.setIntValues(itemMock, attrMock, userTO);

        String expectedBase64 = java.util.Base64.getEncoder().encodeToString(binaryData);
        assertEquals(expectedBase64, userTO.getPlainAttr("binaryAttr").get().getValues().get(0));
    }

    @Test
    void testSetIntValues_PlainSchema_Membership() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        doReturn(List.of("PlainVal")).when(attrMock).getValue();

        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(null).when(nameMock).getField();

        IntAttrName.SchemaInfo schemaInfoMock = mock(IntAttrName.SchemaInfo.class);
        doReturn(SchemaType.PLAIN).when(schemaInfoMock).type();

        PlainSchema plainSchemaMock = mock(PlainSchema.class);
        doReturn("membPlainAttr").when(plainSchemaMock).getKey();
        doReturn(AttrSchemaType.String).when(plainSchemaMock).getType();
        doReturn(plainSchemaMock).when(schemaInfoMock).schema();
        doReturn(schemaInfoMock).when(nameMock).getSchemaInfo();

        doReturn("targetGroup").when(nameMock).getMembership();
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));

        Group groupMock = mock(Group.class);
        doReturn("groupKey123").when(groupMock).getKey();
        doReturn(Optional.of(groupMock)).when(groupDAO).findByName("targetGroup");

        org.apache.syncope.common.lib.to.UserTO userTO = new org.apache.syncope.common.lib.to.UserTO();
        mappingManager.setIntValues(itemMock, attrMock, userTO);

        assertEquals("PlainVal", userTO.getMemberships().get(0).getPlainAttr("membPlainAttr").get().getValues().get(0));
    }

    @Test
    void testSetIntValues_DerivedSchema_NoMembership() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        doReturn(List.of("DerVal")).when(attrMock).getValue();

        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(null).when(nameMock).getField();

        IntAttrName.SchemaInfo schemaInfoMock = mock(IntAttrName.SchemaInfo.class);
        doReturn(SchemaType.DERIVED).when(schemaInfoMock).type();

        DerSchema derSchemaMock = mock(DerSchema.class);
        doReturn("derAttr").when(derSchemaMock).getKey();
        doReturn(derSchemaMock).when(schemaInfoMock).schema();
        doReturn(schemaInfoMock).when(nameMock).getSchemaInfo();
        doReturn(null).when(nameMock).getMembership();
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));

        org.apache.syncope.common.lib.to.UserTO userTO = new org.apache.syncope.common.lib.to.UserTO();
        mappingManager.setIntValues(itemMock, attrMock, userTO);

        assertTrue(userTO.getDerAttr("derAttr").isPresent());
    }

    @Test
    void testSetIntValues_DerivedSchema_Membership() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        doReturn(List.of("DerVal")).when(attrMock).getValue();

        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(null).when(nameMock).getField();

        IntAttrName.SchemaInfo schemaInfoMock = mock(IntAttrName.SchemaInfo.class);
        doReturn(SchemaType.DERIVED).when(schemaInfoMock).type();

        DerSchema derSchemaMock = mock(DerSchema.class);
        doReturn("derMembAttr").when(derSchemaMock).getKey();
        doReturn(derSchemaMock).when(schemaInfoMock).schema();
        doReturn(schemaInfoMock).when(nameMock).getSchemaInfo();

        doReturn("targetGroupForDer").when(nameMock).getMembership();
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));

        Group groupMock = mock(Group.class);
        doReturn("groupKey999").when(groupMock).getKey();
        doReturn(Optional.of(groupMock)).when(groupDAO).findByName("targetGroupForDer");

        org.apache.syncope.common.lib.to.UserTO userTO = new org.apache.syncope.common.lib.to.UserTO();
        mappingManager.setIntValues(itemMock, attrMock, userTO);

        assertTrue(userTO.getMemberships().get(0).getDerAttr("derMembAttr").isPresent());
    }

    @Test
    void testSetIntValues_AttrNull() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));
        doReturn("password").when(nameMock).getField();
        org.apache.syncope.common.lib.to.UserTO userTO = new org.apache.syncope.common.lib.to.UserTO();

        mappingManager.setIntValues(itemMock, null, userTO);

        assertNull(userTO.getPassword());
    }

    @Test
    void testSetIntValues_ValuesGetFirstNull() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        List<Object> nullList = new java.util.ArrayList<>();
        nullList.add(null);
        doReturn(nullList).when(attrMock).getValue();
        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));
        doReturn("password").when(nameMock).getField();
        org.apache.syncope.common.lib.to.UserTO userTO = new org.apache.syncope.common.lib.to.UserTO();

        mappingManager.setIntValues(itemMock, attrMock, userTO);

        assertNull(userTO.getPassword());
    }

    @Test
    void testSetIntValues_WrongTO_GroupTOPassword() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        doReturn(List.of("value")).when(attrMock).getValue();
        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));
        doReturn("password").when(nameMock).getField();

        org.apache.syncope.common.lib.to.GroupTO groupTO = new org.apache.syncope.common.lib.to.GroupTO();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> mappingManager.setIntValues(itemMock, attrMock, groupTO));
    }

    @Test
    void testSetIntValues_WrongTO_UserTOName() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        doReturn(List.of("value")).when(attrMock).getValue();
        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));
        doReturn("name").when(nameMock).getField();

        org.apache.syncope.common.lib.to.UserTO userTO = new org.apache.syncope.common.lib.to.UserTO();
        mappingManager.setIntValues(itemMock, attrMock, userTO);

        assertNull(userTO.getUsername());
    }


    @Test
    void testSetIntValues_GroupTONotGroupable_PLAIN() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        doReturn(List.of("PlainVal")).when(attrMock).getValue();
        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(null).when(nameMock).getField();
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));
        doReturn("someGroup").when(nameMock).getMembership();
        IntAttrName.SchemaInfo schemaInfoMock = mock(IntAttrName.SchemaInfo.class);
        doReturn(SchemaType.PLAIN).when(schemaInfoMock).type();
        PlainSchema plainSchemaMock = mock(PlainSchema.class);
        doReturn("plainAttr").when(plainSchemaMock).getKey();
        doReturn(AttrSchemaType.String).when(plainSchemaMock).getType();
        doReturn(plainSchemaMock).when(schemaInfoMock).schema();
        doReturn(schemaInfoMock).when(nameMock).getSchemaInfo();

        org.apache.syncope.common.lib.to.GroupTO groupTO = new org.apache.syncope.common.lib.to.GroupTO();
        mappingManager.setIntValues(itemMock, attrMock, groupTO);

        assertEquals("PlainVal", groupTO.getPlainAttr("plainAttr").get().getValues().get(0));
    }

    @Test
    void testSetIntValues_GroupTONotGroupable_DERIVED() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        doReturn(List.of("PlainVal")).when(attrMock).getValue();
        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(null).when(nameMock).getField();
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));
        doReturn("someGroup").when(nameMock).getMembership();
        IntAttrName.SchemaInfo schemaInfoMock = mock(IntAttrName.SchemaInfo.class);
        doReturn(SchemaType.DERIVED).when(schemaInfoMock).type();
        DerSchema derSchemaMock = mock(DerSchema.class);
        doReturn("derAttr").when(derSchemaMock).getKey();
        doReturn(derSchemaMock).when(schemaInfoMock).schema();
        doReturn(schemaInfoMock).when(nameMock).getSchemaInfo();

        org.apache.syncope.common.lib.to.GroupTO groupTO = new org.apache.syncope.common.lib.to.GroupTO();
        mappingManager.setIntValues(itemMock, attrMock, groupTO);

        assertTrue(groupTO.getDerAttr("derAttr").isPresent());
    }

    @Test
    void testSetIntValues_GroupNotFoundInDAO_PLAIN() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        doReturn(List.of("Val")).when(attrMock).getValue();
        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(null).when(nameMock).getField();
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));
        doReturn("missingGroup").when(nameMock).getMembership();
        doReturn(Optional.empty()).when(groupDAO).findByName("missingGroup");
        IntAttrName.SchemaInfo schemaInfoMock = mock(IntAttrName.SchemaInfo.class);
        doReturn(SchemaType.PLAIN).when(schemaInfoMock).type();
        PlainSchema plainSchemaMock = mock(PlainSchema.class);
        doReturn("plainAttr").when(plainSchemaMock).getKey();
        doReturn(AttrSchemaType.String).when(plainSchemaMock).getType();
        doReturn(plainSchemaMock).when(schemaInfoMock).schema();
        doReturn(schemaInfoMock).when(nameMock).getSchemaInfo();

        org.apache.syncope.common.lib.to.UserTO userTO = new org.apache.syncope.common.lib.to.UserTO();
        mappingManager.setIntValues(itemMock, attrMock, userTO);

        assertEquals("Val", userTO.getPlainAttr("plainAttr").get().getValues().get(0));
    }

    @Test
    void testSetIntValues_GroupNotFoundInDAO_DERIVED() throws Exception {
        Item itemMock = mock(Item.class);
        when(itemMock.getIntAttrName()).thenReturn("dummy");
        org.identityconnectors.framework.common.objects.Attribute attrMock = mock(org.identityconnectors.framework.common.objects.Attribute.class);
        doReturn(List.of("Val")).when(attrMock).getValue();
        IntAttrName nameMock = mock(IntAttrName.class);
        doReturn(null).when(nameMock).getField();
        doReturn(nameMock).when(intAttrNameParser).parse(anyString(), any(AnyTypeKind.class));
        doReturn("missingGroup").when(nameMock).getMembership();
        doReturn(Optional.empty()).when(groupDAO).findByName("missingGroup");
        IntAttrName.SchemaInfo schemaInfoMock = mock(IntAttrName.SchemaInfo.class);
        doReturn(SchemaType.DERIVED).when(schemaInfoMock).type();
        DerSchema derSchemaMock = mock(DerSchema.class);
        doReturn("derAttr").when(derSchemaMock).getKey();
        doReturn(derSchemaMock).when(schemaInfoMock).schema();
        doReturn(schemaInfoMock).when(nameMock).getSchemaInfo();

        org.apache.syncope.common.lib.to.UserTO userTO = new org.apache.syncope.common.lib.to.UserTO();
        mappingManager.setIntValues(itemMock, attrMock, userTO);

        assertTrue(userTO.getDerAttr("derAttr").isPresent());
    }
}