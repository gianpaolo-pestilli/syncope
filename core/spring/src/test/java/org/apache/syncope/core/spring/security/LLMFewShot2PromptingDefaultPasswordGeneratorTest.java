package org.apache.syncope.core.spring.security;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.apache.syncope.common.lib.policy.DefaultPasswordRuleConf;
import org.apache.syncope.common.lib.policy.PasswordRuleConf;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.Realm;
import org.apache.syncope.core.persistence.api.entity.policy.PasswordPolicy;
import org.apache.syncope.core.provisioning.api.rules.PasswordRule;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
@Disabled
class LLMFewShot2PromptingDefaultPasswordGeneratorTest {

        private static class TestableDefaultPasswordGenerator extends DefaultPasswordGenerator {

            private final Map<PasswordPolicy, List<PasswordRule>> rulesByPolicy = new IdentityHashMap<>();

            DefaultPasswordRuleConf doMerge(final List<DefaultPasswordRuleConf> ruleConfs) {
                return merge(ruleConfs);
            }

            String doGenerate(final DefaultPasswordRuleConf ruleConf) {
                return generate(ruleConf);
            }

            void addRules(final PasswordPolicy policy, final PasswordRule... rules) {
                rulesByPolicy.put(policy, Arrays.asList(rules));
            }

            @Override
            protected List<PasswordRule> getPasswordRules(final PasswordPolicy policy) {
                return rulesByPolicy.getOrDefault(policy, Collections.emptyList());
            }
        }

        private static DefaultPasswordRuleConf conf(final int minLength, final int maxLength) {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(minLength);
            conf.setMaxLength(maxLength);
            return conf;
        }

        private static PasswordRule rule(final PasswordRuleConf conf) {
            PasswordRule rule = mock(PasswordRule.class);
            when(rule.getConf()).thenReturn(conf);
            return rule;
        }

        @Test
        void testMerge_WithSingleRule() {
            DefaultPasswordRuleConf rule = conf(8, 16);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            DefaultPasswordRuleConf result = generator.doMerge(Collections.singletonList(rule));

            boolean condition = result.getMinLength() == 8 && result.getMaxLength() == 16;
            assertTrue(condition);
        }

        @Test
        void testMerge_WithEmptyRuleListUsesDefaultBoundaries() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            DefaultPasswordRuleConf result = generator.doMerge(Collections.emptyList());

