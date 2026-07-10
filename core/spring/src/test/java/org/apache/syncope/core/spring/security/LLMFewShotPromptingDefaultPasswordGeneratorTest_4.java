package org.apache.syncope.core.spring.security;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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

class LLMFewShotPromptingDefaultPasswordGeneratorTest_4 {

    /**
     * Unit tests for {@link DefaultPasswordGenerator}.
     *
     * <p>The test class is placed in the same package as the production class in order to test protected methods
     * through a small test-specific subclass.</p>
     */

    /**
    * Test-specific generator that exposes protected methods and allows deterministic policy-to-rule mapping.
    */
        private static class TestableDefaultPasswordGenerator extends DefaultPasswordGenerator {

            private final Map<PasswordPolicy, List<PasswordRule>> rulesByPolicy = new HashMap<>();

            void registerRules(final PasswordPolicy policy, final List<PasswordRule> rules) {
                rulesByPolicy.put(policy, rules);
            }

            DefaultPasswordRuleConf exposeMerge(final List<DefaultPasswordRuleConf> ruleConfs) {
                return super.merge(ruleConfs);
            }

            String exposeGenerate(final DefaultPasswordRuleConf ruleConf) {
                return super.generate(ruleConf);
            }

            List<PasswordRule> exposeGetPasswordRules(final PasswordPolicy policy) {
                return super.getPasswordRules(policy);
            }

            @Override
            protected List<PasswordRule> getPasswordRules(final PasswordPolicy policy) {
                return rulesByPolicy.getOrDefault(policy, Collections.emptyList());
            }
        }

        private static DefaultPasswordRuleConf ruleConf(final int minLength, final int maxLength) {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(minLength);
            conf.setMaxLength(maxLength);
            return conf;
        }

        private static PasswordRule passwordRule(final PasswordRuleConf conf) {
            PasswordRule rule = mock(PasswordRule.class);
            when(rule.getConf()).thenReturn(conf);
            return rule;
        }

        private static PasswordPolicy passwordPolicy() {
            return mock(PasswordPolicy.class);
        }

        // ==================== TC1: Generation with single policy ====================
        // Tests that a password is generated correctly when a single password policy provides one valid default rule
        @Test
        void testGenerate_WithSinglePolicy() {
            // Arrange: create one policy and one default password rule configuration
            PasswordPolicy policy = passwordPolicy();
            DefaultPasswordRuleConf conf = ruleConf(8, 16);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.registerRules(policy, Collections.singletonList(passwordRule(conf)));

            // Act: generate the password from the configured policy
            String password = generator.generate(Collections.singletonList(policy));

            // Assert: generated password is not null and respects the expected merged size range
            boolean condition = password != null && password.length() >= 8 && password.length() <= 16;
            assertTrue(condition);
        }

        // ==================== TC2: Generation with no policies ====================
        // Tests that the generator falls back to the default minimum length when no policy is provided
        @Test
        void testGenerate_WithNoPolicies_UsesDefaultMinimumLength() {
            // Arrange: create the generator without registering any policy-specific rule
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            // Act: generate a password from an empty policy list
            String password = generator.generate(Collections.emptyList());

            // Assert: generated password is not null and uses the default minimum length of 8
            assertNotNull(password);
            assertEquals(8, password.length());
        }

        // ==================== TC3: Generation ignores non-default rule configurations ====================
        // Tests that only DefaultPasswordRuleConf is considered, as documented by the production class
        @Test
        void testGenerate_WithNonDefaultRuleConf_IgnoresRuleAndUsesDefaults() {
            // Arrange: create a PasswordRuleConf that is not an instance of DefaultPasswordRuleConf
            PasswordRuleConf customConf = mock(PasswordRuleConf.class);
            PasswordPolicy policy = passwordPolicy();

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.registerRules(policy, Collections.singletonList(passwordRule(customConf)));

            // Act: generate the password from a policy containing only a non-default password rule configuration
            String password = generator.generate(Collections.singletonList(policy));

            // Assert: the non-default rule is ignored and the default minimum generated length is used
            assertNotNull(password);
            assertEquals(8, password.length());
        }

