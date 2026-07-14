package org.apache.syncope.core.spring.security;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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
class LLMZeroShot3PromptingDefaultPasswordGeneratorTest {

    /**
     * Black-box tests for DefaultPasswordGenerator.
     *
     * The tests avoid asserting implementation details of the generator algorithm.
     * They exercise only observable behavior through public/protected operation signatures
     * and use a test subclass to provide password rules without relying on
     * ImplementationManager or concrete persistence entities.
     */

        private static final class TestableDefaultPasswordGenerator extends DefaultPasswordGenerator {

            private final Map<PasswordPolicy, List<PasswordRule>> rulesByPolicy = new IdentityHashMap<>();

            private List<PasswordPolicy> observedPolicies;

            void registerRules(final PasswordPolicy policy, final PasswordRule... rules) {
                rulesByPolicy.put(policy, List.of(rules));
            }

            DefaultPasswordRuleConf mergeConfs(final List<DefaultPasswordRuleConf> confs) {
                return super.merge(confs);
            }

            String generateFromConf(final DefaultPasswordRuleConf conf) {
                return super.generate(conf);
            }

            List<PasswordPolicy> observedPolicies() {
                return observedPolicies;
            }

            @Override
            protected List<PasswordRule> getPasswordRules(final PasswordPolicy policy) {
                return rulesByPolicy.getOrDefault(policy, Collections.emptyList());
            }

            @Override
            public String generate(final List<PasswordPolicy> policies) {
                observedPolicies = policies == null ? null : new ArrayList<>(policies);
                return super.generate(policies);
            }
        }

        private static PasswordPolicy policy() {
            return mock(PasswordPolicy.class);
        }

        private static PasswordRule rule(final PasswordRuleConf conf) {
            PasswordRule rule = mock(PasswordRule.class);
            when(rule.getConf()).thenReturn(conf);
            return rule;
        }

        private static PasswordRule nonDefaultRule() {
            PasswordRuleConf conf = mock(PasswordRuleConf.class);
            return rule(conf);
        }

        private static DefaultPasswordRuleConf conf() {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setUsernameAllowed(true);
            return conf;
        }

        private static long uppercaseCount(final String value) {
            return value.chars().filter(Character::isUpperCase).count();
        }

        private static long lowercaseCount(final String value) {
            return value.chars().filter(Character::isLowerCase).count();
        }

        private static long digitCount(final String value) {
            return value.chars().filter(Character::isDigit).count();
        }

        private static long alphabeticalCount(final String value) {
            return value.chars().filter(Character::isAlphabetic).count();
        }

        private static long specialCount(final String value) {
            return value.chars().filter(ch -> !Character.isLetterOrDigit(ch)).count();
        }

        @Test
        void generateWithEmptyPolicyListReturnsDefaultPassword() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            String password = generator.generate(Collections.emptyList());

