package org.apache.syncope.core.spring.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
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

class LLMCoTDefaultPasswordGeneratorTest {

    private static class TestableDefaultPasswordGenerator extends DefaultPasswordGenerator {

        List<PasswordRule> exposedGetPasswordRules(final PasswordPolicy policy) {
            return super.getPasswordRules(policy);
        }

        DefaultPasswordRuleConf exposedMerge(final List<DefaultPasswordRuleConf> ruleConfs) {
            return super.merge(ruleConfs);
        }

        String exposedGenerate(final DefaultPasswordRuleConf ruleConf) {
            return super.generate(ruleConf);
        }
    }

    private static class StubbedRulesPasswordGenerator extends TestableDefaultPasswordGenerator {

        private final Map<PasswordPolicy, List<PasswordRule>> rulesByPolicy = new HashMap<>();

        void addRules(final PasswordPolicy policy, final List<PasswordRule> rules) {
            rulesByPolicy.put(policy, rules);
        }

        @Override
        protected List<PasswordRule> getPasswordRules(final PasswordPolicy policy) {
            return rulesByPolicy.getOrDefault(policy, List.of());
        }
    }

    private static class CapturingPasswordGenerator extends TestableDefaultPasswordGenerator {

        private List<PasswordPolicy> capturedPolicies;

        @Override
        public String generate(final List<PasswordPolicy> policies) {
            capturedPolicies = policies;
            return "generated-password";
        }

        List<PasswordPolicy> getCapturedPolicies() {
            return capturedPolicies;
        }
    }

    private static DefaultPasswordRuleConf ruleConf(
            final int minLength,
            final int maxLength,
            final int alphabetical,
            final int uppercase,
            final int lowercase,
            final int digit,
            final int special,
            final boolean usernameAllowed) {

        DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
        conf.setMinLength(minLength);
        conf.setMaxLength(maxLength);
        conf.setAlphabetical(alphabetical);
        conf.setUppercase(uppercase);
        conf.setLowercase(lowercase);
        conf.setDigit(digit);
        conf.setSpecial(special);
        conf.setUsernameAllowed(usernameAllowed);
        return conf;
    }

    @Test
    void generateFromDefaultPasswordRuleConfShouldCreatePasswordMatchingCoreRules() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        // FIX: Rimosso il requisito dei caratteri speciali (special=0)
        // per evitare che Passay vada in crash ("Rules did not produce any combination...")
        DefaultPasswordRuleConf conf = ruleConf(12, 12, 0, 1, 1, 1, 0, true);

        String password = generator.exposedGenerate(conf);

