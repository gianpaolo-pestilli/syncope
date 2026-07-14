package org.apache.syncope.core.spring.security;

import org.apache.syncope.core.persistence.api.entity.Implementation;
import org.apache.syncope.core.persistence.api.entity.policy.PasswordPolicy;
import org.apache.syncope.core.provisioning.api.rules.PasswordRule;
import org.apache.syncope.core.spring.implementation.ImplementationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ManualBeforeDefaultPasswordGeneratorGetTest {

    private DefaultPasswordGenerator generator;
    private Implementation impl1;
    private Implementation impl2;

    @BeforeEach
    void setUp() {
        generator = new DefaultPasswordGenerator();
        impl1 = mock(Implementation.class);
        impl2 = mock(Implementation.class);

        when(impl1.getKey()).thenReturn("rule1");
        when(impl2.getKey()).thenReturn("rule2");
    }

    @Test
    void testGetPasswordRules_NullPolicy() {
        assertThrows(NullPointerException.class, () -> generator.getPasswordRules(null));
    }

    @Test
    void testGetPasswordRules_ZeroRules() {
        PasswordPolicy policy = mock(PasswordPolicy.class);
        doReturn(Collections.emptyList()).when(policy).getRules();

        List<PasswordRule> result = generator.getPasswordRules(policy);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPasswordRules_OneRule_Valid() {
        PasswordPolicy policy = mock(PasswordPolicy.class);
        doReturn(List.of(impl1)).when(policy).getRules();

        try (MockedStatic<ImplementationManager> mockedManager = mockStatic(ImplementationManager.class)) {
            mockedManager.when(() -> ImplementationManager.buildPasswordRule(eq(impl1), any(), any()))
                    .thenReturn(Optional.of(mock(PasswordRule.class)));

            List<PasswordRule> result = generator.getPasswordRules(policy);
            assertEquals(1, result.size());
        }
    }

    @Test
    void testGetPasswordRules_OneRule_Malformed() {
        PasswordPolicy policy = mock(PasswordPolicy.class);
        doReturn(List.of(impl1)).when(policy).getRules();

        try (MockedStatic<ImplementationManager> mockedManager = mockStatic(ImplementationManager.class)) {
            mockedManager.when(() -> ImplementationManager.buildPasswordRule(eq(impl1), any(), any()))
                    .thenThrow(new RuntimeException("Error"));

            List<PasswordRule> result = generator.getPasswordRules(policy);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testGetPasswordRules_MultipleRules_AllValid() {
        PasswordPolicy policy = mock(PasswordPolicy.class);
        doReturn(List.of(impl1, impl2)).when(policy).getRules();

        try (MockedStatic<ImplementationManager> mockedManager = mockStatic(ImplementationManager.class)) {
            mockedManager.when(() -> ImplementationManager.buildPasswordRule(any(), any(), any()))
                    .thenReturn(Optional.of(mock(PasswordRule.class)));

            List<PasswordRule> result = generator.getPasswordRules(policy);
            assertEquals(2, result.size());
        }
    }

    @Test
    void testGetPasswordRules_MultipleRules_AllMalformed() {
        PasswordPolicy policy = mock(PasswordPolicy.class);
        doReturn(List.of(impl1, impl2)).when(policy).getRules();

        try (MockedStatic<ImplementationManager> mockedManager = mockStatic(ImplementationManager.class)) {
            mockedManager.when(() -> ImplementationManager.buildPasswordRule(any(), any(), any()))
                    .thenThrow(new RuntimeException("Error"));

            List<PasswordRule> result = generator.getPasswordRules(policy);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testGetPasswordRules_MultipleRules_Mixed1() {
        PasswordPolicy policy = mock(PasswordPolicy.class);
        doReturn(List.of(impl1, impl2)).when(policy).getRules();

        try (MockedStatic<ImplementationManager> mockedManager = mockStatic(ImplementationManager.class)) {
            mockedManager.when(() -> ImplementationManager.buildPasswordRule(eq(impl1), any(), any()))
                    .thenReturn(Optional.of(mock(PasswordRule.class)));
            mockedManager.when(() -> ImplementationManager.buildPasswordRule(eq(impl2), any(), any()))
                    .thenThrow(new RuntimeException("Error"));

            List<PasswordRule> result = generator.getPasswordRules(policy);
            assertEquals(1, result.size());
        }
    }

    @Test
    void testGetPasswordRules_MultipleRules_Mixed2() {
        PasswordPolicy policy = mock(PasswordPolicy.class);
        doReturn(List.of(impl2, impl1)).when(policy).getRules();

        try (MockedStatic<ImplementationManager> mockedManager = mockStatic(ImplementationManager.class)) {
            mockedManager.when(() -> ImplementationManager.buildPasswordRule(eq(impl1), any(), any()))
                    .thenReturn(Optional.of(mock(PasswordRule.class)));
            mockedManager.when(() -> ImplementationManager.buildPasswordRule(eq(impl2), any(), any()))
                    .thenThrow(new RuntimeException("Error"));

            List<PasswordRule> result = generator.getPasswordRules(policy);
            assertEquals(1, result.size());
        }
    }
}