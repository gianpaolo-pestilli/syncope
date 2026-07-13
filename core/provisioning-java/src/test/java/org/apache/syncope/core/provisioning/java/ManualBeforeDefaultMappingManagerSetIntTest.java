package org.apache.syncope.core.provisioning.java;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.util.List;

import org.apache.syncope.common.lib.Attr;
import org.apache.syncope.common.lib.to.*;
import org.apache.syncope.common.lib.types.SchemaType;
import org.apache.syncope.common.lib.types.AttrSchemaType;
import org.apache.syncope.core.persistence.api.dao.ImplementationDAO;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.persistence.api.entity.PlainSchema;
import org.apache.syncope.core.provisioning.api.IntAttrName;
import org.apache.syncope.core.provisioning.api.IntAttrNameParser;

import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ManualBeforeDefaultMappingManagerSetIntTest {

    @Mock private IntAttrNameParser intAttrNameParser;

    // I Mock "Salvavita" scoperti analizzando il codice sorgente
    @Mock private ImplementationDAO implementationDAO;
    @Mock private GroupDAO groupDAO;

    @InjectMocks private DefaultMappingManager mappingManager;

    private Item baseItem;
    private Attribute baseAttr;
    private IntAttrName baseIntAttrName;

    @BeforeEach
    void setUpBaseMocks() throws ParseException {
        baseItem = mock(Item.class);
        lenient().when(baseItem.getIntAttrName()).thenReturn("username");

        baseAttr = AttributeBuilder.build("username", "valoreMappato");

        baseIntAttrName = mock(IntAttrName.class);
        lenient().when(baseIntAttrName.getField()).thenReturn("username");
        lenient().when(intAttrNameParser.parse(anyString(), any())).thenReturn(baseIntAttrName);
    }

    private void setupPlainSchemaMock(String schemaName) {
        IntAttrName.SchemaInfo schemaInfo = mock(IntAttrName.SchemaInfo.class);
        PlainSchema schema = mock(PlainSchema.class);
        lenient().when(schemaInfo.type()).thenReturn(SchemaType.PLAIN);
        lenient().when(schemaInfo.schema()).thenReturn(schema);
        lenient().when(schema.getKey()).thenReturn(schemaName);
        lenient().when(schema.getType()).thenReturn(AttrSchemaType.String);
        lenient().when(baseIntAttrName.getField()).thenReturn(null);
        lenient().when(baseIntAttrName.getSchemaInfo()).thenReturn(schemaInfo);
    }

    // =========================================================================
    // GRUPPO 1: IL CASO BASE
    // =========================================================================
    @Test
    @DisplayName("TC01 - Base Choice: UserTO | Singolo | Senza Trasf | Parzialmente Popolato")
    void testTC01_BaseChoice() {
        UserTO userTO = new UserTO();
        setupPlainSchemaMock("username");

        mappingManager.setIntValues(baseItem, baseAttr, userTO);

        assertEquals("valoreMappato", userTO.getPlainAttr("username").get().getValues().get(0));
    }

    // =========================================================================
    // GRUPPO 2: VARIAZIONE DELL'ENTITÀ (AnyTO)
    // =========================================================================
    @Nested
    @DisplayName("Variazione della dimensione: Input (AnyTO)")
    class AnyTOVariations {

        @Test
        @DisplayName("TC02 - GroupTO | Singolo | Senza Trasf | Parzialmente Popolato")
        void testTC02_GroupTO() {
            GroupTO groupTO = new GroupTO();
            setupPlainSchemaMock("name");

            mappingManager.setIntValues(baseItem, baseAttr, groupTO);

            assertEquals("valoreMappato", groupTO.getPlainAttr("name").get().getValues().get(0));
        }

        @Test
        @DisplayName("TC03 - AnyObjectTO | Singolo | Senza Trasf | Parzialmente Popolato")
        void testTC03_AnyObjectTO() {
            AnyObjectTO anyObjectTO = new AnyObjectTO();
            setupPlainSchemaMock("macAddress");

            mappingManager.setIntValues(baseItem, baseAttr, anyObjectTO);

            assertEquals("valoreMappato", anyObjectTO.getPlainAttr("macAddress").get().getValues().get(0));
        }

        @Test
        @DisplayName("TC04 - Input AnyTO Null (Contratto Fail-Fast su Null AnyTO)")
        void testTC04_NullAnyTO() {
            assertThrows(NullPointerException.class, () -> mappingManager.setIntValues(baseItem, baseAttr, (AnyTO) null));
        }
    }

    // =========================================================================
    // GRUPPO 3: VARIAZIONE DEL DATO (Attribute)
    // =========================================================================
    @Nested
    @DisplayName("Variazione della dimensione: Input (Attribute)")
    class AttributeVariations {

        @Test
        @DisplayName("TC05 - UserTO | Multivalore | Senza Trasf | Parzialmente Popolato")
        void testTC05_MultiValue() {
            UserTO userTO = new UserTO();
            setupPlainSchemaMock("emails");
            Attribute multiAttr = AttributeBuilder.build("emails", "a@test.com", "b@test.com");

            mappingManager.setIntValues(baseItem, multiAttr, userTO);

            assertEquals(2, userTO.getPlainAttr("emails").get().getValues().size());
        }

        @Test
        @DisplayName("TC06 - UserTO | Vuoto | Senza Trasf | Parzialmente Popolato")
        void testTC06_EmptyAttribute() {
            UserTO userTO = new UserTO();
            setupPlainSchemaMock("nickname");
            Attribute emptyAttr = AttributeBuilder.build("nickname");

            mappingManager.setIntValues(baseItem, emptyAttr, userTO);

            assertTrue(userTO.getPlainAttr("nickname").get().getValues().isEmpty());
        }

        @Test
        @DisplayName("TC07 - Input Attribute Null (Robustezza - Salta il mapping)")
        void testTC07_NullAttribute() {
            UserTO userTO = new UserTO();
            setupPlainSchemaMock("nickname");

            mappingManager.setIntValues(baseItem, null, userTO);

            assertTrue(userTO.getPlainAttr("nickname").isEmpty());
        }
    }

    // =========================================================================
    // GRUPPO 4: VARIAZIONE DELLA CONFIGURAZIONE (Item)
    // =========================================================================
    @Nested
    @DisplayName("Variazione della dimensione: Input (Item)")
    class ItemVariations {

        @Test
        @DisplayName("TC08 - UserTO | Singolo | CON Trasformazione | Parzialmente Popolato")
        void testTC08_WithTransformation() {
            UserTO userTO = new UserTO();
            setupPlainSchemaMock("customAttr");
            Item transformedItem = mock(Item.class);
            lenient().when(transformedItem.getIntAttrName()).thenReturn("customAttr");
            lenient().when(transformedItem.getTransformers()).thenReturn(List.of("T1"));
            Attribute rawAttr = AttributeBuilder.build("customAttr", "rawPassword");

            mappingManager.setIntValues(transformedItem, rawAttr, userTO);

            assertEquals("rawPassword", userTO.getPlainAttr("customAttr").get().getValues().get(0));
        }

        @Test
        @DisplayName("TC09 - Input Item Null (Contratto Fail-Fast su Item)")
        void testTC09_NullItem() {
            UserTO userTO = new UserTO();
            assertThrows(NullPointerException.class, () -> mappingManager.setIntValues(null, baseAttr, userTO));
        }

        @Test
        @DisplayName("TC10 - Input Item Non Valido (Ritorno Silenzioso)")
        void testTC10_InvalidItem() throws ParseException {
            UserTO userTO = new UserTO();
            setupPlainSchemaMock("fail");
            Item invalidItem = mock(Item.class);
            lenient().when(invalidItem.getIntAttrName()).thenReturn("fail");
            lenient().when(intAttrNameParser.parse(anyString(), any())).thenThrow(new ParseException("Syntax error", 0));

            mappingManager.setIntValues(invalidItem, baseAttr, userTO);

            assertTrue(userTO.getPlainAttrs().isEmpty());
        }
    }

    // =========================================================================
    // GRUPPO 5: VARIAZIONE DELLO STATO (Sdoppiati per garantire 1 Assert/Test)
    // =========================================================================
    @Nested
    @DisplayName("Variazione della dimensione: Stato Oggetto")
    class StateVariations {

        @Test
        @DisplayName("TC11 - UserTO | Singolo | Senza Trasf | Non Inizializzato (Vergine)")
        void testTC11_UninitializedState() {
            UserTO userTO = new UserTO();
            setupPlainSchemaMock("department");

            mappingManager.setIntValues(baseItem, baseAttr, userTO);

            assertEquals("valoreMappato", userTO.getPlainAttr("department").get().getValues().get(0));
        }

        @Test
        @DisplayName("TC12 - UserTO | Singolo | Totalmente Configurato (Focus: Inserimento)")
        void testTC12_FullyConfiguredState_NewData() {
            UserTO userTO = new UserTO();
            userTO.getPlainAttrs().add(new Attr.Builder("existingAttr").value("oldValue").build());
            setupPlainSchemaMock("newAttr");

            mappingManager.setIntValues(baseItem, baseAttr, userTO);

            assertEquals("valoreMappato", userTO.getPlainAttr("newAttr").get().getValues().get(0));
        }

        @Test
        @DisplayName("TC13 - UserTO | Singolo | Totalmente Configurato (Focus: Integrità pre-esistente)")
        void testTC13_FullyConfiguredState_OldDataIntact() {
            UserTO userTO = new UserTO();
            userTO.getPlainAttrs().add(new Attr.Builder("existingAttr").value("oldValue").build());
            setupPlainSchemaMock("newAttr");

            mappingManager.setIntValues(baseItem, baseAttr, userTO);

            assertEquals("oldValue", userTO.getPlainAttr("existingAttr").get().getValues().get(0));
        }
    }

    // =========================================================================
    // GRUPPO 6: COMBINAZIONI E EDGE CASES
    // =========================================================================
    @Nested
    @DisplayName("Combinazioni Pairwise ed Edge Cases")
    class ComplexCombinations {

        @Test
        @DisplayName("TC14 - GroupTO | Multivalore | Totalmente Configurato (Focus: Inserimento)")
        void testTC14_Group_Multi_Configured_NewData() {
            GroupTO groupTO = new GroupTO();
            groupTO.getPlainAttrs().add(new Attr.Builder("oldTag").value("v1").build());
            setupPlainSchemaMock("tags");
            Attribute multiAttr = AttributeBuilder.build("tags", "tag1", "tag2");

            mappingManager.setIntValues(baseItem, multiAttr, groupTO);

            assertEquals(2, groupTO.getPlainAttr("tags").get().getValues().size());
        }

        @Test
        @DisplayName("TC15 - GroupTO | Multivalore | Totalmente Configurato (Focus: Integrità vecchi dati)")
        void testTC15_Group_Multi_Configured_OldDataIntact() {
            GroupTO groupTO = new GroupTO();
            groupTO.getPlainAttrs().add(new Attr.Builder("oldTag").value("v1").build());
            setupPlainSchemaMock("tags");
            Attribute multiAttr = AttributeBuilder.build("tags", "tag1", "tag2");

            mappingManager.setIntValues(baseItem, multiAttr, groupTO);

            assertEquals("v1", groupTO.getPlainAttr("oldTag").get().getValues().get(0));
        }

        @Test
        @DisplayName("TC16 - AnyObjectTO | Vuoto | Senza Trasf | Non Inizializzato")
        void testTC16_AnyObject_Empty_Uninitialized() {
            AnyObjectTO anyObjectTO = new AnyObjectTO();
            setupPlainSchemaMock("emptyField");
            Attribute emptyAttr = AttributeBuilder.build("emptyField");

            mappingManager.setIntValues(baseItem, emptyAttr, anyObjectTO);

            assertTrue(anyObjectTO.getPlainAttr("emptyField").get().getValues().isEmpty());
        }

        @Test
        @DisplayName("TC17 - Robustezza Estrema: Tutti gli input NULL")
        void testTC17_AllInputsNull() {
            assertThrows(NullPointerException.class, () -> mappingManager.setIntValues(null, null, (AnyTO) null));
        }

        @Test
        @DisplayName("TC18 - UserTO | Multivalore | CON Trasformazione | Parzialmente Popolato")
        void testTC18_User_Multi_Transform_Partial() {
            UserTO userTO = new UserTO();
            setupPlainSchemaMock("roles");
            Item transformedItem = mock(Item.class);
            lenient().when(transformedItem.getIntAttrName()).thenReturn("roles");
            lenient().when(transformedItem.getTransformers()).thenReturn(List.of("T1"));
            Attribute multiAttr = AttributeBuilder.build("roles", "roleA", "roleB");

            mappingManager.setIntValues(transformedItem, multiAttr, userTO);

            assertEquals(2, userTO.getPlainAttr("roles").get().getValues().size());
        }

        @Test
        @DisplayName("TC19 - GroupTO | Attribute Null | Totalmente Configurato (Focus: Integrità)")
        void testTC19_Group_NullAttr_Configured_OldDataIntact() {
            GroupTO groupTO = new GroupTO();
            groupTO.getPlainAttrs().add(new Attr.Builder("oldTag").value("v1").build());
            setupPlainSchemaMock("newTag");

            mappingManager.setIntValues(baseItem, null, groupTO);

            assertEquals("v1", groupTO.getPlainAttr("oldTag").get().getValues().get(0));
        }

        @Test
        @DisplayName("TC20 - AnyObjectTO | Singolo | Totalmente Configurato (Focus: Integrità)")
        void testTC20_AnyObject_Single_Configured_OldDataIntact() {
            AnyObjectTO anyObjectTO = new AnyObjectTO();
            anyObjectTO.getPlainAttrs().add(new Attr.Builder("oldField").value("v1").build());
            setupPlainSchemaMock("newField");

            mappingManager.setIntValues(baseItem, baseAttr, anyObjectTO);

            assertEquals("v1", anyObjectTO.getPlainAttr("oldField").get().getValues().get(0));
        }
    }
}