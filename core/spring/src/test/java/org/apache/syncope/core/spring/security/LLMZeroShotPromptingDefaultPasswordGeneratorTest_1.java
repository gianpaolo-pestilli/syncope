package org.apache.syncope.core.spring.security;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import org.apache.syncope.common.lib.policy.DefaultPasswordRuleConf;
import org.apache.syncope.common.lib.policy.PasswordRuleConf;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.Implementation;
import org.apache.syncope.core.persistence.api.entity.Realm;
import org.apache.syncope.core.persistence.api.entity.policy.PasswordPolicy;
import org.apache.syncope.core.provisioning.api.rules.PasswordRule;
import org.apache.syncope.core.spring.implementation.ImplementationManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class LLMZeroShotPromptingDefaultPasswordGeneratorTest_1 {

        private static final Pattern UPPERCASE = Pattern.compile(".*[A-Z].*");

        private static final Pattern LOWERCASE = Pattern.compile(".*[a-z].*");

        private static final Pattern DIGIT = Pattern.compile(".*\\d.*");

        private static final Pattern SPECIAL = Pattern.compile(".*[^a-zA-Z0-9].*");

        private final TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        private static class TestableDefaultPasswordGenerator extends DefaultPasswordGenerator {

            String generateFromRuleConf(final DefaultPasswordRuleConf ruleConf) {
                return super.generate(ruleConf);
            }

            DefaultPasswordRuleConf mergeRuleConfs(final List<DefaultPasswordRuleConf> ruleConfs) {
                return super.merge(ruleConfs);
            }

            List<PasswordRule> getRulesFromPolicy(final PasswordPolicy policy) {
                return super.getPasswordRules(policy);
            }

            void cacheRule(final String key, final PasswordRule rule) {
                perContextPasswordRules.put(key, rule);
            }

            PasswordRule cachedRule(final String key) {
                return perContextPasswordRules.get(key);
            }

            int cacheSize() {
                return perContextPasswordRules.size();
            }
        }

        private static DefaultPasswordRuleConf conf(
                final int minLength,
                final int maxLength,
                final int uppercase,
                final int lowercase,
                final int digit,
                final int special) {

            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(minLength);
            conf.setMaxLength(maxLength);
            conf.setUppercase(uppercase);
            conf.setLowercase(lowercase);
            conf.setDigit(digit);
            conf.setSpecial(special);
            return conf;
        }

        private static PasswordRule passwordRule(final PasswordRuleConf conf) {
            PasswordRule rule = mock(PasswordRule.class);
            when(rule.getConf()).thenReturn(conf);
            return rule;
        }

        private static Implementation implementation(final String key) {
            Implementation implementation = mock(Implementation.class);
            when(implementation.getKey()).thenReturn(key);
            return implementation;
        }

        private static PasswordPolicy policy(final String implementationKey, final PasswordRule rule) {
            Implementation implementation = implementation(implementationKey);
            PasswordPolicy policy = policyWithRules(List.of(implementation));
            return policy;
        }

        private static PasswordPolicy policyWithCachedRule(
                final TestableDefaultPasswordGenerator generator,
                final String implementationKey,
                final PasswordRule rule) {

            generator.cacheRule(implementationKey, rule);
            return policy(implementationKey, rule);
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static PasswordPolicy policyWithRules(final List<Implementation> implementations) {
            PasswordPolicy policy = mock(PasswordPolicy.class);

            /*
             * Important:
             * PasswordPolicy#getRules() is declared with generic wildcards in Syncope.
             * Mockito's when(...).thenReturn(...) can fail to compile with:
             * "Cannot resolve method thenReturn(List<E>)".
             * doReturn(...).when(...) avoids generic capture problems.
             */
            doReturn(implementations).when(policy).getRules();

            return policy;
        }

        private static ExternalResource resourceWithPolicy(final PasswordPolicy policy) {
            ExternalResource resource = mock(ExternalResource.class);
            doReturn(Optional.ofNullable(policy)).when(resource).getPasswordPolicy();
            return resource;
        }

        private static ExternalResource resourceWithoutPolicy() {
            ExternalResource resource = mock(ExternalResource.class);
            doReturn(Optional.empty()).when(resource).getPasswordPolicy();
            return resource;
        }

        private static Realm realmWithPolicy(final PasswordPolicy policy) {
            Realm realm = mock(Realm.class);
            doReturn(Optional.ofNullable(policy)).when(realm).getPasswordPolicy();
            return realm;
        }

        private static Realm realmWithoutPolicy() {
            Realm realm = mock(Realm.class);
            doReturn(Optional.empty()).when(realm).getPasswordPolicy();
            return realm;
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

        private static long specialCount(final String value) {
            return value.chars().filter(ch -> !Character.isLetterOrDigit(ch)).count();
        }

        @Nested
        @DisplayName("generate(DefaultPasswordRuleConf)")
        class GenerateFromDefaultPasswordRuleConfTest {

            @Test
            @DisplayName("generates non-null password from empty configuration")
            void generateFromEmptyConfReturnsPassword() {
                String password = generator.generateFromRuleConf(new DefaultPasswordRuleConf());

                assertNotNull(password);
                assertFalse(password.isBlank());
            }

            @Test
            @DisplayName("uses default minimum length when configured minimum length is zero")
            void generateUsesDefaultMinimumLengthWhenMinLengthIsZero() {
                DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
                conf.setMinLength(0);
                conf.setMaxLength(0);

                String password = generator.generateFromRuleConf(conf);

                assertEquals(DefaultPasswordGenerator.MIN_LENGTH_IF_ZERO, password.length());
            }

            @Test
            @DisplayName("honors explicit minimum length")
            void generateHonorsExplicitMinimumLength() {
                DefaultPasswordRuleConf conf = conf(12, 64, 0, 0, 0, 0);

                String password = generator.generateFromRuleConf(conf);

                assertEquals(12, password.length());
            }

            @Test
            @DisplayName("supports very maximum length boundary")
            void generateSupportsVeryMaximumLengthBoundary() {
                DefaultPasswordRuleConf conf = conf(
                        DefaultPasswordGenerator.VERY_MAX_LENGTH,
                        DefaultPasswordGenerator.VERY_MAX_LENGTH,
                        1,
                        1,
                        1,
                        1);

                String password = generator.generateFromRuleConf(conf);

                assertEquals(DefaultPasswordGenerator.VERY_MAX_LENGTH, password.length());
            }

            @Test
            @DisplayName("generates uppercase characters when uppercase count is required")
            void generateSatisfiesUppercaseRequirement() {
                DefaultPasswordRuleConf conf = conf(10, 64, 3, 0, 0, 0);

                String password = generator.generateFromRuleConf(conf);

                assertTrue(UPPERCASE.matcher(password).matches(), password);
                assertTrue(uppercaseCount(password) >= 3, password);
            }

            @Test
            @DisplayName("generates lowercase characters when lowercase count is required")
            void generateSatisfiesLowercaseRequirement() {
                DefaultPasswordRuleConf conf = conf(10, 64, 0, 3, 0, 0);

                String password = generator.generateFromRuleConf(conf);

                assertTrue(LOWERCASE.matcher(password).matches(), password);
                assertTrue(lowercaseCount(password) >= 3, password);
            }

            @Test
            @DisplayName("generates numeric characters when digit count is required")
            void generateSatisfiesDigitRequirement() {
                DefaultPasswordRuleConf conf = conf(10, 64, 0, 0, 4, 0);

                String password = generator.generateFromRuleConf(conf);

                assertTrue(DIGIT.matcher(password).matches(), password);
                assertTrue(digitCount(password) >= 4, password);
            }

            @Test
            @DisplayName("generates special characters when special count is required")
            void generateSatisfiesSpecialRequirement() {
                DefaultPasswordRuleConf conf = conf(12, 64, 0, 0, 0, 4);

                String password = generator.generateFromRuleConf(conf);

                assertTrue(SPECIAL.matcher(password).matches(), password);
                assertTrue(specialCount(password) >= 4, password);
            }

            @Test
            @DisplayName("generates password satisfying mixed character requirements")
            void generateSatisfiesMixedRequirements() {
                DefaultPasswordRuleConf conf = conf(16, 64, 2, 2, 2, 2);

                String password = generator.generateFromRuleConf(conf);

                assertEquals(16, password.length());
                assertTrue(uppercaseCount(password) >= 2, password);
                assertTrue(lowercaseCount(password) >= 2, password);
                assertTrue(digitCount(password) >= 2, password);
                assertTrue(specialCount(password) >= 2, password);
            }

            @Test
            @DisplayName("throws when minimum length is greater than maximum length")
            void generateThrowsWhenMinimumLengthIsGreaterThanMaximumLength() {
                DefaultPasswordRuleConf conf = conf(12, 8, 0, 0, 0, 0);

                assertThrows(RuntimeException.class, () -> generator.generateFromRuleConf(conf));
            }

            @Test
            @DisplayName("throws when required character counts cannot fit generated password length")
            void generateThrowsWhenRequiredCountsCannotFitLength() {
                DefaultPasswordRuleConf conf = conf(4, 4, 2, 2, 2, 2);

                assertThrows(RuntimeException.class, () -> generator.generateFromRuleConf(conf));
            }

            @Test
            @DisplayName("throws when impossible requirements exceed VERY_MAX_LENGTH")
            void generateThrowsWhenRequirementsExceedMaximumBoundary() {
                DefaultPasswordRuleConf conf = conf(
                        DefaultPasswordGenerator.VERY_MAX_LENGTH,
                        DefaultPasswordGenerator.VERY_MAX_LENGTH,
                        40,
                        40,
                        40,
                        40);

                assertThrows(RuntimeException.class, () -> generator.generateFromRuleConf(conf));
            }

            @Test
            @DisplayName("does not deterministically return the same password for repeated generation")
            void generateProducesDifferentPasswordsOnRepeatedCalls() {
                DefaultPasswordRuleConf conf = conf(20, 64, 1, 1, 1, 1);

                String first = generator.generateFromRuleConf(conf);
                String second = generator.generateFromRuleConf(conf);

                assertNotEquals(first, second);
            }
        }

        @Nested
        @DisplayName("merge(List<DefaultPasswordRuleConf>)")
        class MergeTest {

            @Test
            @DisplayName("merge of empty configuration list returns default range")
            void mergeEmptyConfigurationList() {
                DefaultPasswordRuleConf merged = generator.mergeRuleConfs(List.of());

                assertEquals(DefaultPasswordGenerator.VERY_MIN_LENGTH, merged.getMinLength());
                assertEquals(DefaultPasswordGenerator.VERY_MAX_LENGTH, merged.getMaxLength());
                assertTrue(merged.isUsernameAllowed());
            }

            @Test
            @DisplayName("merge of single configuration copies scalar constraints")
            void mergeSingleConfigurationCopiesScalarConstraints() {
                DefaultPasswordRuleConf source = conf(10, 40, 1, 2, 3, 4);
                source.setAlphabetical(5);
                source.setRepeatSame(2);
                source.setUsernameAllowed(false);

                DefaultPasswordRuleConf merged = generator.mergeRuleConfs(List.of(source));

                assertEquals(10, merged.getMinLength());
                assertEquals(40, merged.getMaxLength());
                assertEquals(5, merged.getAlphabetical());
                assertEquals(1, merged.getUppercase());
                assertEquals(2, merged.getLowercase());
                assertEquals(3, merged.getDigit());
                assertEquals(4, merged.getSpecial());
                assertEquals(2, merged.getRepeatSame());
                assertFalse(merged.isUsernameAllowed());
            }

            @Test
            @DisplayName("merge uses highest minimum length")
            void mergeUsesHighestMinimumLength() {
                DefaultPasswordRuleConf first = conf(8, 64, 0, 0, 0, 0);
                DefaultPasswordRuleConf second = conf(18, 64, 0, 0, 0, 0);

                DefaultPasswordRuleConf merged = generator.mergeRuleConfs(List.of(first, second));

                assertEquals(18, merged.getMinLength());
            }

            @Test
            @DisplayName("merge uses lowest maximum length")
            void mergeUsesLowestMaximumLength() {
                DefaultPasswordRuleConf first = conf(8, 60, 0, 0, 0, 0);
                DefaultPasswordRuleConf second = conf(8, 30, 0, 0, 0, 0);

                DefaultPasswordRuleConf merged = generator.mergeRuleConfs(List.of(first, second));

                assertEquals(30, merged.getMaxLength());
            }

            @Test
            @DisplayName("merge uses strongest character requirements")
            void mergeUsesStrongestCharacterRequirements() {
                DefaultPasswordRuleConf first = conf(8, 64, 1, 4, 1, 2);
                first.setAlphabetical(3);

                DefaultPasswordRuleConf second = conf(12, 50, 3, 1, 5, 1);
                second.setAlphabetical(6);

                DefaultPasswordRuleConf merged = generator.mergeRuleConfs(List.of(first, second));

                assertEquals(3, merged.getUppercase());
                assertEquals(4, merged.getLowercase());
                assertEquals(5, merged.getDigit());
                assertEquals(2, merged.getSpecial());
                assertEquals(6, merged.getAlphabetical());
            }

            @Test
            @DisplayName("merge uses strictest repeatSame value when configured")
            void mergeUsesStrictestRepeatSameValue() {
                DefaultPasswordRuleConf first = new DefaultPasswordRuleConf();
                first.setRepeatSame(5);

                DefaultPasswordRuleConf second = new DefaultPasswordRuleConf();
                second.setRepeatSame(2);

                DefaultPasswordRuleConf merged = generator.mergeRuleConfs(List.of(first, second));

                assertEquals(2, merged.getRepeatSame());
            }

            @Test
            @DisplayName("merge preserves username disallowance when one policy forbids username")
            void mergePreservesUsernameDisallowance() {
                DefaultPasswordRuleConf first = new DefaultPasswordRuleConf();
                first.setUsernameAllowed(true);

                DefaultPasswordRuleConf second = new DefaultPasswordRuleConf();
                second.setUsernameAllowed(false);

                DefaultPasswordRuleConf merged = generator.mergeRuleConfs(List.of(first, second));

                assertFalse(merged.isUsernameAllowed());
            }

            @Test
            @DisplayName("merge accumulates list-based constraints")
            void mergeAccumulatesListBasedConstraints() {
                DefaultPasswordRuleConf first = new DefaultPasswordRuleConf();
                first.getWordsNotPermitted().add("password");
                first.getSchemasNotPermitted().add("username");

                DefaultPasswordRuleConf second = new DefaultPasswordRuleConf();
                second.getWordsNotPermitted().add("syncope");
                second.getSchemasNotPermitted().add("email");

                DefaultPasswordRuleConf merged = generator.mergeRuleConfs(List.of(first, second));

                assertTrue(merged.getWordsNotPermitted().containsAll(List.of("password", "syncope")));
                assertTrue(merged.getSchemasNotPermitted().containsAll(List.of("username", "email")));
            }

            @Test
            @DisplayName("merge returns a new configuration object")
            void mergeReturnsDistinctObject() {
                DefaultPasswordRuleConf first = conf(8, 64, 1, 1, 1, 1);
                DefaultPasswordRuleConf second = conf(10, 40, 2, 2, 2, 2);

                DefaultPasswordRuleConf merged = generator.mergeRuleConfs(List.of(first, second));

                assertNotSame(first, merged);
                assertNotSame(second, merged);
            }
        }

        @Nested
        @DisplayName("getPasswordRules(PasswordPolicy)")
        class GetPasswordRulesTest {

            @Test
            @DisplayName("returns cached password rule without rebuilding it")
            void getPasswordRulesReturnsCachedRule() {
                PasswordRule cachedRule = passwordRule(new DefaultPasswordRuleConf());
                generator.cacheRule("cachedRule", cachedRule);

                PasswordPolicy policy = policyWithRules(List.of(implementation("cachedRule")));

                List<PasswordRule> rules = generator.getRulesFromPolicy(policy);

                assertEquals(1, rules.size());
                assertSame(cachedRule, rules.get(0));
            }

            @Test
            @DisplayName("builds and caches password rule for new implementation")
            void getPasswordRulesBuildsAndCachesNewRule() {
                Implementation implementation = implementation("newRule");
                PasswordPolicy policy = policyWithRules(List.of(implementation));
                PasswordRule builtRule = passwordRule(new DefaultPasswordRuleConf());

                try (MockedStatic<ImplementationManager> implementationManager = mockStatic(ImplementationManager.class)) {
                    implementationManager
                            .when(() -> ImplementationManager.build(implementation))
                            .thenReturn(builtRule);

                    List<PasswordRule> rules = generator.getRulesFromPolicy(policy);

                    assertEquals(1, rules.size());
                    assertSame(builtRule, rules.get(0));
                    assertSame(builtRule, generator.cachedRule("newRule"));
                    implementationManager.verify(() -> ImplementationManager.build(implementation), times(1));
                }
            }

            @Test
            @DisplayName("reuses cached rule after first build")
            void getPasswordRulesReusesCachedRuleAfterFirstBuild() {
                Implementation implementation = implementation("repeatRule");
                PasswordPolicy policy = policyWithRules(List.of(implementation));
                PasswordRule builtRule = passwordRule(new DefaultPasswordRuleConf());

                try (MockedStatic<ImplementationManager> implementationManager = mockStatic(ImplementationManager.class)) {
                    implementationManager
                            .when(() -> ImplementationManager.build(implementation))
                            .thenReturn(builtRule);

                    List<PasswordRule> first = generator.getRulesFromPolicy(policy);
                    List<PasswordRule> second = generator.getRulesFromPolicy(policy);

                    assertEquals(1, first.size());
                    assertEquals(1, second.size());
                    assertSame(first.get(0), second.get(0));
                    implementationManager.verify(() -> ImplementationManager.build(implementation), times(1));
                }
            }

            @Test
            @DisplayName("handles policy with no implementations")
            void getPasswordRulesHandlesPolicyWithNoRules() {
                PasswordPolicy policy = policyWithRules(List.of());

                List<PasswordRule> rules = generator.getRulesFromPolicy(policy);

                assertNotNull(rules);
                assertTrue(rules.isEmpty());
            }

            @Test
            @DisplayName("does not cache failed implementation builds")
            void getPasswordRulesDoesNotCacheFailedBuild() {
                Implementation implementation = implementation("brokenRule");
                PasswordPolicy policy = policyWithRules(List.of(implementation));

                try (MockedStatic<ImplementationManager> implementationManager = mockStatic(ImplementationManager.class)) {
                    implementationManager
                            .when(() -> ImplementationManager.build(implementation))
                            .thenThrow(new IllegalStateException("boom"));

                    assertDoesNotThrow(() -> generator.getRulesFromPolicy(policy));
                    assertEquals(0, generator.cacheSize());
                }
            }

            @Test
            @DisplayName("throws NullPointerException for null policy")
            void getPasswordRulesThrowsForNullPolicy() {
                assertThrows(NullPointerException.class, () -> generator.getRulesFromPolicy(null));
            }
        }

        @Nested
        @DisplayName("generate(List<PasswordPolicy>)")
        class GenerateFromPoliciesTest {

            @Test
            @DisplayName("generates default password for empty policy list")
            void generateFromEmptyPolicyList() {
                String password = generator.generate(List.of());

                assertNotNull(password);
                assertEquals(DefaultPasswordGenerator.MIN_LENGTH_IF_ZERO, password.length());
            }

            @Test
            @DisplayName("generates password using DefaultPasswordRuleConf from policy")
            void generateUsesDefaultPasswordRuleConfFromPolicy() {
                DefaultPasswordRuleConf conf = conf(14, 64, 2, 2, 2, 2);
                PasswordRule rule = passwordRule(conf);
                PasswordPolicy policy = policyWithCachedRule(generator, "defaultRule", rule);

                String password = generator.generate(List.of(policy));

                assertEquals(14, password.length());
                assertTrue(uppercaseCount(password) >= 2, password);
                assertTrue(lowercaseCount(password) >= 2, password);
                assertTrue(digitCount(password) >= 2, password);
                assertTrue(specialCount(password) >= 2, password);
            }

            @Test
            @DisplayName("ignores non DefaultPasswordRuleConf rule configurations")
            void generateIgnoresNonDefaultPasswordRuleConf() {
                PasswordRuleConf customConf = mock(PasswordRuleConf.class);
                PasswordRule rule = passwordRule(customConf);
                PasswordPolicy policy = policyWithCachedRule(generator, "customRule", rule);

                String password = generator.generate(List.of(policy));

                assertNotNull(password);
                assertEquals(DefaultPasswordGenerator.MIN_LENGTH_IF_ZERO, password.length());
            }

            @Test
            @DisplayName("merges multiple policies before generating")
            void generateMergesMultiplePolicies() {
                DefaultPasswordRuleConf firstConf = conf(10, 64, 2, 0, 0, 0);
                DefaultPasswordRuleConf secondConf = conf(18, 30, 0, 2, 2, 2);

                PasswordRule firstRule = passwordRule(firstConf);
                PasswordRule secondRule = passwordRule(secondConf);

                PasswordPolicy firstPolicy = policyWithCachedRule(generator, "ruleOne", firstRule);
                PasswordPolicy secondPolicy = policyWithCachedRule(generator, "ruleTwo", secondRule);

                String password = generator.generate(List.of(firstPolicy, secondPolicy));

                assertEquals(18, password.length());
                assertTrue(uppercaseCount(password) >= 2, password);
                assertTrue(lowercaseCount(password) >= 2, password);
                assertTrue(digitCount(password) >= 2, password);
                assertTrue(specialCount(password) >= 2, password);
            }

            @Test
            @DisplayName("throws NullPointerException for null policy list")
            void generateThrowsForNullPolicyList() {
                assertThrows(NullPointerException.class, () -> generator.generate((List<PasswordPolicy>) null));
            }

            @Test
            @DisplayName("propagates impossible merged rule configuration")
            void generatePropagatesImpossibleMergedRuleConfiguration() {
                DefaultPasswordRuleConf impossible = conf(4, 4, 3, 3, 3, 3);
                PasswordRule rule = passwordRule(impossible);
                PasswordPolicy policy = policyWithCachedRule(generator, "impossibleRule", rule);

                assertThrows(RuntimeException.class, () -> generator.generate(List.of(policy)));
            }
        }

        @Nested
        @DisplayName("generate(ExternalResource, List<Realm>)")
        class GenerateFromResourceAndRealmsTest {

            @Test
            @DisplayName("uses resource password policy")
            void generateUsesResourcePasswordPolicy() {
                DefaultPasswordRuleConf conf = conf(13, 64, 1, 1, 1, 1);
                PasswordRule rule = passwordRule(conf);
                PasswordPolicy policy = policyWithCachedRule(generator, "resourceRule", rule);
                ExternalResource resource = resourceWithPolicy(policy);

                String password = generator.generate(resource, List.of());

                assertEquals(13, password.length());
                assertTrue(uppercaseCount(password) >= 1, password);
                assertTrue(lowercaseCount(password) >= 1, password);
                assertTrue(digitCount(password) >= 1, password);
                assertTrue(specialCount(password) >= 1, password);
            }

            @Test
            @DisplayName("uses realm password policies")
            void generateUsesRealmPasswordPolicies() {
                DefaultPasswordRuleConf firstConf = conf(9, 64, 1, 0, 0, 0);
                DefaultPasswordRuleConf secondConf = conf(15, 64, 0, 1, 1, 1);

                PasswordRule firstRule = passwordRule(firstConf);
                PasswordRule secondRule = passwordRule(secondConf);

                PasswordPolicy firstPolicy = policyWithCachedRule(generator, "realmRuleOne", firstRule);
                PasswordPolicy secondPolicy = policyWithCachedRule(generator, "realmRuleTwo", secondRule);

                Realm firstRealm = realmWithPolicy(firstPolicy);
                Realm secondRealm = realmWithPolicy(secondPolicy);

                String password = generator.generate(null, List.of(firstRealm, secondRealm));

                assertEquals(15, password.length());
                assertTrue(uppercaseCount(password) >= 1, password);
                assertTrue(lowercaseCount(password) >= 1, password);
                assertTrue(digitCount(password) >= 1, password);
                assertTrue(specialCount(password) >= 1, password);
            }

            @Test
            @DisplayName("combines resource and realm policies")
            void generateCombinesResourceAndRealmPolicies() {
                DefaultPasswordRuleConf resourceConf = conf(11, 64, 2, 0, 0, 0);
                DefaultPasswordRuleConf realmConf = conf(18, 32, 0, 2, 2, 2);

                PasswordRule resourceRule = passwordRule(resourceConf);
                PasswordRule realmRule = passwordRule(realmConf);

                PasswordPolicy resourcePolicy = policyWithCachedRule(generator, "resourcePolicyRule", resourceRule);
                PasswordPolicy realmPolicy = policyWithCachedRule(generator, "realmPolicyRule", realmRule);

                ExternalResource resource = resourceWithPolicy(resourcePolicy);
                Realm realm = realmWithPolicy(realmPolicy);

                String password = generator.generate(resource, List.of(realm));

                assertEquals(18, password.length());
                assertTrue(uppercaseCount(password) >= 2, password);
                assertTrue(lowercaseCount(password) >= 2, password);
                assertTrue(digitCount(password) >= 2, password);
                assertTrue(specialCount(password) >= 2, password);
            }

            @Test
            @DisplayName("skips resource and realm when password policies are absent")
            void generateSkipsAbsentResourceAndRealmPolicies() {
                ExternalResource resource = resourceWithoutPolicy();
                Realm realm = realmWithoutPolicy();

                String password = generator.generate(resource, List.of(realm));

                assertNotNull(password);
                assertEquals(DefaultPasswordGenerator.MIN_LENGTH_IF_ZERO, password.length());
            }

            @Test
            @DisplayName("handles null resource")
            void generateHandlesNullResource() {
                DefaultPasswordRuleConf conf = conf(12, 64, 1, 1, 1, 1);
                PasswordRule rule = passwordRule(conf);
                PasswordPolicy policy = policyWithCachedRule(generator, "realmOnlyRule", rule);

                String password = generator.generate(null, List.of(realmWithPolicy(policy)));

                assertEquals(12, password.length());
            }

            @Test
            @DisplayName("throws NullPointerException for null realm list")
            void generateThrowsForNullRealmList() {
                assertThrows(NullPointerException.class, () -> generator.generate(null, null));
            }

            @Test
            @DisplayName("invokes resource and realm password policy accessors")
            void generateInvokesAccessors() {
                ExternalResource resource = resourceWithoutPolicy();
                Realm realm = realmWithoutPolicy();

                generator.generate(resource, List.of(realm));

                verify(resource, times(1)).getPasswordPolicy();
                verify(realm, times(1)).getPasswordPolicy();
            }
        }

        @Nested
        @DisplayName("robustness and concurrency")
        class RobustnessAndConcurrencyTest {

            @Test
            @DisplayName("protected constants have expected security boundary values")
            void constantsHaveExpectedValues() {
                assertEquals(0, DefaultPasswordGenerator.VERY_MIN_LENGTH);
                assertEquals(64, DefaultPasswordGenerator.VERY_MAX_LENGTH);
                assertEquals(8, DefaultPasswordGenerator.MIN_LENGTH_IF_ZERO);
            }

            @Test
            @DisplayName("password rule cache supports concurrent cached reads")
            void cacheSupportsConcurrentCachedReads() {
                PasswordRule cachedRule = passwordRule(new DefaultPasswordRuleConf());
                generator.cacheRule("parallelRule", cachedRule);

                PasswordPolicy policy = policyWithRules(List.of(implementation("parallelRule")));
                List<PasswordRule> collected = new CopyOnWriteArrayList<>();

                assertTimeoutPreemptively(Duration.ofSeconds(3), () -> {
                    List<Thread> threads = new ArrayList<>();

                    for (int i = 0; i < 20; i++) {
                        threads.add(new Thread(() -> collected.addAll(generator.getRulesFromPolicy(policy))));
                    }

                    for (Thread thread : threads) {
                        thread.start();
                    }

                    for (Thread thread : threads) {
                        thread.join();
                    }
                });

                assertEquals(20, collected.size());
                collected.forEach(rule -> assertSame(cachedRule, rule));
            }

            @Test
            @DisplayName("generated password is String instance")
            void generatedPasswordIsStringInstance() {
                String password = generator.generateFromRuleConf(conf(8, 64, 1, 1, 1, 1));

                assertInstanceOf(String.class, password);
            }

            @Test
            @DisplayName("generate from policies can build uncached rule through ImplementationManager")
            void generateFromPoliciesBuildsUncachedRule() {
                Implementation implementation = implementation("buildDuringGenerate");
                PasswordPolicy policy = policyWithRules(List.of(implementation));
                PasswordRule rule = passwordRule(conf(12, 64, 1, 1, 1, 1));

                try (MockedStatic<ImplementationManager> implementationManager = mockStatic(ImplementationManager.class)) {
                    implementationManager
                            .when(() -> ImplementationManager.build(implementation))
                            .thenReturn(rule);

                    String password = generator.generate(List.of(policy));

                    assertEquals(12, password.length());
                    assertSame(rule, generator.cachedRule("buildDuringGenerate"));
                }
            }
        }
    }