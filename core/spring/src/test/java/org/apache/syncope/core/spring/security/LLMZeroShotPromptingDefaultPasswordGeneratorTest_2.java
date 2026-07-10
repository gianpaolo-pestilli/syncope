package org.apache.syncope.core.spring.security;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.apache.syncope.common.lib.policy.DefaultPasswordRuleConf;
import org.junit.jupiter.api.Test;

class LLMZeroShotPromptingDefaultPasswordGeneratorTest_2 {

        private final TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        @Test
        void generateShouldUseDefaultMinimumLengthWhenMinLengthIsZero() {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(0);
            conf.setMaxLength(64);

            String password = generator.generatePassword(conf);

            assertNotNull(password);
            assertEquals(8, password.length());
        }

        @Test
        void generateShouldRespectExactLengthWhenMinAndMaxAreEqual() {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(12);
            conf.setMaxLength(12);

            String password = generator.generatePassword(conf);

            assertNotNull(password);
            assertEquals(12, password.length());
        }

        @Test
        void generateShouldRespectMinimumAndMaximumLength() {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(10);
            conf.setMaxLength(16);

            String password = generator.generatePassword(conf);

            assertNotNull(password);
            assertTrue(password.length() >= 10);
            assertTrue(password.length() <= 16);
        }

        @Test
        void generateShouldIncludeRequiredLowercaseCharacters() {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(10);
            conf.setMaxLength(10);
            conf.setLowercase(2);

            String password = generator.generatePassword(conf);

            assertTrue(countLowercase(password) >= 2);
        }

        @Test
        void generateShouldIncludeRequiredUppercaseCharacters() {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(10);
            conf.setMaxLength(10);
            conf.setUppercase(2);

            String password = generator.generatePassword(conf);

            assertTrue(countUppercase(password) >= 2);
        }

        @Test
        void generateShouldIncludeRequiredDigits() {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(10);
            conf.setMaxLength(10);
            conf.setDigit(3);

            String password = generator.generatePassword(conf);

            assertTrue(countDigits(password) >= 3);
        }

        @Test
        void generateShouldIncludeRequiredSpecialCharacters() {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(12);
            conf.setMaxLength(12);
            conf.setSpecial(2);

            String password = generator.generatePassword(conf);

            assertTrue(countSpecialCharacters(password) >= 2);
        }

        @Test
        void mergeShouldUseDefaultValuesWhenNoRulesAreProvided() {
            DefaultPasswordRuleConf merged = generator.mergeRules(List.of());

            assertEquals(0, merged.getMinLength());
            assertEquals(64, merged.getMaxLength());
            assertTrue(merged.isUsernameAllowed());
        }

        @Test
        void mergeShouldKeepTheStrictestLengthLimits() {
            DefaultPasswordRuleConf first = new DefaultPasswordRuleConf();
            first.setMinLength(8);
            first.setMaxLength(20);

            DefaultPasswordRuleConf second = new DefaultPasswordRuleConf();
            second.setMinLength(12);
            second.setMaxLength(16);

            DefaultPasswordRuleConf merged = generator.mergeRules(List.of(first, second));

            assertEquals(12, merged.getMinLength());
            assertEquals(16, merged.getMaxLength());
        }

        @Test
        void mergeShouldKeepTheStrictestCharacterRequirements() {
            DefaultPasswordRuleConf first = new DefaultPasswordRuleConf();
            first.setLowercase(1);
            first.setUppercase(2);
            first.setDigit(1);

            DefaultPasswordRuleConf second = new DefaultPasswordRuleConf();
            second.setLowercase(3);
            second.setUppercase(1);
            second.setSpecial(2);

            DefaultPasswordRuleConf merged = generator.mergeRules(List.of(first, second));

            assertEquals(3, merged.getLowercase());
            assertEquals(2, merged.getUppercase());
            assertEquals(1, merged.getDigit());
            assertEquals(2, merged.getSpecial());
        }

        private static long countLowercase(final String value) {
            return value.chars().filter(Character::isLowerCase).count();
        }

        private static long countUppercase(final String value) {
            return value.chars().filter(Character::isUpperCase).count();
        }

        private static long countDigits(final String value) {
            return value.chars().filter(Character::isDigit).count();
        }

        private static long countSpecialCharacters(final String value) {
            return value.chars()
                    .filter(ch -> !Character.isLetterOrDigit(ch))
                    .count();
        }

        private static class TestableDefaultPasswordGenerator extends DefaultPasswordGenerator {

            String generatePassword(final DefaultPasswordRuleConf conf) {
                return generate(conf);
            }

            DefaultPasswordRuleConf mergeRules(final List<DefaultPasswordRuleConf> confs) {
                return merge(confs);
            }
        }
    }