            assertAll(
                    () -> assertTrue(result.getMinLength() == 0),
                    () -> assertTrue(result.getMaxLength() == 64),
                    () -> assertTrue(result.isUsernameAllowed()));
        }

        @Test
        void testMerge_WithMultipleRulesUsesHighestMinimumAndLowestMaximum() {
            DefaultPasswordRuleConf rule1 = conf(8, 32);
            DefaultPasswordRuleConf rule2 = conf(12, 20);
            DefaultPasswordRuleConf rule3 = conf(10, 16);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            DefaultPasswordRuleConf result = generator.doMerge(Arrays.asList(rule1, rule2, rule3));

            assertAll(
                    () -> assertTrue(result.getMinLength() == 12),
                    () -> assertTrue(result.getMaxLength() == 16));
        }

        @Test
        void testMerge_WithZeroMinimumDoesNotIncreaseMinimumLength() {
            DefaultPasswordRuleConf zeroMinimumRule = conf(0, 20);
            DefaultPasswordRuleConf concreteMinimumRule = conf(9, 18);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            DefaultPasswordRuleConf result = generator.doMerge(Arrays.asList(zeroMinimumRule, concreteMinimumRule));

            assertAll(
                    () -> assertTrue(result.getMinLength() == 9),
                    () -> assertTrue(result.getMaxLength() == 18));
        }

        @Test
        void testMerge_WithOnlyZeroMinimumKeepsVeryMinimumLength() {
            DefaultPasswordRuleConf rule = conf(0, 12);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            DefaultPasswordRuleConf result = generator.doMerge(Collections.singletonList(rule));

            assertAll(
                    () -> assertTrue(result.getMinLength() == 0),
                    () -> assertTrue(result.getMaxLength() == 12));
        }

        @Test
        void testMerge_WithZeroMaximumFallsBackToVeryMaximumLength() {
            DefaultPasswordRuleConf rule = conf(9, 0);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            DefaultPasswordRuleConf result = generator.doMerge(Collections.singletonList(rule));

            assertAll(
                    () -> assertTrue(result.getMinLength() == 9),
                    () -> assertTrue(result.getMaxLength() == 64));
        }

        @Test
        void testMerge_WithMultipleZeroMaximumRulesKeepsRestrictiveNonZeroMaximum() {
            DefaultPasswordRuleConf rule1 = conf(8, 0);
            DefaultPasswordRuleConf rule2 = conf(10, 18);
            DefaultPasswordRuleConf rule3 = conf(6, 0);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            DefaultPasswordRuleConf result = generator.doMerge(Arrays.asList(rule1, rule2, rule3));

            assertAll(
                    () -> assertTrue(result.getMinLength() == 10),
                    () -> assertTrue(result.getMaxLength() == 18));
        }

        @Test
        void testMerge_UsernameAllowedRemainsTrueWhenAllRulesAllowUsername() {
            DefaultPasswordRuleConf rule1 = conf(8, 16);
            rule1.setUsernameAllowed(true);

            DefaultPasswordRuleConf rule2 = conf(10, 14);
            rule2.setUsernameAllowed(true);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            DefaultPasswordRuleConf result = generator.doMerge(Arrays.asList(rule1, rule2));

            assertTrue(result.isUsernameAllowed());
        }

        @Test
        void testMerge_UsernameAllowedIsFalseWhenAnyRuleDisallowsUsername() {
            DefaultPasswordRuleConf usernameAllowed = conf(8, 16);
            usernameAllowed.setUsernameAllowed(true);

            DefaultPasswordRuleConf usernameNotAllowed = conf(8, 16);
            usernameNotAllowed.setUsernameAllowed(false);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            DefaultPasswordRuleConf result = generator.doMerge(Arrays.asList(usernameAllowed, usernameNotAllowed));

            assertFalse(result.isUsernameAllowed());
        }

        @Test
        void testGenerate_WithFixedLengthRuleReturnsPasswordOfExactLength() {
            DefaultPasswordRuleConf rule = conf(12, 12);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            String password = generator.doGenerate(rule);

            boolean condition = password != null && password.length() == 12;
            assertTrue(condition);
        }

        @Test
        void testGenerate_WithMinimumAndMaximumReturnsPasswordWithinRange() {
            DefaultPasswordRuleConf rule = conf(8, 16);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            String password = generator.doGenerate(rule);

            boolean condition = password != null && password.length() >= 8 && password.length() <= 16;
            assertTrue(condition);
        }

        @Test
        void testGenerate_WithZeroMinimumUsesEffectiveMinimumLength() {
            DefaultPasswordRuleConf rule = conf(0, 12);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            String password = generator.doGenerate(generator.doMerge(Collections.singletonList(rule)));

            boolean condition = password != null && password.length() >= 8 && password.length() <= 12;
            assertTrue(condition);
        }

        @Test
        void testGenerate_WithNoPoliciesReturnsNonNullPasswordWithinDefaultRange() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            String password = generator.generate(Collections.emptyList());

            boolean condition = password != null && password.length() >= 8 && password.length() <= 64;
            assertTrue(condition);
        }

        @Test
        void testGenerate_WithOnePolicyUsesItsDefaultPasswordRuleConf() {
            DefaultPasswordRuleConf ruleConf = conf(9, 13);

            PasswordPolicy policy = mock(PasswordPolicy.class);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.addRules(policy, rule(ruleConf));

            String password = generator.generate(Collections.singletonList(policy));

            boolean condition = password != null && password.length() >= 9 && password.length() <= 13;
            assertTrue(condition);
        }

        @Test
        void testGenerate_WithTwoPoliciesUsesMergedLengthRange() {
            DefaultPasswordRuleConf ruleConf1 = conf(8, 16);
            DefaultPasswordRuleConf ruleConf2 = conf(10, 14);

            PasswordPolicy policy1 = mock(PasswordPolicy.class);
            PasswordPolicy policy2 = mock(PasswordPolicy.class);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.addRules(policy1, rule(ruleConf1));
            generator.addRules(policy2, rule(ruleConf2));

            String password = generator.generate(Arrays.asList(policy1, policy2));

            boolean condition = password != null && password.length() >= 10 && password.length() <= 14;
            assertTrue(condition);
        }

        @Test
        void testGenerate_WithPolicyContainingMultipleRulesMergesAllDefaultRules() {
            DefaultPasswordRuleConf minRule = conf(11, 30);
            DefaultPasswordRuleConf maxRule = conf(4, 15);
            DefaultPasswordRuleConf broadRule = conf(1, 64);

            PasswordPolicy policy = mock(PasswordPolicy.class);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.addRules(policy, rule(minRule), rule(maxRule), rule(broadRule));

            String password = generator.generate(Collections.singletonList(policy));

            boolean condition = password != null && password.length() >= 11 && password.length() <= 15;
            assertTrue(condition);
        }

        @Test
        void testGenerate_IgnoresUnsupportedPasswordRuleConf() {
            PasswordRuleConf unsupportedConf = mock(PasswordRuleConf.class);
            PasswordPolicy policy = mock(PasswordPolicy.class);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.addRules(policy, rule(unsupportedConf));

            String password = generator.generate(Collections.singletonList(policy));

            boolean condition = password != null && password.length() >= 8 && password.length() <= 64;
            assertTrue(condition);
        }

        @Test
        void testGenerate_WithSupportedAndUnsupportedRuleConfsUsesOnlyDefaultPasswordRuleConf() {
            PasswordRuleConf unsupportedConf = mock(PasswordRuleConf.class);
            DefaultPasswordRuleConf supportedConf = conf(10, 12);

            PasswordPolicy policy = mock(PasswordPolicy.class);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.addRules(policy, rule(unsupportedConf), rule(supportedConf));

            String password = generator.generate(Collections.singletonList(policy));

            boolean condition = password != null && password.length() >= 10 && password.length() <= 12;
            assertTrue(condition);
        }

        @Test
        void testGenerate_FromResourceAndRealmsMergesResourceAndRealmPolicies() {
            DefaultPasswordRuleConf resourceConf = conf(8, 30);
            DefaultPasswordRuleConf realmConf = conf(14, 18);

            PasswordPolicy resourcePolicy = mock(PasswordPolicy.class);
            PasswordPolicy realmPolicy = mock(PasswordPolicy.class);

            ExternalResource resource = mock(ExternalResource.class);
            when(resource.getPasswordPolicy()).thenReturn(resourcePolicy);

            Realm realm = mock(Realm.class);
            when(realm.getPasswordPolicy()).thenReturn(realmPolicy);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.addRules(resourcePolicy, rule(resourceConf));
            generator.addRules(realmPolicy, rule(realmConf));

            String password = generator.generate(resource, Collections.singletonList(realm));

            boolean condition = password != null && password.length() >= 14 && password.length() <= 18;
            assertTrue(condition);
        }

        @Test
        void testGenerate_FromResourceAndRealmsSkipsNullPolicies() {
            DefaultPasswordRuleConf realmConf = conf(11, 13);

            PasswordPolicy realmPolicy = mock(PasswordPolicy.class);

            ExternalResource resource = mock(ExternalResource.class);
            when(resource.getPasswordPolicy()).thenReturn(null);

            Realm realmWithoutPolicy = mock(Realm.class);
            when(realmWithoutPolicy.getPasswordPolicy()).thenReturn(null);

            Realm realmWithPolicy = mock(Realm.class);
            when(realmWithPolicy.getPasswordPolicy()).thenReturn(realmPolicy);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.addRules(realmPolicy, rule(realmConf));

            String password = generator.generate(resource, Arrays.asList(realmWithoutPolicy, realmWithPolicy));

            boolean condition = password != null && password.length() >= 11 && password.length() <= 13;
            assertTrue(condition);
        }

        @Test
        void testGenerate_FromMultipleRealmsUsesMostRestrictiveRealmPolicies() {
            DefaultPasswordRuleConf realmConf1 = conf(8, 25);
            DefaultPasswordRuleConf realmConf2 = conf(12, 20);
            DefaultPasswordRuleConf realmConf3 = conf(10, 16);

            PasswordPolicy policy1 = mock(PasswordPolicy.class);
            PasswordPolicy policy2 = mock(PasswordPolicy.class);
            PasswordPolicy policy3 = mock(PasswordPolicy.class);

            ExternalResource resource = mock(ExternalResource.class);
            when(resource.getPasswordPolicy()).thenReturn(null);

            Realm realm1 = mock(Realm.class);
            when(realm1.getPasswordPolicy()).thenReturn(policy1);

            Realm realm2 = mock(Realm.class);
            when(realm2.getPasswordPolicy()).thenReturn(policy2);

            Realm realm3 = mock(Realm.class);
            when(realm3.getPasswordPolicy()).thenReturn(policy3);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.addRules(policy1, rule(realmConf1));
            generator.addRules(policy2, rule(realmConf2));
            generator.addRules(policy3, rule(realmConf3));

            String password = generator.generate(resource, Arrays.asList(realm1, realm2, realm3));

            boolean condition = password != null && password.length() >= 12 && password.length() <= 16;
            assertTrue(condition);
        }

        @Test
        void testGenerate_RepeatedCallsReturnValidPasswords() {
            DefaultPasswordRuleConf rule = conf(16, 16);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            String password1 = generator.doGenerate(rule);
            String password2 = generator.doGenerate(rule);
            String password3 = generator.doGenerate(rule);

            assertAll(
                    () -> assertNotNull(password1),
                    () -> assertNotNull(password2),
                    () -> assertNotNull(password3),
                    () -> assertTrue(password1.length() == 16),
                    () -> assertTrue(password2.length() == 16),
                    () -> assertTrue(password3.length() == 16));
        }
    }