            assertNotNull(password);
            assertFalse(password.isBlank());
            assertTrue(password.length() >= 8);
            assertTrue(password.length() <= 64);
        }

        @Test
        void generateWithNullPolicyListThrowsNullPointerException() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            assertThrows(NullPointerException.class, () -> generator.generate((List<PasswordPolicy>) null));
        }

        @Test
        void generateWithExactLengthPolicyReturnsPasswordHavingThatLength() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            PasswordPolicy policy = policy();

            DefaultPasswordRuleConf conf = conf();
            conf.setMinLength(12);
            conf.setMaxLength(12);

            generator.registerRules(policy, rule(conf));

            String password = generator.generate(List.of(policy));

            assertEquals(12, password.length());
        }

        @Test
        void generateWithLengthRangeKeepsPasswordInsideRange() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            PasswordPolicy policy = policy();

            DefaultPasswordRuleConf conf = conf();
            conf.setMinLength(10);
            conf.setMaxLength(14);

            generator.registerRules(policy, rule(conf));

            String password = generator.generate(List.of(policy));

            assertTrue(password.length() >= 10);
            assertTrue(password.length() <= 14);
        }

        @Test
        void generateWithZeroMinLengthUsesDefaultMinimumLength() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            PasswordPolicy policy = policy();

            DefaultPasswordRuleConf conf = conf();
            conf.setMinLength(0);
            conf.setMaxLength(0);

            generator.registerRules(policy, rule(conf));

            String password = generator.generate(List.of(policy));

            assertTrue(password.length() >= 8);
            assertTrue(password.length() <= 64);
        }

        @Test
        void generateHonorsUppercaseRequirement() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            PasswordPolicy policy = policy();

            DefaultPasswordRuleConf conf = conf();
            conf.setMinLength(10);
            conf.setMaxLength(10);
            conf.setUppercase(3);

            generator.registerRules(policy, rule(conf));

            String password = generator.generate(List.of(policy));

            assertEquals(10, password.length());
            assertTrue(uppercaseCount(password) >= 3, password);
        }

        @Test
        void generateHonorsLowercaseRequirement() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            PasswordPolicy policy = policy();

            DefaultPasswordRuleConf conf = conf();
            conf.setMinLength(10);
            conf.setMaxLength(10);
            conf.setLowercase(4);

            generator.registerRules(policy, rule(conf));

            String password = generator.generate(List.of(policy));

            assertEquals(10, password.length());
            assertTrue(lowercaseCount(password) >= 4, password);
        }

        @Test
        void generateHonorsDigitRequirement() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            PasswordPolicy policy = policy();

            DefaultPasswordRuleConf conf = conf();
            conf.setMinLength(10);
            conf.setMaxLength(10);
            conf.setDigit(3);

            generator.registerRules(policy, rule(conf));

            String password = generator.generate(List.of(policy));

            assertEquals(10, password.length());
            assertTrue(digitCount(password) >= 3, password);
        }

        @Test
        void generateHonorsAlphabeticalRequirement() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            PasswordPolicy policy = policy();

            DefaultPasswordRuleConf conf = conf();
            conf.setMinLength(10);
            conf.setMaxLength(10);
            conf.setAlphabetical(5);

            generator.registerRules(policy, rule(conf));

            String password = generator.generate(List.of(policy));

            assertEquals(10, password.length());
            assertTrue(alphabeticalCount(password) >= 5, password);
        }

        @Test
        void generateHonorsSpecialCharacterRequirement() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            PasswordPolicy policy = policy();

            DefaultPasswordRuleConf conf = conf();
            conf.setMinLength(12);
            conf.setMaxLength(12);
            conf.setSpecial(2);
            conf.getSpecialChars().add('!');
            conf.getSpecialChars().add('@');
            conf.getSpecialChars().add('#');

            generator.registerRules(policy, rule(conf));

            String password = generator.generate(List.of(policy));

            assertEquals(12, password.length());
            assertTrue(specialCount(password) >= 2, password);
        }

        @Test
        void generateDoesNotUseIllegalCharacters() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            PasswordPolicy policy = policy();

            DefaultPasswordRuleConf conf = conf();
            conf.setMinLength(32);
            conf.setMaxLength(32);
            conf.getIllegalChars().add('a');
            conf.getIllegalChars().add('A');
            conf.getIllegalChars().add('1');
            conf.getIllegalChars().add('!');

            generator.registerRules(policy, rule(conf));

            String password = generator.generate(List.of(policy));

            assertFalse(password.contains("a"), password);
            assertFalse(password.contains("A"), password);
            assertFalse(password.contains("1"), password);
            assertFalse(password.contains("!"), password);
        }

        @Test
        void generateMergesRulesFromDifferentPolicies() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            PasswordPolicy lengthPolicy = policy();
            DefaultPasswordRuleConf lengthConf = conf();
            lengthConf.setMinLength(14);
            lengthConf.setMaxLength(14);

            PasswordPolicy contentPolicy = policy();
            DefaultPasswordRuleConf contentConf = conf();
            contentConf.setDigit(2);
            contentConf.setUppercase(2);

            generator.registerRules(lengthPolicy, rule(lengthConf));
            generator.registerRules(contentPolicy, rule(contentConf));

            String password = generator.generate(List.of(lengthPolicy, contentPolicy));

            assertEquals(14, password.length());
            assertTrue(digitCount(password) >= 2, password);
            assertTrue(uppercaseCount(password) >= 2, password);
        }

        @Test
        void generateIgnoresNonDefaultPasswordRuleConfigurations() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            PasswordPolicy policy = policy();

            generator.registerRules(policy, nonDefaultRule());

            String password = generator.generate(List.of(policy));

            assertNotNull(password);
            assertTrue(password.length() >= 8);
            assertTrue(password.length() <= 64);
        }

        @Test
        void generateFailsWhenRulesAreImpossibleToSatisfy() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            PasswordPolicy policy = policy();

            DefaultPasswordRuleConf conf = conf();
            conf.setMinLength(6);
            conf.setMaxLength(6);
            conf.setUppercase(4);
            conf.setLowercase(4);

            generator.registerRules(policy, rule(conf));

            assertThrows(RuntimeException.class, () -> generator.generate(List.of(policy)));
        }

        @Test
        void generateFailsWhenMinimumLengthIsGreaterThanMaximumLength() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            PasswordPolicy policy = policy();

            DefaultPasswordRuleConf conf = conf();
            conf.setMinLength(20);
            conf.setMaxLength(10);

            generator.registerRules(policy, rule(conf));

            assertThrows(RuntimeException.class, () -> generator.generate(List.of(policy)));
        }

        @Test
        void mergeUsesStrictestMinimumAndMaximumLengths() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            DefaultPasswordRuleConf weak = conf();
            weak.setMinLength(8);
            weak.setMaxLength(30);

            DefaultPasswordRuleConf strict = conf();
            strict.setMinLength(14);
            strict.setMaxLength(18);

            DefaultPasswordRuleConf merged = generator.mergeConfs(List.of(weak, strict));

            assertEquals(14, merged.getMinLength());
            assertEquals(18, merged.getMaxLength());
        }

        @Test
        void mergeUsesStrictestCharacterRequirements() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            DefaultPasswordRuleConf first = conf();
            first.setUppercase(1);
            first.setLowercase(5);
            first.setDigit(2);
            first.setSpecial(0);
            first.setAlphabetical(6);
            first.setRepeatSame(3);

            DefaultPasswordRuleConf second = conf();
            second.setUppercase(4);
            second.setLowercase(2);
            second.setDigit(1);
            second.setSpecial(3);
            second.setAlphabetical(3);
            second.setRepeatSame(2);

            DefaultPasswordRuleConf merged = generator.mergeConfs(List.of(first, second));

            assertEquals(4, merged.getUppercase());
            assertEquals(5, merged.getLowercase());
            assertEquals(2, merged.getDigit());
            assertEquals(3, merged.getSpecial());
            assertEquals(6, merged.getAlphabetical());
            assertEquals(2, merged.getRepeatSame());
        }

        @Test
        void mergeKeepsUsernameAllowedOnlyWhenAllConfigurationsAllowIt() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            DefaultPasswordRuleConf allowing = conf();
            allowing.setUsernameAllowed(true);

            DefaultPasswordRuleConf denying = conf();
            denying.setUsernameAllowed(false);

            DefaultPasswordRuleConf merged = generator.mergeConfs(List.of(allowing, denying));

            assertFalse(merged.isUsernameAllowed());
        }

        @Test
        void mergeCombinesIllegalSpecialForbiddenWordsAndSchemas() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            DefaultPasswordRuleConf first = conf();
            first.getIllegalChars().add('x');
            first.getSpecialChars().add('!');
            first.getWordsNotPermitted().add("admin");
            first.getSchemasNotPermitted().add("email");

            DefaultPasswordRuleConf second = conf();
            second.getIllegalChars().add('y');
            second.getSpecialChars().add('@');
            second.getWordsNotPermitted().add("password");
            second.getSchemasNotPermitted().add("surname");

            DefaultPasswordRuleConf merged = generator.mergeConfs(List.of(first, second));

            assertTrue(merged.getIllegalChars().containsAll(List.of('x', 'y')));
            assertTrue(merged.getSpecialChars().containsAll(List.of('!', '@')));
            assertTrue(merged.getWordsNotPermitted().containsAll(List.of("admin", "password")));
            assertTrue(merged.getSchemasNotPermitted().containsAll(List.of("email", "surname")));
        }

        @Test
        void generateFromResourceAndRealmsCollectsResourceAndRealmPasswordPolicies() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            ExternalResource resource = mock(ExternalResource.class);
            Realm realmOne = mock(Realm.class);
            Realm realmTwo = mock(Realm.class);

            PasswordPolicy resourcePolicy = policy();
            PasswordPolicy realmOnePolicy = policy();
            PasswordPolicy realmTwoPolicy = policy();

            when(resource.getPasswordPolicy()).thenReturn(resourcePolicy);
            when(realmOne.getPasswordPolicy()).thenReturn(realmOnePolicy);
            when(realmTwo.getPasswordPolicy()).thenReturn(realmTwoPolicy);

            DefaultPasswordRuleConf conf = conf();
            conf.setMinLength(9);
            conf.setMaxLength(9);

            generator.registerRules(resourcePolicy, rule(conf));

            String password = generator.generate(resource, List.of(realmOne, realmTwo));

            assertEquals(9, password.length());
            assertEquals(List.of(resourcePolicy, realmOnePolicy, realmTwoPolicy), generator.observedPolicies());
        }

        @Test
        void generateFromResourceAndRealmsHandlesMissingPoliciesAsDefaultGeneration() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            ExternalResource resource = mock(ExternalResource.class);
            Realm realm = mock(Realm.class);

            when(resource.getPasswordPolicy()).thenReturn(null);
            when(realm.getPasswordPolicy()).thenReturn(null);

            String password = generator.generate(resource, List.of(realm));

            assertNotNull(password);
            assertTrue(password.length() >= 8);
            assertTrue(password.length() <= 64);
        }
    }
