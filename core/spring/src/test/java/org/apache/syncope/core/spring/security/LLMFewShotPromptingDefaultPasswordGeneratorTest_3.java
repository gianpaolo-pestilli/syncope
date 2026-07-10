package org.apache.syncope.core.spring.security;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.syncope.common.lib.policy.DefaultPasswordRuleConf;
import org.apache.syncope.common.lib.policy.PasswordRuleConf;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.Realm;
import org.apache.syncope.core.persistence.api.entity.policy.PasswordPolicy;
import org.apache.syncope.core.provisioning.api.rules.PasswordRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

    @ExtendWith(MockitoExtension.class)
    class LLMFewShotPromptingDefaultPasswordGeneratorTest_3 {

        private static class TestableDefaultPasswordGenerator extends DefaultPasswordGenerator {

            private final Map<PasswordPolicy, List<PasswordRule>> rulesByPolicy = new HashMap<>();

            void register(final PasswordPolicy policy, final PasswordRule... rules) {
                rulesByPolicy.put(policy, Arrays.asList(rules));
            }

            @Override
            protected List<PasswordRule> getPasswordRules(final PasswordPolicy policy) {
                return rulesByPolicy.getOrDefault(policy, Collections.emptyList());
            }
        }

        private static class CapturingDefaultPasswordGenerator extends DefaultPasswordGenerator {

            private List<PasswordPolicy> capturedPolicies;

            @Override
            public String generate(final List<PasswordPolicy> policies) {
                this.capturedPolicies = policies;
                return "captured-password";
            }

            List<PasswordPolicy> getCapturedPolicies() {
                return capturedPolicies;
            }
        }

        private static PasswordRule passwordRuleWithConf(final PasswordRuleConf conf) {
            PasswordRule rule = mock(PasswordRule.class);
            when(rule.getConf()).thenReturn(conf);
            return rule;
        }

        private static long countUppercase(final String value) {
            return value.chars().filter(Character::isUpperCase).count();
        }

        private static long countLowercase(final String value) {
            return value.chars().filter(Character::isLowerCase).count();
        }

        private static long countDigits(final String value) {
            return value.chars().filter(Character::isDigit).count();
        }

        private static long countSpecials(final String value) {
            return value.chars().filter(ch -> !Character.isLetterOrDigit(ch)).count();
        }

        @Test
        void testGenerate_WithSingleDefaultPasswordPolicy() {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(8);
            conf.setMaxLength(16);

            PasswordPolicy policy = mock(PasswordPolicy.class);
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.register(policy, passwordRuleWithConf(conf));

            String password = generator.generate(Collections.singletonList(policy));

            boolean condition = password != null && password.length() >= 8 && password.length() <= 16;
            assertTrue(condition);
        }

        @Test
        void testGenerate_WithExactLengthPolicy() {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(12);
            conf.setMaxLength(12);

            DefaultPasswordGenerator generator = new DefaultPasswordGenerator();
            String password = generator.generate(conf);

            assertNotNull(password);
            assertEquals(12, password.length());
        }

        @Test
        void testGenerate_WithCharacterRequirements() {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(12);
            conf.setMaxLength(20);
            conf.setUppercase(2);
            conf.setLowercase(2);
            conf.setDigit(2);
            conf.setSpecial(2);

            DefaultPasswordGenerator generator = new DefaultPasswordGenerator();
            String password = generator.generate(conf);

            assertNotNull(password);
            assertTrue(password.length() >= 12 && password.length() <= 20);
            assertTrue(countUppercase(password) >= 2);
            assertTrue(countLowercase(password) >= 2);
            assertTrue(countDigits(password) >= 2);
            assertTrue(countSpecials(password) >= 2);
        }

        @Test
        void testGenerate_WithDigitOnlyRequirement() {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(10);
            conf.setMaxLength(16);
            conf.setDigit(4);

            DefaultPasswordGenerator generator = new DefaultPasswordGenerator();
            String password = generator.generate(conf);

            assertNotNull(password);
            assertTrue(password.length() >= 10 && password.length() <= 16);
            assertTrue(countDigits(password) >= 4);
        }

        @Test
        void testGenerate_WithUppercaseOnlyRequirement() {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(10);
            conf.setMaxLength(16);
            conf.setUppercase(3);

            DefaultPasswordGenerator generator = new DefaultPasswordGenerator();
            String password = generator.generate(conf);

            assertNotNull(password);
            assertTrue(password.length() >= 10 && password.length() <= 16);
            assertTrue(countUppercase(password) >= 3);
        }

        @Test
        void testGenerate_WithEmptyPoliciesUsesDefaultBounds() {
            DefaultPasswordGenerator generator = new DefaultPasswordGenerator();

            String password = generator.generate(Collections.emptyList());

            assertNotNull(password);
            assertTrue(password.length() >= 8 && password.length() <= 64);
        }

        @Test
        void testGenerate_IgnoresNonDefaultPasswordRuleConf() {
            PasswordRuleConf unsupportedConf = mock(PasswordRuleConf.class);
            PasswordPolicy policy = mock(PasswordPolicy.class);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.register(policy, passwordRuleWithConf(unsupportedConf));

            String password = generator.generate(Collections.singletonList(policy));

            assertNotNull(password);
            assertTrue(password.length() >= 8 && password.length() <= 64);
        }

        @Test
        void testGenerate_WithMultiplePoliciesMergesConstraints() {
            DefaultPasswordRuleConf conf1 = new DefaultPasswordRuleConf();
            conf1.setMinLength(8);
            conf1.setMaxLength(32);
            conf1.setDigit(1);

            DefaultPasswordRuleConf conf2 = new DefaultPasswordRuleConf();
            conf2.setMinLength(14);
            conf2.setMaxLength(18);
            conf2.setUppercase(2);

            PasswordPolicy policy1 = mock(PasswordPolicy.class);
            PasswordPolicy policy2 = mock(PasswordPolicy.class);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.register(policy1, passwordRuleWithConf(conf1));
            generator.register(policy2, passwordRuleWithConf(conf2));

            String password = generator.generate(Arrays.asList(policy1, policy2));

            assertNotNull(password);
            assertTrue(password.length() >= 14 && password.length() <= 18);
            assertTrue(countDigits(password) >= 1);
            assertTrue(countUppercase(password) >= 2);
        }

        @Test
        void testMerge_WithZeroValues() {
            DefaultPasswordRuleConf rule1 = mock(DefaultPasswordRuleConf.class);
            when(rule1.getMinLength()).thenReturn(0);
            when(rule1.getMaxLength()).thenReturn(0);
            when(rule1.isUsernameAllowed()).thenReturn(true);
            when(rule1.getSpecialChars()).thenReturn(Collections.emptyList());
            when(rule1.getIllegalChars()).thenReturn(Collections.emptyList());
            when(rule1.getWordsNotPermitted()).thenReturn(Collections.emptyList());
            when(rule1.getSchemasNotPermitted()).thenReturn(Collections.emptyList());

            DefaultPasswordRuleConf rule2 = mock(DefaultPasswordRuleConf.class);
            when(rule2.getMinLength()).thenReturn(8);
            when(rule2.getMaxLength()).thenReturn(16);
            when(rule2.isUsernameAllowed()).thenReturn(true);
            when(rule2.getSpecialChars()).thenReturn(Collections.emptyList());
            when(rule2.getIllegalChars()).thenReturn(Collections.emptyList());
            when(rule2.getWordsNotPermitted()).thenReturn(Collections.emptyList());
            when(rule2.getSchemasNotPermitted()).thenReturn(Collections.emptyList());

            DefaultPasswordGenerator generator = new DefaultPasswordGenerator();
            DefaultPasswordRuleConf result = generator.merge(Arrays.asList(rule1, rule2));

            boolean condition = result.getMinLength() == 8 && result.getMaxLength() == 16;
            assertTrue(condition);
        }

        @Test
        void testMerge_WithNoConfsAppliesDefaultMinAndMax() {
            DefaultPasswordGenerator generator = new DefaultPasswordGenerator();

            DefaultPasswordRuleConf result = generator.merge(Collections.emptyList());

            assertEquals(8, result.getMinLength());
            assertEquals(64, result.getMaxLength());
            assertTrue(result.isUsernameAllowed());
        }

        @Test
        void testMerge_TakesGreatestMinimumLength() {
            DefaultPasswordRuleConf rule1 = new DefaultPasswordRuleConf();
            rule1.setMinLength(10);
            rule1.setMaxLength(40);

            DefaultPasswordRuleConf rule2 = new DefaultPasswordRuleConf();
            rule2.setMinLength(18);
            rule2.setMaxLength(50);

            DefaultPasswordGenerator generator = new DefaultPasswordGenerator();
            DefaultPasswordRuleConf result = generator.merge(Arrays.asList(rule1, rule2));

            assertEquals(18, result.getMinLength());
        }

        @Test
        void testMerge_TakesSmallestNonZeroMaximumLength() {
            DefaultPasswordRuleConf rule1 = new DefaultPasswordRuleConf();
            rule1.setMinLength(8);
            rule1.setMaxLength(40);

            DefaultPasswordRuleConf rule2 = new DefaultPasswordRuleConf();
            rule2.setMinLength(10);
            rule2.setMaxLength(20);

            DefaultPasswordGenerator generator = new DefaultPasswordGenerator();
            DefaultPasswordRuleConf result = generator.merge(Arrays.asList(rule1, rule2));

            assertEquals(20, result.getMaxLength());
        }

        @Test
        void testMerge_TakesStrongestCharacterRequirements() {
            DefaultPasswordRuleConf rule1 = new DefaultPasswordRuleConf();
            rule1.setAlphabetical(2);
            rule1.setUppercase(1);
            rule1.setLowercase(3);
            rule1.setDigit(2);
            rule1.setSpecial(1);
            rule1.setRepeatSame(2);

            DefaultPasswordRuleConf rule2 = new DefaultPasswordRuleConf();
            rule2.setAlphabetical(5);
            rule2.setUppercase(4);
            rule2.setLowercase(1);
            rule2.setDigit(6);
            rule2.setSpecial(2);
            rule2.setRepeatSame(4);

            DefaultPasswordGenerator generator = new DefaultPasswordGenerator();
            DefaultPasswordRuleConf result = generator.merge(Arrays.asList(rule1, rule2));

            assertEquals(5, result.getAlphabetical());
            assertEquals(4, result.getUppercase());
            assertEquals(3, result.getLowercase());
            assertEquals(6, result.getDigit());
            assertEquals(2, result.getSpecial());
            assertEquals(4, result.getRepeatSame());
        }

        @Test
        void testMerge_UsernameAllowedIsFalseWhenAnyPolicyDisallowsIt() {
            DefaultPasswordRuleConf rule1 = new DefaultPasswordRuleConf();
            rule1.setUsernameAllowed(true);

            DefaultPasswordRuleConf rule2 = new DefaultPasswordRuleConf();
            rule2.setUsernameAllowed(false);

            DefaultPasswordGenerator generator = new DefaultPasswordGenerator();
            DefaultPasswordRuleConf result = generator.merge(Arrays.asList(rule1, rule2));

            assertFalse(result.isUsernameAllowed());
        }

        @Test
        void testMerge_AggregatesSpecialAndIllegalCharacters() {
            DefaultPasswordRuleConf rule1 = new DefaultPasswordRuleConf();
            rule1.getSpecialChars().add('!');
            rule1.getIllegalChars().add('x');

            DefaultPasswordRuleConf rule2 = new DefaultPasswordRuleConf();
            rule2.getSpecialChars().add('#');
            rule2.getIllegalChars().add('y');

            DefaultPasswordGenerator generator = new DefaultPasswordGenerator();
            DefaultPasswordRuleConf result = generator.merge(Arrays.asList(rule1, rule2));

            assertTrue(result.getSpecialChars().contains('!'));
            assertTrue(result.getSpecialChars().contains('#'));
            assertTrue(result.getIllegalChars().contains('x'));
            assertTrue(result.getIllegalChars().contains('y'));
        }

        @Test
        void testMerge_AggregatesWordsAndSchemasNotPermitted() {
            DefaultPasswordRuleConf rule1 = new DefaultPasswordRuleConf();
            rule1.getWordsNotPermitted().add("password");
            rule1.getSchemasNotPermitted().add("email");

            DefaultPasswordRuleConf rule2 = new DefaultPasswordRuleConf();
            rule2.getWordsNotPermitted().add("syncope");
            rule2.getSchemasNotPermitted().add("surname");

            DefaultPasswordGenerator generator = new DefaultPasswordGenerator();
            DefaultPasswordRuleConf result = generator.merge(Arrays.asList(rule1, rule2));

            assertTrue(result.getWordsNotPermitted().contains("password"));
            assertTrue(result.getWordsNotPermitted().contains("syncope"));
            assertTrue(result.getSchemasNotPermitted().contains("email"));
            assertTrue(result.getSchemasNotPermitted().contains("surname"));
        }

        @Test
        void testGenerate_WithResourceAndRealmsCollectsPoliciesInOrder() {
            PasswordPolicy resourcePolicy = mock(PasswordPolicy.class);
            PasswordPolicy realmPolicy1 = mock(PasswordPolicy.class);
            PasswordPolicy realmPolicy2 = mock(PasswordPolicy.class);

            ExternalResource resource = mock(ExternalResource.class);
            when(resource.getPasswordPolicy()).thenReturn(resourcePolicy);

            Realm realm1 = mock(Realm.class);
            when(realm1.getPasswordPolicy()).thenReturn(realmPolicy1);

            Realm realm2 = mock(Realm.class);
            when(realm2.getPasswordPolicy()).thenReturn(realmPolicy2);

            CapturingDefaultPasswordGenerator generator = new CapturingDefaultPasswordGenerator();

            String result = generator.generate(resource, Arrays.asList(realm1, realm2));

            assertEquals("captured-password", result);
            assertEquals(3, generator.getCapturedPolicies().size());
            assertSame(resourcePolicy, generator.getCapturedPolicies().get(0));
            assertSame(realmPolicy1, generator.getCapturedPolicies().get(1));
            assertSame(realmPolicy2, generator.getCapturedPolicies().get(2));
        }

        @Test
        void testGenerate_WithNullResourceSkipsResourcePolicy() {
            PasswordPolicy realmPolicy = mock(PasswordPolicy.class);

            Realm realm = mock(Realm.class);
            when(realm.getPasswordPolicy()).thenReturn(realmPolicy);

            CapturingDefaultPasswordGenerator generator = new CapturingDefaultPasswordGenerator();

            String result = generator.generate(null, Collections.singletonList(realm));

            assertEquals("captured-password", result);
            assertEquals(1, generator.getCapturedPolicies().size());
            assertSame(realmPolicy, generator.getCapturedPolicies().get(0));
        }

        @Test
        void testGenerate_WithNullPolicyListThrowsException() {
            DefaultPasswordGenerator generator = new DefaultPasswordGenerator();

            assertThrows(Exception.class, () -> generator.generate((List<PasswordPolicy>) null));
        }

        @Test
        void testGenerate_WithNullRuleConfThrowsException() {
            DefaultPasswordGenerator generator = new DefaultPasswordGenerator();

            assertThrows(Exception.class, () -> generator.generate((DefaultPasswordRuleConf) null));
        }
    }