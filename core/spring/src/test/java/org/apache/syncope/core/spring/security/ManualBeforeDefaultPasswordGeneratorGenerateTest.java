package org.apache.syncope.core.spring.security;

import org.apache.syncope.common.lib.policy.DefaultPasswordRuleConf;
import org.apache.syncope.core.persistence.api.entity.Implementation;
import org.apache.syncope.core.persistence.api.entity.policy.PasswordPolicy;
import org.apache.syncope.core.provisioning.api.rules.PasswordRule;
import org.apache.syncope.core.spring.implementation.ImplementationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


public class ManualBeforeDefaultPasswordGeneratorGenerateTest {

    private DefaultPasswordGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new DefaultPasswordGenerator();
    }


    private Implementation createMockImpl(String key) {
        Implementation impl = mock(Implementation.class);
        lenient().when(impl.getKey()).thenReturn(key);
        return impl;
    }


    @Test
    void testGenerate_Input_NullList() {
        assertThrows(NullPointerException.class, () -> generator.generate((List<PasswordPolicy>) null));
    }

    @Test
    void testGenerate_Input_EmptyList() {

        String result = generator.generate(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.length() >= 8, "La password generata da una lista vuota deve usare i valori minimi di default (8)");
    }


    @Test
    void testGenerate_PolicyState_NullPolicy() {
        List<PasswordPolicy> policies = new ArrayList<>();
        policies.add(null);
        assertThrows(NullPointerException.class, () -> generator.generate(policies));
    }

    @Test
    void testGenerate_PolicyState_NoRulesInside() {
        PasswordPolicy policy = mock(PasswordPolicy.class);
        doReturn(Collections.emptyList()).when(policy).getRules();

        String result = generator.generate(List.of(policy));

        assertNotNull(result);
        assertTrue(result.length() >= 8, "Nessuna regola comporta l'applicazione dei default (minimo 8)");
    }


    @Test
    void testGenerate_Rules_OneElement_OneValidRule() {

        Implementation impl = createMockImpl("rule1");
        PasswordPolicy policy = mock(PasswordPolicy.class);
        doReturn(List.of(impl)).when(policy).getRules();

        DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
        conf.setMinLength(12);

        PasswordRule rule = mock(PasswordRule.class);
        when(rule.getConf()).thenReturn(conf);

        try (MockedStatic<ImplementationManager> mockedManager = mockStatic(ImplementationManager.class)) {
            mockedManager.when(() -> ImplementationManager.buildPasswordRule(eq(impl), any(), any()))
                    .thenReturn(Optional.of(rule));

            String result = generator.generate(List.of(policy));

            assertNotNull(result);
            assertTrue(result.length() >= 12, "La password deve rispettare l'unica regola fornita (lunghezza minima 12)");
        }
    }

    @Test
    void testGenerate_Rules_OneElement_TwoValidRules() {

        Implementation impl1 = createMockImpl("rule1");
        Implementation impl2 = createMockImpl("rule2");
        PasswordPolicy policy = mock(PasswordPolicy.class);
        doReturn(List.of(impl1, impl2)).when(policy).getRules();

        DefaultPasswordRuleConf conf1 = new DefaultPasswordRuleConf();
        conf1.setMinLength(10);
        PasswordRule rule1 = mock(PasswordRule.class);
        when(rule1.getConf()).thenReturn(conf1);

        DefaultPasswordRuleConf conf2 = new DefaultPasswordRuleConf();
        conf2.setMinLength(15);
        PasswordRule rule2 = mock(PasswordRule.class);
        when(rule2.getConf()).thenReturn(conf2);

        try (MockedStatic<ImplementationManager> mockedManager = mockStatic(ImplementationManager.class)) {
            mockedManager.when(() -> ImplementationManager.buildPasswordRule(eq(impl1), any(), any()))
                    .thenReturn(Optional.of(rule1));
            mockedManager.when(() -> ImplementationManager.buildPasswordRule(eq(impl2), any(), any()))
                    .thenReturn(Optional.of(rule2));

            String result = generator.generate(List.of(policy));

            assertNotNull(result);
            assertTrue(result.length() >= 15, "Il merge deve preservare la regola più restrittiva (min 15)");
        }
    }

    @Test
    void testGenerate_Rules_TwoElements_ValidRules() {

        Implementation impl1 = createMockImpl("rule1");
        PasswordPolicy policy1 = mock(PasswordPolicy.class);
        doReturn(List.of(impl1)).when(policy1).getRules();

        Implementation impl2 = createMockImpl("rule2");
        PasswordPolicy policy2 = mock(PasswordPolicy.class);
        doReturn(List.of(impl2)).when(policy2).getRules();

        DefaultPasswordRuleConf conf1 = new DefaultPasswordRuleConf();
        conf1.setMinLength(10);
        PasswordRule rule1 = mock(PasswordRule.class);
        when(rule1.getConf()).thenReturn(conf1);

        DefaultPasswordRuleConf conf2 = new DefaultPasswordRuleConf();
        conf2.setMinLength(18);
        PasswordRule rule2 = mock(PasswordRule.class);
        when(rule2.getConf()).thenReturn(conf2);

        try (MockedStatic<ImplementationManager> mockedManager = mockStatic(ImplementationManager.class)) {
            mockedManager.when(() -> ImplementationManager.buildPasswordRule(eq(impl1), any(), any()))
                    .thenReturn(Optional.of(rule1));
            mockedManager.when(() -> ImplementationManager.buildPasswordRule(eq(impl2), any(), any()))
                    .thenReturn(Optional.of(rule2));

            String result = generator.generate(List.of(policy1, policy2));

            assertNotNull(result);
            assertTrue(result.length() >= 18, "Merge di policy multiple: vince il minimo più alto (18)");
        }
    }

    @Test
    void testGenerate_Rules_OneElement_MalformedRule() {

        Implementation impl1 = createMockImpl("valid_rule");
        Implementation impl2 = createMockImpl("malformed_rule");

        PasswordPolicy policy = mock(PasswordPolicy.class);
        doReturn(List.of(impl1, impl2)).when(policy).getRules();


        DefaultPasswordRuleConf conf1 = new DefaultPasswordRuleConf();
        conf1.setMinLength(14);
        PasswordRule rule1 = mock(PasswordRule.class);
        when(rule1.getConf()).thenReturn(conf1);

        PasswordRule rule2 = mock(PasswordRule.class);
        when(rule2.getConf()).thenReturn(null); // Simula una configurazione errata/esterna

        try (MockedStatic<ImplementationManager> mockedManager = mockStatic(ImplementationManager.class)) {
            mockedManager.when(() -> ImplementationManager.buildPasswordRule(eq(impl1), any(), any()))
                    .thenReturn(Optional.of(rule1));
            mockedManager.when(() -> ImplementationManager.buildPasswordRule(eq(impl2), any(), any()))
                    .thenReturn(Optional.of(rule2));

            String result = generator.generate(List.of(policy));

            assertNotNull(result);
            assertTrue(result.length() >= 14, "La regola malformata deve essere ignorata e la password deve seguire la regola lecita");
        }
    }
}