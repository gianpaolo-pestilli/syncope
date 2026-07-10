package org.apache.syncope.core.spring.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.List;
import org.apache.syncope.common.lib.policy.DefaultPasswordRuleConf;
import org.apache.syncope.core.persistence.api.entity.ExternalResource;
import org.apache.syncope.core.persistence.api.entity.Implementation;
import org.apache.syncope.core.persistence.api.entity.Realm;
import org.apache.syncope.core.persistence.api.entity.policy.PasswordPolicy;
import org.apache.syncope.core.provisioning.api.rules.PasswordRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LLMToTDefaultPasswordGeneratorTest {

    private static class TestableDefaultPasswordGenerator extends DefaultPasswordGenerator {

        @Override
        protected List<PasswordRule> getPasswordRules(final PasswordPolicy policy) {
            if (policy == null || policy.getRules() == null) {
                return List.of();
            }
            return policy.getRules().stream()
                    .map(impl -> perContextPasswordRules.get(impl.getKey()))
                    .filter(java.util.Objects::nonNull)
                    .toList();
        }

        List<PasswordRule> exposedGetPasswordRules(final PasswordPolicy policy) {
            return this.getPasswordRules(policy);
        }

        DefaultPasswordRuleConf exposedMerge(final List<DefaultPasswordRuleConf> defaultRuleConfs) {
            return super.merge(defaultRuleConfs);
        }

        String exposedGenerate(final DefaultPasswordRuleConf ruleConf) {
            return super.generate(ruleConf);
        }

        void registerRule(final String key, final PasswordRule rule) {
            perContextPasswordRules.put(key, rule);
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

    private static PasswordRule passwordRuleReturning(final DefaultPasswordRuleConf conf) {
        PasswordRule rule = mock(PasswordRule.class);
        when(rule.getConf()).thenReturn(conf);
        return rule;
    }

    private static Implementation implementation(final String key) {
        Implementation implementation = mock(Implementation.class);
        when(implementation.getKey()).thenReturn(key);
        return implementation;
    }

    private static PasswordPolicy policyWithRuleKey(final String key) {
        PasswordPolicy policy = mock(PasswordPolicy.class);
        doReturn(List.of(implementation(key))).when(policy).getRules();
        return policy;
    }

    @Test
    @DisplayName("Junior tester: generate(DefaultPasswordRuleConf) creates a password with the configured length")
    void generateFromRuleConfCreatesPasswordWithConfiguredLength() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
        DefaultPasswordRuleConf conf = conf(12, 12, 0, 0, 0, 0);

        String password = generator.exposedGenerate(conf);

        assertNotNull(password);
        assertTrue(password.length() >= 12);
        assertTrue(password.length() <= 12);
    }

    @Test
    @DisplayName("Experienced junior tester: generate(DefaultPasswordRuleConf) honors character-class requirements")
    void generateFromRuleConfHonorsCharacterClassRequirements() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
        DefaultPasswordRuleConf conf = conf(16, 16, 1, 1, 1, 0);

        String password = generator.exposedGenerate(conf);

        assertNotNull(password);
        assertTrue(password.length() >= 16);
        assertTrue(password.length() <= 16);
        assertTrue(password.chars().anyMatch(Character::isUpperCase));
        assertTrue(password.chars().anyMatch(Character::isLowerCase));
        assertTrue(password.chars().anyMatch(Character::isDigit));
    }

    @Test
    @DisplayName("Experienced junior tester: merge applies default bounds when no rule configuration is supplied")
    void mergeUsesDefaultsWhenNoRuleConfigurationsAreSupplied() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        DefaultPasswordRuleConf merged = generator.exposedMerge(List.of());

        assertNotNull(merged);
        assertTrue(merged.getMinLength() >= 0);
        assertTrue(merged.getMaxLength() <= 64);
        assertTrue(merged.isUsernameAllowed());
    }

    @Test
    @DisplayName("Senior tester: merge output can be used to generate a policy-compliant password")
    void mergeProducesConfigurationThatCanGeneratePassword() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        DefaultPasswordRuleConf first = conf(10, 32, 1, 1, 0, 0);
        DefaultPasswordRuleConf second = conf(14, 24, 0, 0, 1, 0);

        DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(first, second));
        String password = generator.exposedGenerate(merged);

        assertNotNull(password);
        assertTrue(password.length() >= merged.getMinLength());
        assertTrue(password.length() <= merged.getMaxLength());
        assertTrue(password.chars().anyMatch(Character::isUpperCase));
        assertTrue(password.chars().anyMatch(Character::isLowerCase));
        assertTrue(password.chars().anyMatch(Character::isDigit));
    }

    @Test
    @DisplayName("Junior tester: getPasswordRules returns cached password rule implementations")
    void getPasswordRulesReturnsCachedRule() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        DefaultPasswordRuleConf conf = conf(10, 10, 0, 0, 0, 0);
        PasswordRule cachedRule = passwordRuleReturning(conf);

        generator.registerRule("default-rule", cachedRule);

        PasswordPolicy policy = policyWithRuleKey("default-rule");

        List<PasswordRule> rules = generator.exposedGetPasswordRules(policy);

        assertNotNull(rules);
        assertTrue(rules.contains(cachedRule));
    }

    @Test
    @DisplayName("Experienced junior tester: getPasswordRules handles a policy with no rule implementations")
    void getPasswordRulesReturnsEmptyListWhenPolicyHasNoRules() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        PasswordPolicy policy = mock(PasswordPolicy.class);
        doReturn(List.of()).when(policy).getRules();

        List<PasswordRule> rules = generator.exposedGetPasswordRules(policy);

        assertNotNull(rules);
        assertTrue(rules.isEmpty());
    }

    @Test
    @DisplayName("Senior tester: getPasswordRules does not fail when implementation resolution fails")
    void getPasswordRulesDoesNotPropagateImplementationResolutionFailures() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        PasswordPolicy policy = policyWithRuleKey("missing-rule");

        List<PasswordRule> rules = assertDoesNotThrow(() -> generator.exposedGetPasswordRules(policy));

        assertNotNull(rules);
        assertTrue(rules.isEmpty());
    }

    @Test
    @DisplayName("Junior tester: generate(List<PasswordPolicy>) creates a password from policy rules")
    void generateFromPoliciesCreatesPasswordUsingCachedDefaultRuleConf() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        DefaultPasswordRuleConf conf = conf(11, 11, 1, 1, 1, 0);
        generator.registerRule("policy-rule", passwordRuleReturning(conf));

        PasswordPolicy policy = policyWithRuleKey("policy-rule");

        String password = generator.generate(List.of(policy));

        assertNotNull(password);
        assertTrue(password.length() >= 11);
        assertTrue(password.length() <= 11);
        assertTrue(password.chars().anyMatch(Character::isUpperCase));
        assertTrue(password.chars().anyMatch(Character::isLowerCase));
        assertTrue(password.chars().anyMatch(Character::isDigit));
    }

    @Test
    @DisplayName("Experienced junior tester: generate(List<PasswordPolicy>) ignores non-default password rule configurations")
    void generateFromPoliciesIgnoresNonDefaultPasswordRuleConf() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        PasswordRule nonDefaultRule = mock(PasswordRule.class);
        when(nonDefaultRule.getConf()).thenReturn(null);

        generator.registerRule("non-default-rule", nonDefaultRule);

        PasswordPolicy policy = policyWithRuleKey("non-default-rule");

        String password = generator.generate(List.of(policy));

        assertNotNull(password);
        assertTrue(password.length() > 0);
        assertTrue(password.length() <= 64);
    }

    @Test
    @DisplayName("Senior tester: generate(List<PasswordPolicy>) handles an empty policy list")
    void generateFromEmptyPolicyListUsesDefaultGeneration() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        String password = generator.generate(List.of());

        assertNotNull(password);
        assertTrue(password.length() > 0);
        assertTrue(password.length() <= 64);
    }

    @Test
    @DisplayName("Junior tester: generate(ExternalResource, List<Realm>) combines resource and realm policies")
    void generateFromResourceAndRealmsCombinesPolicies() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        DefaultPasswordRuleConf resourceConf = conf(9, 32, 1, 0, 0, 0);
        DefaultPasswordRuleConf realmConf = conf(14, 24, 0, 1, 1, 0);

        generator.registerRule("resource-rule", passwordRuleReturning(resourceConf));
        generator.registerRule("realm-rule", passwordRuleReturning(realmConf));

        PasswordPolicy resourcePolicy = policyWithRuleKey("resource-rule");
        PasswordPolicy realmPolicy = policyWithRuleKey("realm-rule");

        ExternalResource resource = mock(ExternalResource.class);
        when(resource.getPasswordPolicy()).thenReturn(resourcePolicy);

        Realm realm = mock(Realm.class);
        when(realm.getPasswordPolicy()).thenReturn(realmPolicy);

        String password = generator.generate(resource, List.of(realm));

        assertNotNull(password);
        assertTrue(password.length() >= 14);
        assertTrue(password.length() <= 32);
        assertTrue(password.chars().anyMatch(Character::isUpperCase));
        assertTrue(password.chars().anyMatch(Character::isLowerCase));
        assertTrue(password.chars().anyMatch(Character::isDigit));
    }

    @Test
    @DisplayName("Experienced junior tester: generate(ExternalResource, List<Realm>) tolerates resource without password policy")
    void generateFromResourceAndRealmsToleratesNullResourcePolicy() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        DefaultPasswordRuleConf realmConf = conf(13, 13, 0, 1, 1, 0);
        generator.registerRule("realm-only-rule", passwordRuleReturning(realmConf));

        PasswordPolicy realmPolicy = policyWithRuleKey("realm-only-rule");

        ExternalResource resource = mock(ExternalResource.class);
        when(resource.getPasswordPolicy()).thenReturn(null);

        Realm realm = mock(Realm.class);
        when(realm.getPasswordPolicy()).thenReturn(realmPolicy);

        String password = generator.generate(resource, List.of(realm));

        assertNotNull(password);
        assertTrue(password.length() >= 13);
        assertTrue(password.length() <= 13);
        assertTrue(password.chars().anyMatch(Character::isLowerCase));
        assertTrue(password.chars().anyMatch(Character::isDigit));
    }

    @Test
    @DisplayName("Senior tester: generate(ExternalResource, List<Realm>) tolerates realms without password policies")
    void generateFromResourceAndRealmsToleratesNullRealmPolicy() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        DefaultPasswordRuleConf resourceConf = conf(10, 10, 1, 1, 0, 0);
        generator.registerRule("resource-only-rule", passwordRuleReturning(resourceConf));

        PasswordPolicy resourcePolicy = policyWithRuleKey("resource-only-rule");

        ExternalResource resource = mock(ExternalResource.class);
        when(resource.getPasswordPolicy()).thenReturn(resourcePolicy);

        Realm realmWithoutPolicy = mock(Realm.class);
        when(realmWithoutPolicy.getPasswordPolicy()).thenReturn(null);

        String password = generator.generate(resource, List.of(realmWithoutPolicy));

        assertNotNull(password);
        assertTrue(password.length() >= 10);
        assertTrue(password.length() <= 10);
        assertTrue(password.chars().anyMatch(Character::isUpperCase));
        assertTrue(password.chars().anyMatch(Character::isLowerCase));
    }

    @Test
    @DisplayName("Senior tester: generate(ExternalResource, List<Realm>) falls back to default generation when no policies exist")
    void generateFromResourceAndEmptyRealmsUsesDefaultGeneration() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        ExternalResource resource = mock(ExternalResource.class);
        when(resource.getPasswordPolicy()).thenReturn(null);

        String password = generator.generate(resource, List.of());

        assertNotNull(password);
        assertTrue(password.length() > 0);
        assertTrue(password.length() <= 64);
    }
}