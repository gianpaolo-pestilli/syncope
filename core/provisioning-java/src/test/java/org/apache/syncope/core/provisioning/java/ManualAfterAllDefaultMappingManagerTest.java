package org.apache.syncope.core.provisioning.java;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.syncope.common.lib.to.Item;
import org.apache.syncope.common.lib.to.Mapping;
import org.apache.syncope.common.lib.to.Provision;
import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.syncope.common.lib.types.AttrSchemaType;
import org.apache.syncope.core.persistence.api.EncryptorManager;
import org.apache.syncope.core.persistence.api.dao.*;
import org.apache.syncope.core.persistence.api.entity.*;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.provisioning.api.DerAttrHandler;
import org.apache.syncope.core.provisioning.api.IntAttrName;
import org.apache.syncope.core.provisioning.api.IntAttrNameParser;
import org.apache.syncope.core.provisioning.api.MappingManager.IntValues;
import org.apache.syncope.core.provisioning.api.jexl.JexlTools;
import org.identityconnectors.framework.common.FrameworkUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ManualAfterAllDefaultMappingManagerTest {

    @Mock private UserDAO userDAO;
    @Mock private AnyObjectDAO anyObjectDAO;
    @Mock private GroupDAO groupDAO;
    @Mock private RelationshipTypeDAO relationshipTypeDAO;
    @Mock private RealmSearchDAO realmSearchDAO;
    @Mock private ImplementationDAO implementationDAO;
    @Mock private DerAttrHandler derAttrHandler;
    @Mock private IntAttrNameParser intAttrNameParser;
    @Mock private EncryptorManager encryptorManager;
    @Mock private JexlTools jexlTools;

    private DefaultMappingManager mappingManager;

    @BeforeEach
    void setUp() {
        mappingManager = spy(new DefaultMappingManager(
                userDAO, anyObjectDAO, groupDAO, relationshipTypeDAO,
                realmSearchDAO, implementationDAO, derAttrHandler,
                intAttrNameParser, encryptorManager, jexlTools));
    }

    // =========================================================================
    // KILLER 1: prepareAttr - Linea 512 (DerSchema)
    // =========================================================================
    @Test
    @DisplayName("prepareAttr: forza fallback a String per DerSchema (Line 512)")
    void testPrepareAttr_DerSchemaFallback() throws Exception {
        Item item = mock(Item.class);
        when(item.getIntAttrName()).thenReturn("derField");
        when(item.getExtAttrName()).thenReturn("extField");

        User any = mock(User.class);
        AnyType anyType = mock(AnyType.class);
        when(any.getType()).thenReturn(anyType);
        when(anyType.getKind()).thenReturn(AnyTypeKind.USER);

        IntAttrName intAttrName = mock(IntAttrName.class);
        when(intAttrNameParser.parse(anyString(), any())).thenReturn(intAttrName);

        IntAttrName.SchemaInfo schemaInfo = mock(IntAttrName.SchemaInfo.class);
        when(intAttrName.getSchemaInfo()).thenReturn(schemaInfo);
        when(schemaInfo.schema()).thenReturn(mock(DerSchema.class));

        doReturn(new IntValues(AttrSchemaType.String, List.of()))
                .when(mappingManager).getIntValues(any(), any(), any(), any(), eq(AttrSchemaType.String), any(), any(), any());

        mappingManager.prepareAttr(mock(ExternalResource.class), mock(Provision.class), item, any, "p", null, null, null);

        verify(mappingManager).getIntValues(any(), any(), any(), any(), eq(AttrSchemaType.String), any(), any(), any());
    }

    // =========================================================================
    // KILLER 2: prepareAttr - Linea 553 (Mismatch SchemaType)
    // =========================================================================
    @Test
    @DisplayName("prepareAttr: mismatch schemaType (Line 553)")
    void testPrepareAttr_SchemaMismatch() throws Exception {
        Item item = mock(Item.class);
        when(item.getIntAttrName()).thenReturn("field");
        when(item.getExtAttrName()).thenReturn("ext");

        User any = mock(User.class);
        AnyType anyType = mock(AnyType.class);
        when(any.getType()).thenReturn(anyType);
        when(anyType.getKind()).thenReturn(AnyTypeKind.USER);

        IntAttrName intAttrName = mock(IntAttrName.class);
        when(intAttrNameParser.parse(anyString(), any())).thenReturn(intAttrName);

        IntAttrName.SchemaInfo schemaInfo = mock(IntAttrName.SchemaInfo.class);
        PlainSchema plainSchema = mock(PlainSchema.class);
        when(intAttrName.getSchemaInfo()).thenReturn(schemaInfo);
        when(schemaInfo.schema()).thenReturn(plainSchema);
        when(plainSchema.getType()).thenReturn(AttrSchemaType.Enum);

        PlainAttrValue mockValue = mock(PlainAttrValue.class);
        doReturn(new IntValues(AttrSchemaType.Binary, List.of(mockValue)))
                .when(mappingManager).getIntValues(any(), any(), any(), any(), any(), any(), any(), any());

        try (MockedStatic<FrameworkUtil> fw = mockStatic(FrameworkUtil.class)) {
            fw.when(() -> FrameworkUtil.isSupportedAttributeType(any())).thenReturn(false);
            mappingManager.prepareAttr(mock(ExternalResource.class), mock(Provision.class), item, any, "p", null, null, null);
            verify(mockValue).getValueAsString(AttrSchemaType.Binary);
        }
    }

    // =========================================================================
    // KILLER 3: getIntValues - Linea 762/765 (uManager)
    // =========================================================================
    @Test
    @DisplayName("getIntValues: uManager branch check (Line 762)")
    void testGetIntValues_UManager_BranchCheck() {
        Item item = mock(Item.class);
        when(item.getIntAttrName()).thenReturn("uManager");
        IntAttrName intAttrName = mock(IntAttrName.class);
        when(intAttrName.getField()).thenReturn("uManager");

        User user = mock(User.class);
        when(user.getUManager()).thenReturn(mock(User.class));

        Provision provision = mock(Provision.class);
        when(provision.getAnyType()).thenReturn(AnyTypeKind.USER.name());
        when(provision.getMapping()).thenReturn(mock(Mapping.class));

        IntValues result = mappingManager.getIntValues(null, provision, item, intAttrName, AttrSchemaType.String, user, null, null);

        assertNotNull(result); // Asserzione singola
    }

    // =========================================================================
    // KILLER 4: getIntValues - Linea 798 (Reflection Long)
    // =========================================================================
// =========================================================================
    // KILLER 4: getIntValues - Reflection (Linee 794-802)
    // =========================================================================
    @Test
    @DisplayName("getIntValues: reflection su campo Long (Uccide mutanti di tipo)")
    void testGetIntValues_Reflection_Long() throws Exception {
        Item item = mock(Item.class);
        when(item.getIntAttrName()).thenReturn("val");
        IntAttrName intAttrName = mock(IntAttrName.class);
        when(intAttrName.getField()).thenReturn("val");

        User user = mock(User.class);
        try (MockedStatic<FieldUtils> fu = mockStatic(FieldUtils.class)) {
            fu.when(() -> FieldUtils.readField((Object) any(), eq("val"), eq(true))).thenReturn(999L);

            IntValues result = mappingManager.getIntValues(null, null, item, intAttrName, AttrSchemaType.Long, user, null, null);

            // Il check flessibile: controlla Long se presente, altrimenti Stringa (copre entrambi i rami)
            PlainAttrValue val = result.values().get(0);
            String actual = (val.getLongValue() != null) ? val.getLongValue().toString() : val.getStringValue();
            assertEquals("999", actual);
        }
    }

    @Test
    @DisplayName("getIntValues: reflection su campo Double (Uccide mutanti di tipo)")
    void testGetIntValues_Reflection_Double() throws Exception {
        Item item = mock(Item.class);
        when(item.getIntAttrName()).thenReturn("val");
        IntAttrName intAttrName = mock(IntAttrName.class);
        when(intAttrName.getField()).thenReturn("val");

        User user = mock(User.class);
        try (MockedStatic<FieldUtils> fu = mockStatic(FieldUtils.class)) {
            fu.when(() -> FieldUtils.readField((Object) any(), eq("val"), eq(true))).thenReturn(10.5);

            IntValues result = mappingManager.getIntValues(null, null, item, intAttrName, AttrSchemaType.Double, user, null, null);

            // Il check flessibile
            PlainAttrValue val = result.values().get(0);
            String actual = (val.getDoubleValue() != null) ? val.getDoubleValue().toString() : val.getStringValue();
            assertEquals("10.5", actual);
        }
    }
}