        // ==================== TC4: Generation with multiple policies ====================
        // Tests that several policies are merged before password generation
        @Test
        void testGenerate_WithMultiplePolicies_MergesDefaultRules() {
            // Arrange: create two policies with compatible length constraints
            PasswordPolicy policyOne = passwordPolicy();
            PasswordPolicy policyTwo = passwordPolicy();

            DefaultPasswordRuleConf confOne = ruleConf(8, 32);
            DefaultPasswordRuleConf confTwo = ruleConf(12, 20);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.registerRules(policyOne, Collections.singletonList(passwordRule(confOne)));
            generator.registerRules(policyTwo, Collections.singletonList(passwordRule(confTwo)));

            // Act: generate a password from both policies
            String password = generator.generate(List.of(policyOne, policyTwo));

            // Assert: the stricter minimum length from the merged rules is respected
            assertNotNull(password);
            assertTrue(password.length() >= 12);
            assertTrue(password.length() <= 20);
        }

        // ==================== TC5: Generation through resource and realms ====================
        // Tests the public generate(resource, realms) method using resource and realm password policies
        @Test
        void testGenerate_WithResourceAndRealms_CombinesAllPolicies() {
            // Arrange: create resource, realm, and policies with compatible stricter constraints
            ExternalResource resource = mock(ExternalResource.class);
            Realm realm = mock(Realm.class);

            PasswordPolicy resourcePolicy = passwordPolicy();
            PasswordPolicy realmPolicy = passwordPolicy();

            when(resource.getPasswordPolicy()).thenReturn(resourcePolicy);
            when(realm.getPasswordPolicy()).thenReturn(realmPolicy);

            DefaultPasswordRuleConf resourceConf = ruleConf(10, 64);
            DefaultPasswordRuleConf realmConf = ruleConf(14, 24);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.registerRules(resourcePolicy, Collections.singletonList(passwordRule(resourceConf)));
            generator.registerRules(realmPolicy, Collections.singletonList(passwordRule(realmConf)));

            // Act: generate the password from the resource and realm chain
            String password = generator.generate(resource, Collections.singletonList(realm));

            // Assert: the generated password respects the merged constraints from both contexts
            assertNotNull(password);
            assertTrue(password.length() >= 14);
            assertTrue(password.length() <= 24);
        }

        // ==================== TC6: Generation through resource without resource policy ====================
        // Tests that generate(resource, realms) safely handles a null resource password policy
        @Test
        void testGenerate_WithNullResourcePolicy_UsesRealmPolicyOnly() {
            // Arrange: create a resource without policy and one realm with a valid policy
            ExternalResource resource = mock(ExternalResource.class);
            Realm realm = mock(Realm.class);

            PasswordPolicy realmPolicy = passwordPolicy();

            when(resource.getPasswordPolicy()).thenReturn(null);
            when(realm.getPasswordPolicy()).thenReturn(realmPolicy);

            DefaultPasswordRuleConf realmConf = ruleConf(11, 18);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.registerRules(realmPolicy, Collections.singletonList(passwordRule(realmConf)));

            // Act: generate the password from a null resource policy and one realm policy
            String password = generator.generate(resource, Collections.singletonList(realm));

            // Assert: the realm policy is still applied correctly
            assertNotNull(password);
            assertTrue(password.length() >= 11);
            assertTrue(password.length() <= 18);
        }

