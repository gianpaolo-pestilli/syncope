package org.apache.syncope.core.provisioning.java;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.apache.syncope.core.provisioning.api.MappingManager.PreparedAttr;

public class ManualBeforeDefaultMappingManagerProcessAttrTest {

    private Set<Attribute> attributesSet;
    private PreparedAttr preparedAttrMock;

    @BeforeEach
    public void setUp() {
        attributesSet = new HashSet<>();
        preparedAttrMock = mock(PreparedAttr.class);
    }

    // --- GRUPPO 1: INPUT PREPAREDATTR (5 partizioni) ---

    @Test
    public void TC01_PrepAttr_Null() {
        Optional<String> result = DefaultMappingManager.processPreparedAttr(null, attributesSet);

        assertTrue(result.isEmpty() && attributesSet.isEmpty(),
                "Se PreparedAttr è null, deve ritornare Optional vuoto e non modificare il Set");
    }

    @Test
    public void TC02_PrepAttr_NonValido() {
        when(preparedAttrMock.connObjectLink()).thenReturn(null);
        when(preparedAttrMock.attribute()).thenReturn(null);

        Optional<String> result = DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        assertTrue(result.isEmpty() && attributesSet.isEmpty(),
                "Se PreparedAttr ha campi nulli, deve ritornare Optional vuoto e non modificare il Set");
    }

    @Test
    public void TC03_PrepAttr_PayloadSenzaIndirizzo() {
        Attribute attr = AttributeBuilder.build("email", "test@test.com");
        when(preparedAttrMock.connObjectLink()).thenReturn(null);
        when(preparedAttrMock.attribute()).thenReturn(attr);

        Optional<String> result = DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        assertTrue(result.isEmpty() && attributesSet.size() == 1,
                "Senza indirizzo, ritorna vuoto ma il payload deve essere aggiunto al set");
    }

    @Test
    public void TC04_PrepAttr_IndirizzoSenzaPayload() {
        when(preparedAttrMock.connObjectLink()).thenReturn("http://link.com");
        when(preparedAttrMock.attribute()).thenReturn(null);

        Optional<String> result = DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        assertTrue(result.isPresent() && "http://link.com".equals(result.get()) && attributesSet.isEmpty(),
                "Senza payload, ritorna l'indirizzo ma il set rimane vuoto");
    }

    @Test
    public void TC05_PrepAttr_IndirizzoEPayload() {
        Attribute attr = AttributeBuilder.build("email", "test@test.com");
        when(preparedAttrMock.connObjectLink()).thenReturn("http://link.com");
        when(preparedAttrMock.attribute()).thenReturn(attr);

        Optional<String> result = DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        assertTrue(result.isPresent() && "http://link.com".equals(result.get()) && attributesSet.size() == 1,
                "Ritorna l'indirizzo e aggiunge il payload al set");
    }

    // --- GRUPPO 2: INPUT SET DI ATTRIBUTE E COLLISIONI (6 partizioni) ---

    @Test
    public void TC06_Set_Null() {
        Attribute attr = AttributeBuilder.build("email", "test@test.com");
        when(preparedAttrMock.attribute()).thenReturn(attr);

        assertThrows(NullPointerException.class, () ->
                        DefaultMappingManager.processPreparedAttr(preparedAttrMock, null),
                "Ci si aspetta NullPointerException per l'uso di Set null senza controlli preventivi");
    }

    @Test
    public void TC07_Set_Vuoto() {
        Attribute attr = AttributeBuilder.build("username", "user1");
        when(preparedAttrMock.attribute()).thenReturn(attr);

        DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        assertTrue(attributesSet.size() == 1 && "user1".equals(AttributeUtil.find("username", attributesSet).getValue().get(0)),
                "L'attributo deve essere inserito in modo pulito nel Set vuoto");
    }

    @Test
    public void TC08_Set_UnElemento_Omonimo() {
        attributesSet.add(AttributeBuilder.build("group", "admin"));
        Attribute payload = AttributeBuilder.build("group", "user");
        when(preparedAttrMock.attribute()).thenReturn(payload);

        DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        Attribute mergedAttr = AttributeUtil.find("group", attributesSet);
        assertTrue(attributesSet.size() == 1 && mergedAttr != null && mergedAttr.getValue().contains("admin") && mergedAttr.getValue().contains("user"),
                "Deve esserci stato un merge, non un'aggiunta di un nuovo attributo separato");
    }

    @Test
    public void TC09_Set_UnElemento_NonOmonimo() {
        attributesSet.add(AttributeBuilder.build("email", "test@test.com"));
        Attribute payload = AttributeBuilder.build("group", "user");
        when(preparedAttrMock.attribute()).thenReturn(payload);

        DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        assertTrue(attributesSet.size() == 2 && AttributeUtil.find("email", attributesSet) != null && AttributeUtil.find("group", attributesSet) != null,
                "I due attributi sono diversi, il set deve crescere senza unire i dati");
    }

    @Test
    public void TC10_Set_DueElementi_OmonimoPresente() {
        attributesSet.add(AttributeBuilder.build("email", "test@test.com"));
        attributesSet.add(AttributeBuilder.build("group", "admin"));
        Attribute payload = AttributeBuilder.build("group", "user");
        when(preparedAttrMock.attribute()).thenReturn(payload);

        DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        Attribute merged = AttributeUtil.find("group", attributesSet);
        assertTrue(attributesSet.size() == 2 && merged != null && merged.getValue().contains("user") && AttributeUtil.find("email", attributesSet) != null,
                "Il set non cresce di dimensione e il target omonimo riceve il merge corretto");
    }

