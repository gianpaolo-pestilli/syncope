package org.apache.syncope.core.provisioning.java;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.apache.syncope.common.lib.to.Item;
import org.apache.syncope.common.lib.to.Provision;
import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.syncope.common.lib.types.AttrSchemaType;
import org.apache.syncope.common.lib.types.SchemaType;
import org.apache.syncope.core.persistence.api.entity.Any;
import org.apache.syncope.core.persistence.api.entity.AnyType;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.PlainSchema;
import org.apache.syncope.core.persistence.api.dao.ImplementationDAO;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.dao.AnyObjectDAO;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.persistence.api.dao.RelationshipTypeDAO;
import org.apache.syncope.core.persistence.api.dao.RealmSearchDAO;
import org.apache.syncope.core.persistence.api.EncryptorManager;
import org.apache.syncope.core.provisioning.api.DerAttrHandler;
import org.apache.syncope.core.provisioning.api.jexl.JexlTools;
import org.apache.syncope.core.provisioning.api.AccountGetter;
import org.apache.syncope.core.provisioning.api.PlainAttrGetter;
import org.apache.syncope.core.provisioning.api.IntAttrNameParser;
import org.apache.syncope.core.provisioning.api.IntAttrName;
import org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttr;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ManualBeforeDefaultMappingManagerPrepAttrTest {

    @InjectMocks
    private DefaultMappingManager mappingManager;

    // --- DIPENDENZE DEL COSTRUTTORE (Tutte mockate per evitare NPE nascosti) ---
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

    // --- OGGETTI DEL DOMINIO DA MOCKARE ---
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

    @BeforeEach
    public void setUp() throws Exception {
        // --- Setup catena di mocking vitale ---

        // 1. Any chain
        when(any.getType()).thenReturn(anyType);
        when(anyType.getKind()).thenReturn(AnyTypeKind.USER);

        // 2. Item & Parser chain
        when(item.getIntAttrName()).thenReturn("dummyField");
        when(item.getExtAttrName()).thenReturn("extName");
        when(item.getTransformers()).thenReturn(List.of()); // <-- FIX FONDAMENTALE: Previene NPE nello stream
        when(intAttrNameParser.parse(anyString(), any(AnyTypeKind.class))).thenReturn(intAttrName);

        // 3. Schema chain
        when(intAttrName.getSchemaInfo()).thenReturn(schemaInfo);
        when(schemaInfo.type()).thenReturn(SchemaType.PLAIN);
        when(schemaInfo.schema()).thenReturn(plainSchema);
        when(plainSchema.getKey()).thenReturn("schemaKey");
        when(plainSchema.getType()).thenReturn(AttrSchemaType.String);
    }

    // --- GRUPPO 1: VALIDAZIONE INPUT ANY ---
    @Test
    public void TC01_Any_Null() {
        assertThrows(NullPointerException.class, () ->
                mappingManager.prepareAttr(resource, provision, item, null, "pwd", accGetter, accGetter, attrGetter));
    }

    @Test
    public void TC02_Any_UserKind() {
        when(anyType.getKind()).thenReturn(AnyTypeKind.USER);
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals("extName", res.attribute().getName());
    }

    @Test
    public void TC03_Any_GroupKind() {
        when(anyType.getKind()).thenReturn(AnyTypeKind.GROUP);
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals("extName", res.attribute().getName());
    }

    @Test
    public void TC04_Any_AnyObjectKind() {
        when(anyType.getKind()).thenReturn(AnyTypeKind.ANY_OBJECT);
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals("extName", res.attribute().getName());
    }

    // --- GRUPPO 2: VALIDAZIONE ITEM ---
    @Test
    public void TC05_Item_Null() {
        assertThrows(NullPointerException.class, () ->
                mappingManager.prepareAttr(resource, provision, null, any, "pwd", accGetter, accGetter, attrGetter));
    }

    @Test
    public void TC06_Item_SimpleName() {
        when(item.getExtAttrName()).thenReturn("simpleName");
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals("simpleName", res.attribute().getName());
    }

    @Test
    public void TC07_Item_WithTransformers() {
        // Mocking list con 1 transformer valido testato in isolamento
        when(item.getTransformers()).thenReturn(List.of("T1"));
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals("extName", res.attribute().getName());
    }

    @Test
    public void TC08_Item_ConnObjectKey() {
        when(item.isConnObjectKey()).thenReturn(true);
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        // Se è ConnObjectKey senza valori, setta la chiave a null nel field attributes
        assertNull(res.attribute());
    }

    // --- GRUPPO 3: VALIDAZIONE RISORSA/PROVISION ---
    @Test
    public void TC09_Resource_Null() {
        PreparedAttr res = mappingManager.prepareAttr(null, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals("extName", res.attribute().getName());
    }

    @Test
    public void TC10_Resource_Invalid() {
        PreparedAttr res = mappingManager.prepareAttr(mock(ExternalResource.class), provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals("extName", res.attribute().getName());
    }

    @Test
    public void TC11_Provision_Null() {
        PreparedAttr res = mappingManager.prepareAttr(resource, null, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals("extName", res.attribute().getName());
    }

    @Test
    public void TC12_Provision_Valid() {
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals("extName", res.attribute().getName());
    }

    // --- GRUPPO 4: VALIDAZIONE GETTER ---
    @Test
    public void TC13_Getter_UsernameFail() {
        when(item.getExtAttrName()).thenReturn("uFail");
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", (a) -> null, accGetter, attrGetter);
        assertEquals("uFail", res.attribute().getName());
    }

    @Test
    public void TC14_Getter_PasswordFail() {
        when(item.getExtAttrName()).thenReturn("pFail");
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, (a) -> null, attrGetter);
        assertEquals("pFail", res.attribute().getName());
    }

    @Test
    public void TC15_Getter_PlainAttrFail() {
        when(item.getExtAttrName()).thenReturn("attrFail");
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, (a, b) -> null);
        assertEquals("attrFail", res.attribute().getName());
    }

    @Test
    public void TC16_Getter_AllValid() {
        when(item.getExtAttrName()).thenReturn("validGetter");
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "pwd", accGetter, accGetter, attrGetter);
        assertEquals("validGetter", res.attribute().getName());
    }

    // --- GRUPPO 5: VALIDAZIONE PASSWORD STRING ---
    @Test
    public void TC17_Pass_Null() {
        when(item.getExtAttrName()).thenReturn("passNull");
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, null, accGetter, accGetter, attrGetter);
        assertEquals("passNull", res.attribute().getName());
    }

    @Test
    public void TC18_Pass_Empty() {
        when(item.getExtAttrName()).thenReturn("passEmpty");
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "", accGetter, accGetter, attrGetter);
        assertEquals("passEmpty", res.attribute().getName());
    }

    @Test
    public void TC19_Pass_Short() {
        when(item.getExtAttrName()).thenReturn("passShort");
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "a", accGetter, accGetter, attrGetter);
        assertEquals("passShort", res.attribute().getName());
    }

    @Test
    public void TC20_Pass_Complex() {
        when(item.getExtAttrName()).thenReturn("passComplex");
        PreparedAttr res = mappingManager.prepareAttr(resource, provision, item, any, "Secret123!", accGetter, accGetter, attrGetter);
        assertEquals("passComplex", res.attribute().getName());
    }
}