        // ==================== TC7: Generation through realms with null realm policy ====================
        // Tests that generate(resource, realms) safely ignores realms that do not define a password policy
        @Test
        void testGenerate_WithNullRealmPolicy_UsesResourcePolicyOnly() {
            // Arrange: create one resource policy and a realm without policy
            ExternalResource resource = mock(ExternalResource.class);
            Realm realm = mock(Realm.class);

            PasswordPolicy resourcePolicy = passwordPolicy();

            when(resource.getPasswordPolicy()).thenReturn(resourcePolicy);
            when(realm.getPasswordPolicy()).thenReturn(null);

            DefaultPasswordRuleConf resourceConf = ruleConf(9, 15);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            generator.registerRules(resourcePolicy, Collections.singletonList(passwordRule(resourceConf)));

            // Act: generate the password from one resource policy and one null realm policy
            String password = generator.generate(resource, Collections.singletonList(realm));

            // Assert: the resource policy is applied and the null realm policy is ignored
            assertNotNull(password);
            assertTrue(password.length() >= 9);
            assertTrue(password.length() <= 15);
        }

        // ==================== TC8: Protected merge with empty rules ====================
        // Tests that merge returns the production defaults when no DefaultPasswordRuleConf is available
        @Test
        void testMerge_WithEmptyRuleConfs_ReturnsDefaultRuleConf() {
            // Arrange: create a generator and an empty list of rule configurations
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            // Act: merge an empty configuration list
            DefaultPasswordRuleConf merged = generator.exposeMerge(Collections.emptyList());

            // Assert: default min/max length and username setting are applied
            assertEquals(0, merged.getMinLength());
            assertEquals(64, merged.getMaxLength());
            assertTrue(merged.isUsernameAllowed());
        }

        // ==================== TC9: Protected merge chooses strictest length range ====================
        // Tests that merge keeps the highest minimum length and the lowest maximum length
        @Test
        void testMerge_WithMultipleRuleConfs_UsesStrictestLengths() {
            // Arrange: create several rule configurations with different min and max values
            DefaultPasswordRuleConf weak = ruleConf(6, 64);
            DefaultPasswordRuleConf strictMin = ruleConf(14, 40);
            DefaultPasswordRuleConf strictMax = ruleConf(8, 20);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            // Act: merge all configurations
            DefaultPasswordRuleConf merged = generator.exposeMerge(List.of(weak, strictMin, strictMax));

            // Assert: the merged result uses the highest minimum and lowest maximum
            assertEquals(14, merged.getMinLength());
            assertEquals(20, merged.getMaxLength());
        }

        // ==================== TC10: Protected merge aggregates character requirements ====================
        // Tests that merge preserves the strictest character class requirements across rule configurations
        @Test
        void testMerge_WithCharacterRequirements_UsesMaximumRequiredCounts() {
            // Arrange: create independent rules that require different numbers of character classes
            DefaultPasswordRuleConf confOne = ruleConf(8, 32);
            confOne.setUppercase(1);
            confOne.setLowercase(2);
            confOne.setDigit(1);
            confOne.setSpecial(0);
            confOne.setAlphabetical(3);

            DefaultPasswordRuleConf confTwo = ruleConf(10, 24);
            confTwo.setUppercase(3);
            confTwo.setLowercase(1);
            confTwo.setDigit(2);
            confTwo.setSpecial(2);
            confTwo.setAlphabetical(1);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            // Act: merge both configurations
            DefaultPasswordRuleConf merged = generator.exposeMerge(List.of(confOne, confTwo));

            // Assert: each numeric requirement keeps the stricter value
            assertEquals(3, merged.getUppercase());
            assertEquals(2, merged.getLowercase());
            assertEquals(2, merged.getDigit());
            assertEquals(2, merged.getSpecial());
            assertEquals(3, merged.getAlphabetical());
        }

