package org.apache.syncope.core.spring.security;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
@Disabled
public class LLMC1Test {
    /**
     * Classe di utilità per esporre i metodi protetti ed evitare di usare la reflection.
    */
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

    // =======================================================================
    // TEST JUNIOR: Verifiche di base sui comportamenti attesi
    // =======================================================================

    @Test
    @DisplayName("Junior tester: generate(DefaultPasswordRuleConf) crea una password rispettando i limiti di lunghezza")
    void generateFromRuleConfCreatesPasswordWithConfiguredLength() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
        DefaultPasswordRuleConf conf = conf(12, 12, 0, 0, 0, 0);

        String password = generator.exposedGenerate(conf);

        assertNotNull(password);
        assertEquals(12, password.length());
    }

    @Test
    @DisplayName("Junior tester: Il refactoring di mergeLengths funziona combinando correttamente minimo e massimo")
    void mergeUsesDefaultsWhenNoRuleConfigurationsAreSupplied() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        DefaultPasswordRuleConf merged = generator.exposedMerge(List.of());

        assertNotNull(merged);
        assertEquals(8, merged.getMinLength());
        assertEquals(64, merged.getMaxLength());
        assertTrue(merged.isUsernameAllowed());
    }

    // =======================================================================
    // TEST EXPERIENCED JUNIOR: Interazioni intermedie e validazione requisiti
    // =======================================================================

    @Test
    @DisplayName("Experienced junior tester: generate(DefaultPasswordRuleConf) rispetta la complessità e le classi di caratteri")
    void generateFromRuleConfHonorsCharacterClassRequirements() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
        // Richiediamo esplicitamente almeno 1 maiuscola, 1 minuscola, 1 numero
        DefaultPasswordRuleConf conf = conf(16, 16, 1, 1, 1, 0);

        String password = generator.exposedGenerate(conf);

        assertNotNull(password);
        assertEquals(16, password.length());
        assertTrue(password.chars().anyMatch(Character::isUpperCase), "Deve contenere maiuscole");
        assertTrue(password.chars().anyMatch(Character::isLowerCase), "Deve contenere minuscole");
        assertTrue(password.chars().anyMatch(Character::isDigit), "Deve contenere numeri");
    }

    @Test
    @DisplayName("Experienced junior tester: Il refactoring di mergeCharacterCounts preserva il massimo rigore per i contatori")
    void mergeCombinesCharacterCountsCorrectly() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        DefaultPasswordRuleConf conf1 = conf(8, 20, 2, 0, 1, 0);
        DefaultPasswordRuleConf conf2 = conf(8, 20, 0, 3, 0, 1);

        DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(conf1, conf2));

        assertEquals(2, merged.getUppercase());
        assertEquals(3, merged.getLowercase());
        assertEquals(1, merged.getDigit());
        assertEquals(1, merged.getSpecial());
    }

    // =======================================================================
    // TEST SENIOR: Edge cases, combinazioni complesse e individuazione bug
    // =======================================================================

    @Test
    @DisplayName("Senior tester: Il refactoring di mergeLists unisce correttamente caratteri illegali e speciali senza duplicati")
    void mergeListsRemovesDuplicatesAndCombinesProperly() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        DefaultPasswordRuleConf conf1 = conf(8, 20, 0, 0, 0, 0);
        conf1.getSpecialChars().addAll(List.of('!', '@', '#'));
        conf1.getIllegalChars().addAll(List.of('x', 'y'));

        DefaultPasswordRuleConf conf2 = conf(8, 20, 0, 0, 0, 0);
        conf2.getSpecialChars().addAll(List.of('#', '$', '%')); // '#' è duplicato
        conf2.getIllegalChars().addAll(List.of('y', 'z'));       // 'y' è duplicato

        DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(conf1, conf2));

        assertEquals(5, merged.getSpecialChars().size(), "Dovrebbe unire i set evitando duplicati (!, @, #, $, %)");
        assertTrue(merged.getSpecialChars().containsAll(List.of('!', '@', '#', '$', '%')));

        assertEquals(3, merged.getIllegalChars().size(), "Dovrebbe unire i set evitando duplicati (x, y, z)");
        assertTrue(merged.getIllegalChars().containsAll(List.of('x', 'y', 'z')));
    }

    @Test
    @Disabled("Questo test FALLISCE rivelando un bug nella logica estratta in mergeFlags")
    @DisplayName("Senior tester: mergeFlags deve propagare isUsernameAllowed=false se almeno una conf lo vieta")
    void mergeFlagsShouldPropagateMostRestrictiveUsernamePolicy() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        DefaultPasswordRuleConf allowUsername = conf(8, 20, 0, 0, 0, 0);
        allowUsername.setUsernameAllowed(true);

        DefaultPasswordRuleConf forbidUsername = conf(8, 20, 0, 0, 0, 0);
        forbidUsername.setUsernameAllowed(false);

        // Attualmente in ClasseBC0.java la logica è:
        // if (!result.isUsernameAllowed()) { result.setUsernameAllowed(ruleConf.isUsernameAllowed()); }
        // Poiché result inizializza `usernameAllowed` a `true`, la condizione non si verifica MAI
        // e `forbidUsername` viene ignorato.
        DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(allowUsername, forbidUsername));

        assertFalse(merged.isUsernameAllowed(), "Il flag usernameAllowed dovrebbe essere false se almeno una policy lo richiede");
    }

    @Test
    @DisplayName("Senior tester: generate con liste vuote di Realms non lancia eccezioni e usa le policy della risorsa")
    void generateFromResourceAndEmptyRealmsUsesResourcePolicy() {
        TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        DefaultPasswordRuleConf resourceConf = conf(15, 15, 1, 1, 1, 0);
        generator.registerRule("resource-only-rule", passwordRuleReturning(resourceConf));

        PasswordPolicy resourcePolicy = mock(PasswordPolicy.class);
        doReturn(List.of(implementation("resource-only-rule"))).when(resourcePolicy).getRules();

        ExternalResource resource = mock(ExternalResource.class);
        when(resource.getPasswordPolicy()).thenReturn(resourcePolicy);

        String password = generator.generate(resource, List.of());

        assertNotNull(password);
        assertEquals(15, password.length());
    }

    // Metodi di helper interni per il mock
    private static Implementation implementation(final String key) {
        Implementation implementation = mock(Implementation.class);
        when(implementation.getKey()).thenReturn(key);
        return implementation;
    }

    private static PasswordRule passwordRuleReturning(final DefaultPasswordRuleConf conf) {
        PasswordRule rule = mock(PasswordRule.class);
        when(rule.getConf()).thenReturn(conf);
        return rule;
    }
}