    @Test
    public void TC11_Set_DueElementi_OmonimoNonPresente() {
        attributesSet.add(AttributeBuilder.build("email", "test@test.com"));
        attributesSet.add(AttributeBuilder.build("group", "admin"));
        Attribute payload = AttributeBuilder.build("role", "manager");
        when(preparedAttrMock.attribute()).thenReturn(payload);

        DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        assertTrue(attributesSet.size() == 3 && AttributeUtil.find("role", attributesSet) != null,
                "Nuovo attributo deve essere aggiunto agli altri due preservandoli");
    }

    // --- GRUPPO 3: STATO DI ATTRIBUTE E MERGE DEI VALORI (Partizioni di valore) ---

    @Test
    public void TC12_AttrState_EntrambiSingoloValore() {
        attributesSet.add(AttributeBuilder.build("role", "user"));
        when(preparedAttrMock.attribute()).thenReturn(AttributeBuilder.build("role", "admin"));

        DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        assertTrue(AttributeUtil.find("role", attributesSet).getValue().size() == 2,
                "Due attributi a valore singolo devono unirsi in uno multivalore da 2 elementi");
    }

    @Test
    public void TC13_AttrState_SetSingolo_PayloadMultivalore() {
        attributesSet.add(AttributeBuilder.build("role", "user"));
        when(preparedAttrMock.attribute()).thenReturn(AttributeBuilder.build("role", "admin", "super"));

        DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        Attribute merged = AttributeUtil.find("role", attributesSet);
        assertTrue(merged != null && merged.getValue().size() == 3 && merged.getValue().containsAll(Set.of("user", "admin", "super")),
                "L'attributo unito deve contenere tutti e 3 gli elementi combinati");
    }

    @Test
    public void TC14_AttrState_SetMultivalore_PayloadSingolo() {
        attributesSet.add(AttributeBuilder.build("role", "user", "guest"));
        when(preparedAttrMock.attribute()).thenReturn(AttributeBuilder.build("role", "admin"));

        DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        assertTrue(AttributeUtil.find("role", attributesSet).getValue().size() == 3,
                "Il risultato dell'unione deve essere un multivalore da 3 elementi");
    }

    @Test
    public void TC15_AttrState_EntrambiMultivalore() {
        attributesSet.add(AttributeBuilder.build("role", "user", "guest"));
        when(preparedAttrMock.attribute()).thenReturn(AttributeBuilder.build("role", "admin", "super"));

        DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        assertTrue(AttributeUtil.find("role", attributesSet).getValue().size() == 4,
                "Unione totale di entrambi i set multivalore");
    }

    @Test
    public void TC16_AttrState_SetVuoto_PayloadVuoto() {
        attributesSet.add(AttributeBuilder.build("role"));
        when(preparedAttrMock.attribute()).thenReturn(AttributeBuilder.build("role"));

        DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        assertTrue(AttributeUtil.find("role", attributesSet).getValue().isEmpty(),
                "L'unione di due attributi omonimi senza valori genera un attributo vuoto");
    }

    @Test
    public void TC17_AttrState_SetConValori_PayloadVuoto() {
        attributesSet.add(AttributeBuilder.build("role", "admin"));
        when(preparedAttrMock.attribute()).thenReturn(AttributeBuilder.build("role"));

        DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        Attribute merged = AttributeUtil.find("role", attributesSet);
        assertTrue(merged != null && merged.getValue().size() == 1 && merged.getValue().contains("admin"),
                "Il set originale deve rimanere intatto senza perdere i dati correnti");
    }

    @Test
    public void TC18_AttrState_SetVuoto_PayloadConValori() {
        attributesSet.add(AttributeBuilder.build("role"));
        when(preparedAttrMock.attribute()).thenReturn(AttributeBuilder.build("role", "admin"));

        DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        Attribute merged = AttributeUtil.find("role", attributesSet);
        assertTrue(merged != null && merged.getValue().size() == 1 && merged.getValue().contains("admin"),
                "Il nuovo payload sovrascrive lo stato vuoto del set preesistente");
    }

    @Test
    public void TC19_AttrState_MergeDuplicati() {
        attributesSet.add(AttributeBuilder.build("role", "admin"));
        when(preparedAttrMock.attribute()).thenReturn(AttributeBuilder.build("role", "admin"));

        DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        assertTrue(AttributeUtil.find("role", attributesSet).getValue().size() == 1,
                "I valori duplicati devono collassare in un solo elemento a causa dell'uso di HashSet interno");
    }

    @Test
    public void TC20_AttrState_SetNullaOmonimia_MergeEvitato() {
        attributesSet.add(AttributeBuilder.build("email", "test"));
        Attribute payload = mock(Attribute.class);
        when(payload.getName()).thenReturn("email");
        when(payload.getValue()).thenReturn(null);

        when(preparedAttrMock.attribute()).thenReturn(payload);

        DefaultMappingManager.processPreparedAttr(preparedAttrMock, attributesSet);

        assertTrue(AttributeUtil.find("email", attributesSet).getValue().size() == 1,
                "Il metodo deve proteggere l'inserimento di payload la cui lista interna di valori è null");
    }
}