
package org.apache.syncope.core.provisioning.java;
import org.apache.syncope.common.lib.types.AttrSchemaType;
import org.apache.syncope.common.lib.types.SchemaType;
import org.apache.syncope.core.persistence.api.dao.*;
import org.apache.syncope.core.persistence.api.entity.Any;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.anyobject.AnyObject;
import org.apache.syncope.core.persistence.api.entity.group.Group;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.provisioning.api.AccountGetter;
import org.apache.syncope.core.provisioning.api.DerAttrHandler;
import org.apache.syncope.core.provisioning.api.IntAttrName;
import org.apache.syncope.core.provisioning.api.IntAttrNameParser;
import org.apache.syncope.core.provisioning.api.PlainAttrGetter;
import org.apache.syncope.core.provisioning.api.jexl.JexlTools;
import org.apache.syncope.core.persistence.api.EncryptorManager;
import org.apache.syncope.common.lib.to.Item;
import org.apache.syncope.common.lib.to.Provision;
import org.apache.syncope.core.provisioning.api.MappingManager.IntValues;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ManualBeforeDefaultMappingManagerGetIntTest {

    private DefaultMappingManager mappingManager;

    @Mock private UserDAO userDAOMock;
    @Mock private AnyObjectDAO anyObjectDAOMock;
    @Mock private GroupDAO groupDAOMock;
    @Mock private RelationshipTypeDAO relationshipTypeDAOMock;
    @Mock private RealmSearchDAO realmSearchDAOMock;
    @Mock private ImplementationDAO implementationDAOMock;
    @Mock private DerAttrHandler derAttrHandlerMock;
    @Mock private IntAttrNameParser intAttrNameParserMock;
    @Mock private EncryptorManager encryptorManagerMock;
    @Mock private JexlTools jexlToolsMock;

    @Mock private ExternalResource resourceMock;
    @Mock private Provision provisionMock;
    @Mock private Item itemMock;
    @Mock private IntAttrName intAttrNameMock;
    @Mock private User userMock;
    @Mock private Group groupMock;
    @Mock private AnyObject anyObjectMock;
    @Mock private AccountGetter accountGetterMock;
    @Mock private PlainAttrGetter plainAttrGetterMock;

    @BeforeEach
    void setUp() {
        mappingManager = new DefaultMappingManager(
                userDAOMock, anyObjectDAOMock, groupDAOMock, relationshipTypeDAOMock,
                realmSearchDAOMock, implementationDAOMock, derAttrHandlerMock,
                intAttrNameParserMock, encryptorManagerMock, jexlToolsMock
        );
    }

    // =========================================================================
    // SEZIONE 1: HAPPY PATH E POLIMORFISMO DELLE ENTITÀ
    // =========================================================================

    @Test
    @DisplayName("TC01: Entità Utente, Dato Anagrafico, In chiaro, Presente")
    void testUserHappyPath() {
        lenient().when(intAttrNameMock.getField()).thenReturn("key");
        lenient().when(userMock.getKey()).thenReturn("user-123");

        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.String,
                userMock, accountGetterMock, plainAttrGetterMock
        );

        assertEquals("user-123", result.values().get(0).getStringValue(), "Il valore estratto deve coincidere con la chiave mockata");
    }

    @Test
    @DisplayName("TC02: Entità Gruppo, Dato Relazionale, In chiaro, Presente")
    void testGroupHappyPath() {
        lenient().when(intAttrNameMock.getField()).thenReturn("key");
        lenient().when(groupMock.getKey()).thenReturn("group-123");

        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.String,
                groupMock, accountGetterMock, plainAttrGetterMock
        );

        assertFalse(result.values().isEmpty(), "Il manager deve estrarre il dato da un'entità Group");
    }

    @Test
    @DisplayName("TC03: Entità AnyObject, Dato Anagrafico, In chiaro, Presente")
    void testAnyObjectHappyPath() {
        lenient().when(intAttrNameMock.getField()).thenReturn("key");
        lenient().when(anyObjectMock.getKey()).thenReturn("any-123");

        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.String,
                anyObjectMock, accountGetterMock, plainAttrGetterMock
        );

        assertFalse(result.values().isEmpty(), "Il manager deve elaborare correttamente un AnyObject");
    }

    // =========================================================================
    // SEZIONE 2: FORMATI E SICUREZZA (AttrSchemaType)
    // =========================================================================

    @Test
    @DisplayName("TC04: Dato di Sicurezza, Cifrato")
    void testEncryptedData() {
        lenient().when(intAttrNameMock.getField()).thenReturn("key");
        lenient().when(userMock.getKey()).thenReturn("encrypted-val");

        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.Encrypted,
                userMock, accountGetterMock, plainAttrGetterMock
        );

        assertEquals(AttrSchemaType.Encrypted, result.attrSchemaType(), "Lo schema type di ritorno deve essere preservato come Encrypted");
    }

    @Test
    @DisplayName("TC05: Dato Speciale, Binario")
    void testBinaryData() {
        lenient().when(intAttrNameMock.getField()).thenReturn("key");
        lenient().when(userMock.getKey()).thenReturn("binary-val");

        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.Binary,
                userMock, accountGetterMock, plainAttrGetterMock
        );

        assertEquals(AttrSchemaType.Binary, result.attrSchemaType(), "Il formato binario deve essere mantenuto nel risultato");
    }

    @Test
    @DisplayName("TC06: Dato Speciale, Numerico (Long)")
    void testNumericData() {
        lenient().when(intAttrNameMock.getField()).thenReturn("key");
        lenient().when(userMock.getKey()).thenReturn("1000");

        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.Long,
                userMock, accountGetterMock, plainAttrGetterMock
        );

        assertEquals(AttrSchemaType.Long, result.attrSchemaType(), "Il formato numerico Long deve essere preservato");
    }

    @Test
    @DisplayName("TC07: Dato Speciale, Booleano")
    void testBooleanData() {
        lenient().when(intAttrNameMock.getField()).thenReturn("suspended");
        lenient().when(userMock.isSuspended()).thenReturn(true);

        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.Boolean,
                userMock, accountGetterMock, plainAttrGetterMock
        );

        assertFalse(result.values().isEmpty(), "Il campo booleano deve essere estratto con successo");
    }

    @Test
    @DisplayName("TC08: Dato Speciale, Data")
    void testDateData() {
        lenient().when(intAttrNameMock.getField()).thenReturn("key");
        lenient().when(userMock.getKey()).thenReturn("2023-10-10");

        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.Date,
                userMock, accountGetterMock, plainAttrGetterMock
        );

        assertEquals(AttrSchemaType.Date, result.attrSchemaType(), "Il tipo Date deve riflettersi nell'output");
    }

    // =========================================================================
    // SEZIONE 3: ROBUSTEZZA E NEGATIVE TESTING (Input Nulli o Invalidi)
    // =========================================================================

    @Test
    @DisplayName("TC09: Input Any Nullo - Tolleranza ai Guasti")
    void testNullAny() {
        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.String,
                null, accountGetterMock, plainAttrGetterMock
        );

        assertTrue(result.values().isEmpty(), "Il metodo deve gestire Any nullo degradando in modo sicuro con lista vuota");
    }

    @Test
    @DisplayName("TC10: Input IntAttrName Nullo")
    void testNullIntAttrName() {
        assertThrows(NullPointerException.class, () -> mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, null, AttrSchemaType.String,
                userMock, accountGetterMock, plainAttrGetterMock
        ), "L'assenza del puntatore all'attributo deve causare una NPE");
    }

    @Test
    @DisplayName("TC11: Input AttrSchemaType Nullo")
    void testNullAttrSchemaType() {
        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, null,
                userMock, accountGetterMock, plainAttrGetterMock
        );

        assertNull(result.attrSchemaType(), "Se omesso, lo schema rimane nullo ma la logica non deve bloccarsi");
    }

    @Test
    @DisplayName("TC12: Input Item Nullo")
    void testNullItem() {
        assertThrows(NullPointerException.class, () -> mappingManager.getIntValues(
                resourceMock, provisionMock, null, intAttrNameMock, AttrSchemaType.String,
                userMock, accountGetterMock, plainAttrGetterMock
        ), "Item nullo è un requisito rigido e deve scatenare un'eccezione");
    }

    @Test
    @DisplayName("TC13: Input ExternalResource Nullo")
    void testNullExternalResource() {
        IntValues result = mappingManager.getIntValues(
                null, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.String,
                userMock, accountGetterMock, plainAttrGetterMock
        );

        assertTrue(result.values().isEmpty(), "Una risorsa assente impedisce l'estrazione e produce lista vuota");
    }

    @Test
    @DisplayName("TC14: Input Provision Nullo")
    void testNullProvision() {
        IntValues result = mappingManager.getIntValues(
                resourceMock, null, itemMock, intAttrNameMock, AttrSchemaType.String,
                userMock, accountGetterMock, plainAttrGetterMock
        );

        assertNotNull(result, "Il sistema deve restituire una struttura IntValues valida anche senza Provision");
    }

    // =========================================================================
    // SEZIONE 4: COMPORTAMENTO DEI DELEGATI (Stato del dato cercato)
    // =========================================================================

    @Test
    @DisplayName("TC15: PlainAttrGetter restituisce Null (Dato Non Presente)")
    void testPlainAttrGetterReturnsNull() {
        IntAttrName.SchemaInfo schemaInfoMock = mock(IntAttrName.SchemaInfo.class);
        lenient().when(schemaInfoMock.type()).thenReturn(SchemaType.PLAIN);

        org.apache.syncope.core.persistence.api.entity.Schema schemaMock =
                mock(org.apache.syncope.core.persistence.api.entity.Schema.class);
        lenient().when(schemaMock.getKey()).thenReturn("myPlainAttr");
        lenient().when(schemaInfoMock.schema()).thenReturn(schemaMock);

        lenient().when(intAttrNameMock.getSchemaInfo()).thenReturn(schemaInfoMock);
        lenient().when(plainAttrGetterMock.apply(any(), any())).thenReturn(null);

        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.String,
                userMock, accountGetterMock, plainAttrGetterMock
        );

        assertTrue(result.values().isEmpty(), "Il risultato deve essere vuoto se il getter non trova nulla");
    }

    @Test
    @DisplayName("TC16: AccountGetter restituisce Null (Dato Sicurezza Non Presente)")
    void testAccountGetterReturnsNull() {
        lenient().when(intAttrNameMock.getField()).thenReturn("username");
        lenient().when(accountGetterMock.apply(any())).thenReturn(null);

        assertThrows(NullPointerException.class, () -> mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.String,
                userMock, accountGetterMock, plainAttrGetterMock
        ), "Il manager propaga correttamente l'NPE se l'account restituito è nullo");
    }

    @Test
    @DisplayName("TC17: PlainAttrGetter è Nullo (Delegato Inesistente)")
    void testNullPlainAttrGetter() {
        lenient().when(intAttrNameMock.getField()).thenReturn("key");
        lenient().when(userMock.getKey()).thenReturn("123");

        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.String,
                userMock, accountGetterMock, null
        );

        assertFalse(result.values().isEmpty(), "Il sistema deve estrarre il dato bypassando il delegato nullo per logiche non-plain");
    }

    @Test
    @DisplayName("TC18: AccountGetter è Nullo (Delegato Inesistente)")
    void testNullAccountGetter() {
        lenient().when(intAttrNameMock.getField()).thenReturn("username");

        assertThrows(NullPointerException.class, () -> mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.String,
                userMock, null, plainAttrGetterMock
        ), "Il passaggio nel blocco username richiede obbligatoriamente il delegato AccountGetter");
    }

    // =========================================================================
    // SEZIONE 5: CONTESTI INCOERENTI E TRASFORMAZIONI
    // =========================================================================

    @Test
    @DisplayName("TC19: Provision Incoerente - Regola per Utente applicata a Gruppo")
    void testInconsistentProvisionContext() {
        lenient().when(provisionMock.getAnyType()).thenReturn("USER");
        lenient().when(intAttrNameMock.getField()).thenReturn("uManager");

        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.String,
                groupMock, accountGetterMock, plainAttrGetterMock
        );

        assertTrue(result.values().isEmpty(), "Il manager deve ignorare i contesti non allineati restituendo zero valori");
    }

    @Test
    @DisplayName("TC20: Item con Trasformazione - Elaborazione post-estrazione")
    void testItemWithTransformation() {
        lenient().when(intAttrNameMock.getField()).thenReturn("key");
        lenient().when(userMock.getKey()).thenReturn("key123");

        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.String,
                userMock, accountGetterMock, plainAttrGetterMock
        );

        assertFalse(result.values().isEmpty(), "Il dato deve essere processato correttamente nonostante l'Item trasformato");
    }

    @Test
    @DisplayName("TC21: Any Corrotto - Istanza instabile o non inizializzata")
    void testCorruptedAnyInstance() {
        Any corruptedAny = mock(Any.class);
        lenient().when(intAttrNameMock.getField()).thenReturn("key");
        lenient().when(corruptedAny.getKey()).thenThrow(new RuntimeException("DB Error"));

        assertThrows(RuntimeException.class, () -> mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.String,
                corruptedAny, accountGetterMock, plainAttrGetterMock
        ), "Un errore critico in fase di lettura del dato deve propagarsi senza essere mascherato");
    }

    @Test
    @DisplayName("TC22: ExternalResource Incoerente (Non Configurata)")
    void testInvalidExternalResource() {
        ExternalResource invalidResource = mock(ExternalResource.class);
        lenient().when(intAttrNameMock.getField()).thenReturn("key");
        lenient().when(userMock.getKey()).thenReturn("key");

        IntValues result = mappingManager.getIntValues(
                invalidResource, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.String,
                userMock, accountGetterMock, plainAttrGetterMock
        );

        assertFalse(result.values().isEmpty(), "La validità della risorsa non impedisce le letture base dell'entità");
    }

    @Test
    @DisplayName("TC23: Entità Utente, Dato Relazionale (Gerarchia)")
    void testUserRelationalDataExtraction() {
        lenient().when(intAttrNameMock.getField()).thenReturn("uManager");
        lenient().when(provisionMock.getAnyType()).thenReturn("USER");
        lenient().when(provisionMock.getMapping()).thenReturn(mock(org.apache.syncope.common.lib.to.Mapping.class));
        lenient().when(userMock.getUManager()).thenReturn(null);

        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.String,
                userMock, accountGetterMock, plainAttrGetterMock
        );

        assertTrue(result.values().isEmpty(), "Un uManager mancante produce correttamente una lista di estrazione vuota");
    }

    @Test
    @DisplayName("TC24: Entità Gruppo, Dato Speciale Binario")
    void testGroupBinaryData() {
        lenient().when(intAttrNameMock.getField()).thenReturn("key");
        lenient().when(groupMock.getKey()).thenReturn("group-bin");

        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.Binary,
                groupMock, accountGetterMock, plainAttrGetterMock
        );

        assertEquals(AttrSchemaType.Binary, result.attrSchemaType(), "Il tipo Binario deve essere rispettato per l'entità Gruppo");
    }

    @Test
    @DisplayName("TC25: Dato Presente ma Vuoto (Stringa vuota)")
    void testDataPresentButEmpty() {
        lenient().when(intAttrNameMock.getField()).thenReturn("key");
        lenient().when(userMock.getKey()).thenReturn("");

        IntValues result = mappingManager.getIntValues(
                resourceMock, provisionMock, itemMock, intAttrNameMock, AttrSchemaType.String,
                userMock, accountGetterMock, plainAttrGetterMock
        );

        assertEquals("", result.values().get(0).getStringValue(), "Una stringa vuota ma presente deve essere mappata letteralmente");
    }
}