        // ==================== TC11: Protected merge keeps username disallowed if any rule disallows it ====================
        // Tests that usernameAllowed becomes false when at least one merged configuration forbids usernames in passwords
        @Test
        void testMerge_WhenAnyRuleDisallowsUsername_ResultDisallowsUsername() {
            // Arrange: create one permissive and one restrictive rule
            DefaultPasswordRuleConf permissive = ruleConf(8, 32);
            permissive.setUsernameAllowed(true);

            DefaultPasswordRuleConf restrictive = ruleConf(8, 32);
            restrictive.setUsernameAllowed(false);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            // Act: merge the two rules
            DefaultPasswordRuleConf merged = generator.exposeMerge(List.of(permissive, restrictive));

            // Assert: the merged rule is restrictive and does not allow username usage
            assertFalse(merged.isUsernameAllowed());
        }

        // ==================== TC12: Protected generate with direct rule configuration ====================
        // Tests protected generate(DefaultPasswordRuleConf) directly using minimum and maximum lengths
        @Test
        void testProtectedGenerate_WithDirectRuleConf_ReturnsPasswordWithinLengthRange() {
            // Arrange: create a direct rule configuration
            DefaultPasswordRuleConf conf = ruleConf(13, 22);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            // Act: generate directly from the rule configuration
            String password = generator.exposeGenerate(conf);

            // Assert: generated password is not null and respects the requested length constraints
            assertNotNull(password);
            assertTrue(password.length() >= 13);
            assertTrue(password.length() <= 22);
        }

        // ==================== TC13: Protected generate with zero minimum length ====================
        // Tests that protected generate(DefaultPasswordRuleConf) replaces zero minimum length with the configured default
        @Test
        void testProtectedGenerate_WithZeroMinimumLength_UsesDefaultMinimumLength() {
            // Arrange: create a rule with zero minimum length
            DefaultPasswordRuleConf conf = ruleConf(0, 64);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            // Act: generate directly from the rule configuration
            String password = generator.exposeGenerate(conf);

            // Assert: the generator uses the default minimum length of 8
            assertNotNull(password);
            assertEquals(8, password.length());
        }

        // ==================== TC14: Protected generate with uppercase requirement ====================
        // Tests that protected generation can satisfy an uppercase character requirement
        @Test
        void testProtectedGenerate_WithUppercaseRequirement_ContainsUppercaseCharacter() {
            // Arrange: create a rule that requires at least two uppercase characters
            DefaultPasswordRuleConf conf = ruleConf(12, 20);
            conf.setUppercase(2);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            // Act: generate directly from the rule configuration
            String password = generator.exposeGenerate(conf);

            // Assert: the generated password contains at least two uppercase characters
            long uppercaseCount = password.chars().filter(Character::isUpperCase).count();
            assertTrue(uppercaseCount >= 2);
        }

        // ==================== TC15: Protected generate with digit requirement ====================
        // Tests that protected generation can satisfy a digit requirement
        @Test
        void testProtectedGenerate_WithDigitRequirement_ContainsDigit() {
            // Arrange: create a rule that requires at least three digits
            DefaultPasswordRuleConf conf = ruleConf(12, 20);
            conf.setDigit(3);

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            // Act: generate directly from the rule configuration
            String password = generator.exposeGenerate(conf);

            // Assert: the generated password contains at least three numeric characters
            long digitCount = password.chars().filter(Character::isDigit).count();
            assertTrue(digitCount >= 3);
        }

        // ==================== TC16: Protected getPasswordRules with empty policy rules ====================
        // Tests the protected getPasswordRules method for a policy that has no implementations configured
        @Test
        void testGetPasswordRules_WithEmptyPolicyRules_ReturnsEmptyList() {
            // Arrange: create a real call path for getPasswordRules with an empty implementation list
            PasswordPolicy policy = mock(PasswordPolicy.class);
            when(policy.getRules()).thenReturn(new ArrayList<>());

            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            // Act: call the protected method through the test subclass
            List<PasswordRule> rules = generator.exposeGetPasswordRules(policy);

            // Assert: no implementation means no password rules are returned
            assertNotNull(rules);
            assertTrue(rules.isEmpty());
        }
    }
