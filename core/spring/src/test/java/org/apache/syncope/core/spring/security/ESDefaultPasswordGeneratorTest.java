package org.apache.syncope.core.spring.security;

import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.Implementation;
import org.apache.syncope.core.persistence.api.entity.policy.PasswordPolicy;
import org.apache.syncope.core.provisioning.api.rules.PasswordRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ESDefaultPasswordGeneratorTest {

    private DefaultPasswordGenerator generator;

    @BeforeEach
    void setUp() {
        // Inizializziamo il generatore prima di ogni test per mantenere l'isolamento
        generator = new DefaultPasswordGenerator();
    }

    @Test
    void test0_GetPasswordRulesWithNullRulesThrowsNPE() {
        PasswordPolicy policy = mock(PasswordPolicy.class);
        // MODIFICATO: Risoluzione del problema dei Generics
        doReturn(null).when(policy).getRules();

        assertThrows(NullPointerException.class, () -> {
            generator.getPasswordRules(policy);
        });
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void test1_GenerateWithInvalidRuleImplementationThrowsClassCastException() {
        PasswordPolicy policy1 = mock(PasswordPolicy.class);

        // Simuliamo l'inserimento di un oggetto sbagliato (il generatore stesso) nella lista delle regole
        List rawList = new ArrayList();
        rawList.add(generator);
        // MODIFICATO: Bypass dei controlli sui tipi di Mockito
        doReturn(rawList).when(policy1).getRules();

        ExternalResource resource = mock(ExternalResource.class);
        // MODIFICATO: Restituzione immediata della policy "avvelenata"
        when(resource.getPasswordPolicy()).thenReturn(policy1);

        assertThrows(ClassCastException.class, () -> {
            generator.generate(resource, new ArrayList<>());
        });
    }

    @Test
    void test2_GenerateWithNullPolicyListThrowsNPE() {
        assertThrows(NullPointerException.class, () -> {
            generator.generate((List<PasswordPolicy>) null);
        });
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void test3_GenerateWithInvalidPolicyTypeInListThrowsClassCastException() {
        // Simuliamo una lista di PasswordPolicy che contiene un tipo errato
        List rawList = new ArrayList();
        rawList.add(generator);

        assertThrows(ClassCastException.class, () -> {
            generator.generate((List<PasswordPolicy>) rawList);
        });
    }

    @Test
    void test4_GetPasswordRulesFiltersOutNullImplementations() {
        // EvoSuite creava uno Stack di 3502 elementi nulli. Usiamo nCopies per replicarlo in modo pulito.
        List<Implementation> nullImplementations = Collections.nCopies(3502, null);

        PasswordPolicy policy = mock(PasswordPolicy.class);
        // MODIFICATO: Restituzione della lista effettiva, non di null
        doReturn(nullImplementations).when(policy).getRules();

        List<PasswordRule> result = generator.getPasswordRules(policy);
        assertEquals(0, result.size(), "La lista risultante dovrebbe essere vuota ignorando i null");
    }

    @Test
    void test5_GenerateWithResourceReturningNullRulesThrowsNPE() {
        PasswordPolicy policy1 = mock(PasswordPolicy.class);
        // MODIFICATO: Risoluzione del problema dei Generics
        doReturn(null).when(policy1).getRules();

        ExternalResource resource = mock(ExternalResource.class);
        // MODIFICATO: Restituzione immediata della policy "avvelenata"
        when(resource.getPasswordPolicy()).thenReturn(policy1);

        assertThrows(NullPointerException.class, () -> {
            generator.generate(resource, new ArrayList<>());
        });
    }

    @Test
    void test6_GenerateWithNullPasswordPolicyReturnsDefault() {
        ExternalResource resource = mock(ExternalResource.class);
        when(resource.getPasswordPolicy()).thenReturn(null);

        String result = generator.generate(resource, new ArrayList<>());

        // MODIFICATO: Verifiche per logica randomica invece di stringa statica
        assertNotNull(result, "La password generata non deve essere nulla");
        assertTrue(result.length() >= 8, "La password di default deve essere di almeno 8 caratteri");
    }

    @Test
    void test7_GenerateWithEmptyPolicyListReturnsDefault() {
        List<PasswordPolicy> emptyPolicies = new ArrayList<>();

        String result = generator.generate(emptyPolicies);

        // MODIFICATO: Verifiche per logica randomica invece di stringa statica
        assertNotNull(result, "La password generata non deve essere nulla");
        assertTrue(result.length() >= 8, "La password di default deve essere di almeno 8 caratteri");
    }
}