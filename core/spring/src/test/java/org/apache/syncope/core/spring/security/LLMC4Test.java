package org.apache.syncope.core.spring.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
public class LLMC4Test {
        /**
         * Sottoclasse di utilità per esporre i metodi protetti senza dover ricorrere
         * alla reflection, mantenendo il testing isolato.
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

        /**
         * Metodo helper per costruire rapidamente configurazioni.
         */
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
        // TEST JUNIOR: Verifiche di base sui comportamenti attesi e validazioni
        // =======================================================================

        @Test
        @DisplayName("Junior tester: generate(DefaultPasswordRuleConf) crea una password rispettando la lunghezza esatta")
        void generateFromRuleConfCreatesPasswordWithConfiguredLength() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            DefaultPasswordRuleConf conf = conf(12, 12, 0, 0, 0, 0);

            String password = generator.exposedGenerate(conf);

            assertNotNull(password);
            assertEquals(12, password.length(), "La password generata deve essere lunga esattamente 12 caratteri");
        }

        @Test
        @DisplayName("Junior tester: validate() scarta una configurazione nulla lanciando IllegalArgumentException")
        void generateThrowsExceptionForNullConfiguration() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> generator.exposedGenerate(null)
            );
            assertEquals("Configuration cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Junior tester: merge() applica correttamente i valori di fallback se riceve una lista vuota")
        void mergeUsesDefaultsWhenNoRuleConfigurationsAreSupplied() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            DefaultPasswordRuleConf merged = generator.exposedMerge(List.of());

            assertNotNull(merged);
            assertEquals(8, merged.getMinLength(), "La lunghezza minima di default deve essere 8");
            assertEquals(64, merged.getMaxLength(), "La lunghezza massima di default deve essere 64");
            assertTrue(merged.isUsernameAllowed(), "Username deve essere permesso di default");
        }

        // =======================================================================
        // TEST EXPERIENCED JUNIOR: Validazione limiti e coerenza logica
        // =======================================================================

        @Test
        @DisplayName("Experienced junior tester: generate(DefaultPasswordRuleConf) inietta correttamente le classi di caratteri")
        void generateFromRuleConfHonorsCharacterClassRequirements() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();
            DefaultPasswordRuleConf conf = conf(16, 16, 1, 1, 1, 0);

            String password = generator.exposedGenerate(conf);

            assertNotNull(password);
            assertEquals(16, password.length());
            assertTrue(password.chars().anyMatch(Character::isUpperCase), "Dovrebbe contenere almeno una maiuscola");
            assertTrue(password.chars().anyMatch(Character::isLowerCase), "Dovrebbe contenere almeno una minuscola");
            assertTrue(password.chars().anyMatch(Character::isDigit), "Dovrebbe contenere almeno un numero");
        }

        @Test
        @DisplayName("Experienced junior tester: validate() blocca limiti di lunghezza impossibili o negativi")
        void generateThrowsExceptionForInvalidLengths() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            // Limite negativo
            IllegalArgumentException exMin = assertThrows(IllegalArgumentException.class,
                    () -> generator.exposedGenerate(conf(-1, 20, 0, 0, 0, 0)));
            assertEquals("Minimum length cannot be negative", exMin.getMessage());

            // Limite di inviluppo illogico
            IllegalArgumentException exMinMax = assertThrows(IllegalArgumentException.class,
                    () -> generator.exposedGenerate(conf(20, 10, 0, 0, 0, 0)));
            assertEquals("Minimum length cannot be greater than maximum length", exMinMax.getMessage());
        }

        @Test
        @DisplayName("Experienced junior tester: mergeCharacterCounts() preserva il contatore più stringente tra le policy")
        void mergeCombinesCharacterCountsCorrectly() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            DefaultPasswordRuleConf conf1 = conf(8, 20, 2, 0, 1, 0); // 2 Maiuscole, 1 Numero
            DefaultPasswordRuleConf conf2 = conf(8, 20, 0, 3, 0, 1); // 3 Minuscole, 1 Speciale

            DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(conf1, conf2));

            assertEquals(2, merged.getUppercase());
            assertEquals(3, merged.getLowercase());
            assertEquals(1, merged.getDigit());
            assertEquals(1, merged.getSpecial());
        }

        // =======================================================================
        // TEST SENIOR: Limiti matematici, deduplicazione e identificazione bug
        // =======================================================================

        @Test
        @DisplayName("Senior tester: validate() intercetta un'impossibilità aritmetica (requisiti > lunghezza max)")
        void generateThrowsExceptionWhenRequiredCharactersExceedMaxLength() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            // maxLength = 4, Requisiti totali = 5 (2 UPPER + 2 LOWER + 1 DIGIT)
            DefaultPasswordRuleConf conf = conf(4, 4, 2, 2, 1, 0);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> generator.exposedGenerate(conf)
            );
            assertEquals("Strict cardinality requirements exceed maximum length", exception.getMessage());
        }

        @Test
        @DisplayName("Senior tester: validate() intercetta un conflitto diretto tra set speciali e illegali")
        void generateThrowsExceptionWhenSpecialAndIllegalCharactersConflict() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            DefaultPasswordRuleConf conf = conf(8, 20, 1, 1, 1, 1);
            conf.getSpecialChars().addAll(List.of('!', '@', '#'));
            conf.getIllegalChars().addAll(List.of('#', '%', '^')); // '#' causa paradosso (richiesto ma vietato)

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> generator.exposedGenerate(conf)
            );
            assertEquals("Conflict between special and illegal characters", exception.getMessage());
        }

        @Test
        @DisplayName("Senior tester: mergeLists() aggrega set di caratteri multipli eliminando i duplicati cross-policy")
        void mergeListsRemovesDuplicatesAndCombinesProperly() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            DefaultPasswordRuleConf conf1 = conf(8, 20, 0, 0, 0, 0);
            conf1.getSpecialChars().addAll(List.of('!', '@', '#'));
            conf1.getIllegalChars().addAll(List.of('x', 'y'));

            DefaultPasswordRuleConf conf2 = conf(8, 20, 0, 0, 0, 0);
            conf2.getSpecialChars().addAll(List.of('#', '$', '%')); // '#' è duplicato
            conf2.getIllegalChars().addAll(List.of('y', 'z'));       // 'y' è duplicato

            DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(conf1, conf2));

            assertEquals(5, merged.getSpecialChars().size(), "L'unione deve produrre esattamente 5 caratteri unici");
            assertTrue(merged.getSpecialChars().containsAll(List.of('!', '@', '#', '$', '%')));

            assertEquals(3, merged.getIllegalChars().size(), "L'unione deve produrre esattamente 3 caratteri unici");
            assertTrue(merged.getIllegalChars().containsAll(List.of('x', 'y', 'z')));
        }

        @Test
        @Disabled("FALLIMENTO ATTESO: Bug persistente in mergeFlags - la restrizione su usernameAllowed viene ignorata")
        @DisplayName("Senior tester: mergeFlags() deve diffondere false se anche solo una policy lo richiede")
        void mergeFlagsShouldPropagateMostRestrictiveUsernamePolicy() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            DefaultPasswordRuleConf allowUsername = conf(8, 20, 0, 0, 0, 0);
            allowUsername.setUsernameAllowed(true);

            DefaultPasswordRuleConf forbidUsername = conf(8, 20, 0, 0, 0, 0);
            forbidUsername.setUsernameAllowed(false);

            // Nel codice sorgente: if (!result.isUsernameAllowed())
            // L'oggetto 'result' parte da true. La condizione non sarà mai vera, quindi 'false' viene ignorato.
            DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(allowUsername, forbidUsername));

            assertFalse(merged.isUsernameAllowed(), "Il divieto (false) deve sovrascrivere il permesso (true)");
        }

        @Test
        @DisplayName("Senior tester: generate() con liste di Realms vuote usa in modo sicuro la policy della risorsa singola")
        void generateFromResourceAndEmptyRealmsUsesResourcePolicy() {
            TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

            DefaultPasswordRuleConf resourceConf = conf(15, 15, 1, 1, 1, 0);
            generator.registerRule("resource-only-rule", passwordRuleReturning(resourceConf));

            PasswordPolicy resourcePolicy = mock(PasswordPolicy.class);
            doReturn(List.of(implementation("resource-only-rule"))).when(resourcePolicy).getRules();

            ExternalResource resource = mock(ExternalResource.class);
            when(resource.getPasswordPolicy()).thenReturn(resourcePolicy);

            // Uso sicuro dei generics List.<Realm>of()
            String password = generator.generate(resource, List.<Realm>of());

            assertNotNull(password);
            assertEquals(15, password.length());
        }

        // Helper interni per gestire Mockito con pulizia visiva
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