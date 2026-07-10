package org.apache.syncope.core.spring.security;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import org.apache.syncope.common.lib.policy.DefaultPasswordRuleConf;
import org.junit.jupiter.api.Test;
class LLMFewShotPromptingDefaultPasswordGeneratorTest_1 {

        private static class TestableDefaultPasswordGenerator extends DefaultPasswordGenerator {

            String generateFrom(final DefaultPasswordRuleConf ruleConf) {
                return generate(ruleConf);
            }

            DefaultPasswordRuleConf mergeFrom(final List<DefaultPasswordRuleConf> ruleConfs) {
                return merge(ruleConfs);
            }
        }

        private final TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        @Test
        void testGenerate_WithValidPolicy() {
            DefaultPasswordRuleConf rule = new DefaultPasswordRuleConf();
            rule.setMinLength(8);
            rule.setMaxLength(16);

            String password = generator.generateFrom(rule);

            boolean condition = password != null && password.length() >= 8 && password.length() <= 16;
            assertTrue(condition);
        }

        @Test
        void testGenerate_WithEmptyPolicyListUsesDefaults() {
            String password = generator.generate(Collections.emptyList());

            assertAll(
                    () -> assertNotNull(password),
                    () -> assertTrue(password.length() >= 8),
                    () -> assertTrue(password.length() <= 64));
        }

        @Test
        void testGenerate_WithExactLengthPolicy() {
            DefaultPasswordRuleConf rule = new DefaultPasswordRuleConf();
            rule.setMinLength(12);
            rule.setMaxLength(12);

            String password = generator.generateFrom(rule);

            assertAll(
                    () -> assertNotNull(password),
                    () -> assertTrue(password.length() == 12));
        }

        @Test
        void testGenerate_WhenMinLengthIsZeroFallsBackToMinimumAlphabeticalCharacters() {
            DefaultPasswordRuleConf rule = new DefaultPasswordRuleConf();
            rule.setMinLength(0);
            rule.setMaxLength(12);

            String password = generator.generateFrom(rule);

            long alphabeticalCharacters = password.chars().filter(Character::isLetter).count();

            assertAll(
                    () -> assertNotNull(password),
                    () -> assertTrue(password.length() <= 12),
                    () -> assertTrue(alphabeticalCharacters >= 8));
        }

        @Test
        void testGenerate_WithUppercaseRequirement() {
            DefaultPasswordRuleConf rule = new DefaultPasswordRuleConf();
            rule.setMinLength(8);
            rule.setMaxLength(16);
            rule.setUppercase(3);

            String password = generator.generateFrom(rule);

            long uppercaseCharacters = password.chars().filter(Character::isUpperCase).count();

            assertAll(
                    () -> assertNotNull(password),
                    () -> assertTrue(password.length() >= 8 && password.length() <= 16),
                    () -> assertTrue(uppercaseCharacters >= 3));
        }

        @Test
        void testGenerate_WithLowercaseRequirement() {
            DefaultPasswordRuleConf rule = new DefaultPasswordRuleConf();
            rule.setMinLength(8);
            rule.setMaxLength(16);
            rule.setLowercase(4);

            String password = generator.generateFrom(rule);

            long lowercaseCharacters = password.chars().filter(Character::isLowerCase).count();

            assertAll(
                    () -> assertNotNull(password),
                    () -> assertTrue(password.length() >= 8 && password.length() <= 16),
                    () -> assertTrue(lowercaseCharacters >= 4));
        }

        @Test
        void testGenerate_WithDigitRequirement() {
            DefaultPasswordRuleConf rule = new DefaultPasswordRuleConf();
            rule.setMinLength(8);
            rule.setMaxLength(16);
            rule.setDigit(3);

            String password = generator.generateFrom(rule);

            long digits = password.chars().filter(Character::isDigit).count();

            assertAll(
                    () -> assertNotNull(password),
                    () -> assertTrue(password.length() >= 8 && password.length() <= 16),
                    () -> assertTrue(digits >= 3));
        }

        @Test
        void testGenerate_WithSpecialCharacterRequirement() {
            DefaultPasswordRuleConf rule = new DefaultPasswordRuleConf();
            rule.setMinLength(8);
            rule.setMaxLength(16);
            rule.setSpecial(2);
            rule.getSpecialChars().add('!');
            rule.getSpecialChars().add('@');
            rule.getSpecialChars().add('#');

            String password = generator.generateFrom(rule);

            long specialCharacters = password.chars()
                    .filter(ch -> ch == '!' || ch == '@' || ch == '#')
                    .count();

            assertAll(
                    () -> assertNotNull(password),
                    () -> assertTrue(password.length() >= 8 && password.length() <= 16),
                    () -> assertTrue(specialCharacters >= 2));
        }

        @Test
        void testGenerate_WithIllegalCharactersDoesNotContainThem() {
            DefaultPasswordRuleConf rule = new DefaultPasswordRuleConf();
            rule.setMinLength(8);
            rule.setMaxLength(20);
            rule.getIllegalChars().add('a');
            rule.getIllegalChars().add('B');
            rule.getIllegalChars().add('7');

            String password = generator.generateFrom(rule);

            assertAll(
                    () -> assertNotNull(password),
                    () -> assertFalse(password.indexOf('a') >= 0),
                    () -> assertFalse(password.indexOf('B') >= 0),
                    () -> assertFalse(password.indexOf('7') >= 0));
        }

        @Test
        void testGenerate_WithCombinedCharacterRequirements() {
            DefaultPasswordRuleConf rule = new DefaultPasswordRuleConf();
            rule.setMinLength(10);
            rule.setMaxLength(20);
            rule.setUppercase(2);
            rule.setLowercase(2);
            rule.setDigit(2);
            rule.setSpecial(2);
            rule.getSpecialChars().add('$');
            rule.getSpecialChars().add('%');
            rule.getSpecialChars().add('&');

            String password = generator.generateFrom(rule);

            assertAll(
                    () -> assertNotNull(password),
                    () -> assertTrue(password.length() >= 10 && password.length() <= 20),
                    () -> assertTrue(password.chars().filter(Character::isUpperCase).count() >= 2),
                    () -> assertTrue(password.chars().filter(Character::isLowerCase).count() >= 2),
                    () -> assertTrue(password.chars().filter(Character::isDigit).count() >= 2),
                    () -> assertTrue(password.chars().filter(ch -> ch == '$' || ch == '%' || ch == '&').count() >= 2));
        }

        @Test
        void testMerge_WithMultipleRulesKeepsMostRestrictiveLengths() {
            DefaultPasswordRuleConf first = new DefaultPasswordRuleConf();
            first.setMinLength(8);
            first.setMaxLength(30);

            DefaultPasswordRuleConf second = new DefaultPasswordRuleConf();
            second.setMinLength(12);
            second.setMaxLength(24);

            DefaultPasswordRuleConf merged = generator.mergeFrom(List.of(first, second));

            assertAll(
                    () -> assertTrue(merged.getMinLength() == 12),
                    () -> assertTrue(merged.getMaxLength() == 24));
        }

        @Test
        void testMerge_WithMultipleRulesKeepsMaximumCharacterRequirements() {
            DefaultPasswordRuleConf first = new DefaultPasswordRuleConf();
            first.setMinLength(8);
            first.setMaxLength(32);
            first.setUppercase(1);
            first.setLowercase(5);
            first.setDigit(2);
            first.setSpecial(1);


        }
}