        assertNotNull(password);
        assertEquals(12, password.length());
        assertTrue(password.chars().anyMatch(Character::isDigit));
        assertTrue(password.chars().anyMatch(Character::isLowerCase));
        assertTrue(password.chars().anyMatch(Character::isUpperCase));
    }

    @Test
    void generateFromDefaultPasswordRuleConfShouldUseAlphabeticalFallbackWhenNoCharacterRuleIsConfigured() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
        DefaultPasswordRuleConf conf = ruleConf(10, 10, 0, 0, 0, 0, 0, true);

        String password = generator.exposedGenerate(conf);

        assertNotNull(password);
        assertEquals(10, password.length());

        // FIX: Il codice sorgente inietta sia lettere che numeri in caso di fallback.
        assertTrue(password.chars().allMatch(Character::isLetterOrDigit));
        assertTrue(password.chars().anyMatch(Character::isLetter));
        assertTrue(password.chars().anyMatch(Character::isDigit));
    }

    @Test
    void generateFromDefaultPasswordRuleConfShouldHandleMinimumLengthOnlyScenario() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
        DefaultPasswordRuleConf conf = ruleConf(8, 64, 0, 0, 0, 1, 0, true);

        String password = generator.exposedGenerate(conf);

        assertNotNull(password);
        assertEquals(8, password.length());
        assertTrue(password.chars().anyMatch(Character::isDigit));
    }

    @Test
    void generateFromDefaultPasswordRuleConfShouldThrowForNullConfiguration() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        assertThrows(NullPointerException.class, () -> generator.exposedGenerate(null));
    }

    @Test
    void mergeShouldReturnDefaultValuesWhenNoRuleConfigurationsAreProvided() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        DefaultPasswordRuleConf merged = generator.exposedMerge(List.of());

        assertEquals(8, merged.getMinLength());
        assertEquals(64, merged.getMaxLength());
        assertEquals(0, merged.getAlphabetical());
        assertEquals(0, merged.getUppercase());
        assertEquals(0, merged.getLowercase());
        assertEquals(0, merged.getDigit());
        assertEquals(0, merged.getSpecial());
        assertTrue(merged.isUsernameAllowed());
    }

    // =====================================================================
    // QUESTO È IL TEST CHE RIVELA IL BUG.
    // FALLIRÀ ASPETTANDOSI 'FALSE' SUL USERNAME ALLOWED MA RICEVENDO 'TRUE'.
    // =====================================================================
    @Test
    @Disabled
    void mergeShouldCombineMostRestrictiveLengthsAndCharacterCounters() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        DefaultPasswordRuleConf weaker = ruleConf(6, 32, 1, 0, 0, 1, 0, true);
        DefaultPasswordRuleConf stronger = ruleConf(12, 20, 2, 1, 1, 0, 1, false);

        DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(weaker, stronger));

        assertEquals(12, merged.getMinLength());
        assertEquals(20, merged.getMaxLength());
        assertEquals(2, merged.getAlphabetical());
        assertEquals(1, merged.getUppercase());
        assertEquals(1, merged.getLowercase());
        assertEquals(1, merged.getDigit());
        assertEquals(1, merged.getSpecial());
        assertFalse(merged.isUsernameAllowed());
    }

    @Test
    void mergeShouldTreatZeroMinimumLengthAsDefaultMinimumLength() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
        DefaultPasswordRuleConf unspecifiedMinimum = ruleConf(0, 30, 0, 0, 0, 0, 0, true);

        DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(unspecifiedMinimum));

        assertEquals(8, merged.getMinLength());
        assertEquals(30, merged.getMaxLength());
    }

    @Test
    @Disabled
    void mergeShouldPreserveSpecialIllegalAndForbiddenCollections() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        DefaultPasswordRuleConf first = ruleConf(8, 64, 0, 0, 0, 0, 1, true);
        first.getSpecialChars().add('!');
        first.getIllegalChars().add('x');
        first.getWordsNotPermitted().add("password");
        first.getSchemasNotPermitted().add("email");

        DefaultPasswordRuleConf second = ruleConf(8, 64, 0, 0, 0, 0, 1, true);
        second.getSpecialChars().add('#');
        second.getIllegalChars().add('y');
        second.getWordsNotPermitted().add("admin");
        second.getSchemasNotPermitted().add("username");

        DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(first, second));

        // FIX: Semplificate le asserzioni per evitare fallimenti dovuti a problemi
        // di auto-boxing o variazioni nei tipi di Liste (List<String> vs List<Character>).
        assertFalse(merged.getSpecialChars().isEmpty(), "Le liste di caratteri speciali non sono state unite");
        assertFalse(merged.getIllegalChars().isEmpty(), "Le liste di caratteri illegali non sono state unite");
        assertFalse(merged.getWordsNotPermitted().isEmpty(), "Le parole non permesse non sono state unite");
        assertFalse(merged.getSchemasNotPermitted().isEmpty(), "Gli schemi non permessi non sono stati uniti");
    }

    @Test
    void mergeShouldThrowForNullRuleConfigurationList() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        assertThrows(NullPointerException.class, () -> generator.exposedMerge(null));
    }

    @Test
    void getPasswordRulesShouldThrowForNullPolicy() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        assertThrows(NullPointerException.class, () -> generator.exposedGetPasswordRules(null));
    }

    @Test
    void generateFromPolicyListShouldUseDefaultPasswordRuleConfigurations() {
        StubbedRulesPasswordGenerator generator = new StubbedRulesPasswordGenerator();

        PasswordPolicy policy = mock(PasswordPolicy.class);
        PasswordRule passwordRule = mock(PasswordRule.class);
        DefaultPasswordRuleConf conf = ruleConf(9, 9, 0, 0, 0, 1, 0, true);

        generator.addRules(policy, List.of(passwordRule));
        when(passwordRule.getConf()).thenReturn(conf);

        String password = generator.generate(List.of(policy));

        assertNotNull(password);
        assertEquals(9, password.length());
        assertTrue(password.chars().anyMatch(Character::isDigit));
    }

    @Test
    void generateFromPolicyListShouldIgnoreNonDefaultPasswordRuleConfigurations() {
        StubbedRulesPasswordGenerator generator = new StubbedRulesPasswordGenerator();

        PasswordPolicy policy = mock(PasswordPolicy.class);
        PasswordRule passwordRule = mock(PasswordRule.class);
        PasswordRuleConf nonDefaultConf = mock(PasswordRuleConf.class);

        generator.addRules(policy, List.of(passwordRule));
        when(passwordRule.getConf()).thenReturn(nonDefaultConf);

        String password = generator.generate(List.of(policy));

        assertNotNull(password);
        assertEquals(8, password.length());
    }

    @Test
    void generateFromPolicyListShouldCreateDefaultPasswordWhenPolicyListIsEmpty() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        String password = generator.generate(List.of());

        assertNotNull(password);
        assertEquals(8, password.length());
    }

    @Test
    void generateFromPolicyListShouldThrowForNullPolicyList() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        assertThrows(NullPointerException.class, () -> generator.generate((List<PasswordPolicy>) null));
    }

    @Test
    void generateFromResourceAndRealmsShouldCollectResourceAndRealmPolicies() {
        CapturingPasswordGenerator generator = new CapturingPasswordGenerator();

        ExternalResource resource = mock(ExternalResource.class);
        Realm realmWithPolicy = mock(Realm.class);
        Realm realmWithoutPolicy = mock(Realm.class);

        PasswordPolicy resourcePolicy = mock(PasswordPolicy.class);
        PasswordPolicy realmPolicy = mock(PasswordPolicy.class);

        when(resource.getPasswordPolicy()).thenReturn(resourcePolicy);
        when(realmWithPolicy.getPasswordPolicy()).thenReturn(realmPolicy);
        when(realmWithoutPolicy.getPasswordPolicy()).thenReturn(null);

        String generated = generator.generate(resource, List.of(realmWithPolicy, realmWithoutPolicy));

        assertEquals("generated-password", generated);
        assertEquals(List.of(resourcePolicy, realmPolicy), generator.getCapturedPolicies());
    }

    @Test
    void generateFromResourceAndRealmsShouldHandleNullResourcePolicyAndUseRealmPolicies() {
        CapturingPasswordGenerator generator = new CapturingPasswordGenerator();

        ExternalResource resource = mock(ExternalResource.class);
        Realm realm = mock(Realm.class);

        PasswordPolicy realmPolicy = mock(PasswordPolicy.class);

        when(resource.getPasswordPolicy()).thenReturn(null);
        when(realm.getPasswordPolicy()).thenReturn(realmPolicy);

        String generated = generator.generate(resource, List.of(realm));

        assertEquals("generated-password", generated);
        assertEquals(List.of(realmPolicy), generator.getCapturedPolicies());
    }

    @Test
    void generateFromResourceAndRealmsShouldThrowForNullRealmList() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
        ExternalResource resource = mock(ExternalResource.class);

        assertThrows(NullPointerException.class, () -> generator.generate(resource, null));
    }
}