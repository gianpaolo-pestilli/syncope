package org.apache.syncope.core.provisioning.java;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeUtil;

import org.apache.syncope.common.lib.to.Item;
import org.apache.syncope.common.lib.to.Provision;
import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.syncope.common.lib.types.AttrSchemaType;
import org.apache.syncope.common.lib.types.SchemaType;
import org.apache.syncope.core.persistence.api.entity.Any;
import org.apache.syncope.core.persistence.api.entity.AnyType;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.PlainAttr;
import org.apache.syncope.core.persistence.api.entity.PlainAttrValue;
import org.apache.syncope.core.persistence.api.entity.PlainSchema;
import org.apache.syncope.core.provisioning.api.AccountGetter;
import org.apache.syncope.core.provisioning.api.PlainAttrGetter;
import org.apache.syncope.core.provisioning.api.IntAttrNameParser;
import org.apache.syncope.core.provisioning.api.IntAttrName;
import org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttr;
import org.apache.syncope.core.persistence.api.dao.ImplementationDAO;
import org.apache.syncope.core.persistence.api.entity.Implementation;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ManualIntegrationDefaultMappingManagerTest {

    @InjectMocks
    private DefaultMappingManager mappingManager;

    @Mock private IntAttrNameParser intAttrNameParser;
    @Mock private IntAttrName intAttrName;
    @Mock private IntAttrName.SchemaInfo schemaInfo;
    @Mock private PlainSchema plainSchema;
    @Mock private ImplementationDAO implementationDAO;

    @Mock private Any any;
    @Mock private AnyType anyType;
    @Mock private ExternalResource resource;
    @Mock private Provision provision;
    @Mock private Item item;

    private Set<Attribute> targetSet;

    @BeforeEach
    public void setUp() throws Exception {
        targetSet = new HashSet<>();

        when(any.getType()).thenReturn(anyType);
        when(anyType.getKind()).thenReturn(AnyTypeKind.USER);

        when(item.getIntAttrName()).thenReturn("campoInterno");
        when(item.getExtAttrName()).thenReturn("campoEsterno");
        when(item.getTransformers()).thenReturn(List.of());

        when(intAttrNameParser.parse(anyString(), any(AnyTypeKind.class))).thenReturn(intAttrName);

        when(intAttrName.getSchemaInfo()).thenReturn(schemaInfo);

        // RIMOSSO: when(intAttrName.getName()).thenReturn("..."); -> INVENTATO

        when(schemaInfo.type()).thenReturn(SchemaType.PLAIN);
        when(schemaInfo.schema()).thenReturn(plainSchema);

        // RIMOSSO: when(schemaInfo.getKey()).thenReturn("..."); -> INVENTATO

        when(plainSchema.getKey()).thenReturn("schemaKey"); // Questo esiste ed è sufficiente!
        when(plainSchema.getType()).thenReturn(AttrSchemaType.String);
    }

    // Costruttore di dati grezzi per simulare il DB
    private PlainAttrGetter createMockAttrGetter(String... values) {
        return (a, schema) -> {
            PlainAttr plainAttr = mock(PlainAttr.class);

            doReturn("schemaKey").when(plainAttr).getSchema();
            doReturn(java.util.Arrays.asList(values)).when(plainAttr).getValuesAsStrings();

            // LA SOLUZIONE DEFINITIVA: Usiamo oggetti REALI invece di Mock!
            // Poiché Syncope esegue una deep-copy (clonePlainAttrValue), i mock
            // perdevano i dati. Passando un'istanza vera, la copia è perfetta.
            List<PlainAttrValue> realValues = new java.util.ArrayList<>();
            for (String val : values) {
                PlainAttrValue realVal = new PlainAttrValue();
                realVal.setStringValue(val);
                realValues.add(realVal);
            }

            // Instradiamo Syncope esattamente come si aspetta
            if (values.length == 1) {
                doReturn(realValues.get(0)).when(plainAttr).getUniqueValue();
                doReturn(realValues).when(plainAttr).getValues();
            } else if (values.length > 1) {
                doReturn(null).when(plainAttr).getUniqueValue();
                doReturn(realValues).when(plainAttr).getValues();
            }

            return plainAttr;
        };
    }

    // --- SCENARI DAL TUO REPORT ---

    @Test
    public void IT01_DatoSenzaTrasformazioni_SetVuoto() {
        PlainAttrGetter attrGetter = createMockAttrGetter("valoreSemplice");

        PreparedAttr pacchetto = mappingManager.prepareAttr(
                resource, provision, item, any, "pwd", AccountGetter.DEFAULT, AccountGetter.DEFAULT, attrGetter);

        DefaultMappingManager.processPreparedAttr(pacchetto, targetSet);

        Attribute risultato = AttributeUtil.find("campoEsterno", targetSet);
        assertTrue(targetSet.size() == 1 && risultato != null && "valoreSemplice".equals(risultato.getValue().get(0)),
                "Il flusso deve estrarre il dato e caricarlo nel Set vuoto senza alterazioni");
    }

    @Test
    public void IT02_DatoConTrasformazioni_SetVuoto() {
        PlainAttrGetter attrGetter = createMockAttrGetter("valoreDaTrasformare");

        when(item.getTransformers()).thenReturn(List.of("transformerId"));
        Implementation mockImpl = mock(Implementation.class);
        doReturn(Optional.of(mockImpl)).when(implementationDAO).findById("transformerId");

        PreparedAttr pacchetto = mappingManager.prepareAttr(
                resource, provision, item, any, "pwd", AccountGetter.DEFAULT, AccountGetter.DEFAULT, attrGetter);

        DefaultMappingManager.processPreparedAttr(pacchetto, targetSet);

        assertTrue(targetSet.size() == 1 && AttributeUtil.find("campoEsterno", targetSet) != null,
                "La pipeline deve completare il flusso anche quando il dato viene sottoposto al layer di trasformazione");
    }

    @Test
    public void IT03_DatoEsisteGia_Omonimi_Merge() {
        targetSet.add(AttributeBuilder.build("campoEsterno", "vecchioValore"));
        PlainAttrGetter attrGetter = createMockAttrGetter("nuovoValore");

        PreparedAttr pacchetto = mappingManager.prepareAttr(
                resource, provision, item, any, "pwd", AccountGetter.DEFAULT, AccountGetter.DEFAULT, attrGetter);

        DefaultMappingManager.processPreparedAttr(pacchetto, targetSet);

        Attribute risultato = AttributeUtil.find("campoEsterno", targetSet);
        assertTrue(targetSet.size() == 1 && risultato.getValue().size() == 2
                        && risultato.getValue().containsAll(Set.of("vecchioValore", "nuovoValore")),
                "Se l'attributo è già presente, la pipeline deve fondere i valori estratti con quelli preesistenti");
    }

    @Test
    public void IT04_DatoConChiaveDiConnessione_EstraeIndirizzo() {
        when(item.isConnObjectKey()).thenReturn(true);
        when(item.getExtAttrName()).thenReturn("__NAME__");
        PlainAttrGetter attrGetter = createMockAttrGetter("id_utente_123");

        PreparedAttr pacchetto = mappingManager.prepareAttr(
                resource, provision, item, any, "pwd", AccountGetter.DEFAULT, AccountGetter.DEFAULT, attrGetter);

        Optional<String> linkEstratto = DefaultMappingManager.processPreparedAttr(pacchetto, targetSet);

        assertTrue(targetSet.isEmpty() && linkEstratto.isPresent() && "id_utente_123".equals(linkEstratto.get()),
                "Se l'attributo è una chiave di connessione, il dato non entra nel Set ma diviene l'indirizzo di ritorno");
    }

    @Test
    public void IT05_DatoMultivalore_SetVuoto() {
        PlainAttrGetter attrGetter = createMockAttrGetter("ruolo1", "ruolo2", "ruolo3");

        PreparedAttr pacchetto = mappingManager.prepareAttr(
                resource, provision, item, any, "pwd", AccountGetter.DEFAULT, AccountGetter.DEFAULT, attrGetter);

        DefaultMappingManager.processPreparedAttr(pacchetto, targetSet);

        Attribute risultato = AttributeUtil.find("campoEsterno", targetSet);
        assertTrue(targetSet.size() == 1 && risultato.getValue().size() == 3,
                "La pipeline deve trasportare e caricare nativamente strutture dati multivalore senza perdite");
    }

    @Test
    public void IT06_DatoPayloadNull_Tolleranza() {
        PlainAttrGetter attrGetter = (a, schema) -> null;

        PreparedAttr pacchetto = mappingManager.prepareAttr(
                resource, provision, item, any, "pwd", AccountGetter.DEFAULT, AccountGetter.DEFAULT, attrGetter);

        DefaultMappingManager.processPreparedAttr(pacchetto, targetSet);

        Attribute risultato = AttributeUtil.find("campoEsterno", targetSet);
        assertTrue(targetSet.size() == 1 && (risultato.getValue() == null || risultato.getValue().isEmpty()),
                "Un flusso dati nullo in partenza deve attraversare l'intera pipeline generando un attributo vuoto");
    }

    @Test
    public void IT07_StatoDestinazione_SetNull() {
        PlainAttrGetter attrGetter = createMockAttrGetter("valore");

        PreparedAttr pacchetto = mappingManager.prepareAttr(
                resource, provision, item, any, "pwd", AccountGetter.DEFAULT, AccountGetter.DEFAULT, attrGetter);

        assertThrows(NullPointerException.class, () ->
                        DefaultMappingManager.processPreparedAttr(pacchetto, null),
                "Se lo stadio di caricamento riceve un Set nullo, la pipeline deve interrompersi lanciando eccezione");
    }
}