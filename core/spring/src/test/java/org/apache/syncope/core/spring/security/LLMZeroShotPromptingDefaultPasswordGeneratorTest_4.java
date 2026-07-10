package org.apache.syncope.core.spring.security;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.apache.syncope.common.lib.policy.DefaultPasswordRuleConf;
import org.junit.jupiter.api.Test;
 class LLMZeroShotPromptingDefaultPasswordGeneratorTest_4 {

     private static final class TestableDefaultPasswordGenerator extends DefaultPasswordGenerator {

         DefaultPasswordRuleConf exposedMerge(final List<DefaultPasswordRuleConf> confs) {
             return merge(confs);
         }

            String exposedGenerate(final DefaultPasswordRuleConf conf) {
                return generate(conf);
            }
        }

        private final TestableDefaultPasswordGenerator generator = new TestableDefaultPasswordGenerator();

        private static DefaultPasswordRuleConf conf(final int minLength, final int maxLength) {
            DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
            conf.setMinLength(minLength);
            conf.setMaxLength(maxLength);
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

        private static long nonAlphanumericCount(final String value) {
            return value.chars().filter(ch -> !Character.isLetterOrDigit(ch)).count();
        }

        private static void assertLengthBetween(final String password, final int min, final int max) {
            assertNotNull(password);
            assertTrue(password.length() >= min, "Generated password is shorter than minimum boundary");
            assertTrue(password.length() <= max, "Generated password is longer than maximum boundary");
        }

        @Test
        void mergeOfNoRulesUsesVeryMinimumVeryMaximumAndUsernameAllowed() {
            DefaultPasswordRuleConf merged = generator.exposedMerge(List.of());

            assertEquals(0, merged.getMinLength());
            assertEquals(64, merged.getMaxLength());
            assertTrue(merged.isUsernameAllowed());
        }

        @Test
        void mergeKeepsMinLengthAtVeryMinimumBoundaryWhenSingleRuleAlsoZero() {
            DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(conf(0, 64)));

            assertEquals(0, merged.getMinLength());
            assertEquals(64, merged.getMaxLength());
        }

        @Test
        void mergeSelectsHighestMinimumLengthBoundaryAcrossPolicies() {
            DefaultPasswordRuleConf low = conf(7, 64);
            DefaultPasswordRuleConf boundary = conf(8, 64);
            DefaultPasswordRuleConf high = conf(9, 64);

            DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(low, boundary, high));

            assertEquals(9, merged.getMinLength());
            assertEquals(64, merged.getMaxLength());
        }

        @Test
        void mergeSelectsLowestMaximumLengthBoundaryAcrossPolicies() {
            DefaultPasswordRuleConf wide = conf(0, 64);
            DefaultPasswordRuleConf narrow = conf(0, 63);
            DefaultPasswordRuleConf exactBoundary = conf(0, 8);

            DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(wide, narrow, exactBoundary));

            assertEquals(0, merged.getMinLength());
            assertEquals(8, merged.getMaxLength());
        }

        @Test
        void mergePropagatesMostRestrictiveCharacterCountBoundaries() {
            DefaultPasswordRuleConf first = conf(0, 64);
            first.setAlphabetical(1);
            first.setUppercase(2);
            first.setLowercase(0);
            first.setDigit(3);
            first.setSpecial(1);
            first.setRepeatSame(2);

            DefaultPasswordRuleConf second = conf(0, 64);
            second.setAlphabetical(2);
            second.setUppercase(1);
            second.setLowercase(4);
            second.setDigit(1);
            second.setSpecial(3);
            second.setRepeatSame(1);

            DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(first, second));

            assertEquals(2, merged.getAlphabetical());
            assertEquals(2, merged.getUppercase());
            assertEquals(4, merged.getLowercase());
            assertEquals(3, merged.getDigit());
            assertEquals(3, merged.getSpecial());
            assertEquals(2, merged.getRepeatSame());
        }

        @Test
        void mergeUsernameAllowedIsFalseWhenAnyPolicyDisallowsUsername() {
            DefaultPasswordRuleConf allowed = conf(0, 64);
            allowed.setUsernameAllowed(true);

            DefaultPasswordRuleConf denied = conf(0, 64);
            denied.setUsernameAllowed(false);

            DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(allowed, denied));

            assertFalse(merged.isUsernameAllowed());
        }

        @Test
        void mergeAggregatesSpecialIllegalWordsAndSchemaCollections() {
            DefaultPasswordRuleConf first = conf(0, 64);
            first.getSpecialChars().add('!');
            first.getIllegalChars().add('x');
            first.getWordsNotPermitted().add("alpha");
            first.getSchemasNotPermitted().add("email");

            DefaultPasswordRuleConf second = conf(0, 64);
            second.getSpecialChars().add('?');
            second.getIllegalChars().add('y');
            second.getWordsNotPermitted().add("beta");
            second.getSchemasNotPermitted().add("username");

            DefaultPasswordRuleConf merged = generator.exposedMerge(List.of(first, second));

            assertTrue(merged.getSpecialChars().containsAll(List.of('!', '?')));
            assertTrue(merged.getIllegalChars().containsAll(List.of('x', 'y')));
            assertTrue(merged.getWordsNotPermitted().containsAll(List.of("alpha", "beta")));
            assertTrue(merged.getSchemasNotPermitted().containsAll(List.of("email", "username")));
        }

        @Test
        void generateWithMinimumAndMaximumBothZeroUsesDefaultMinimumLengthBoundary() {
            String password = generator.exposedGenerate(conf(0, 0));

            assertLengthBetween(password, 8, 64);
        }

        @Test
        void generateWithMinimumZeroAndMaximumOneExposesInconsistentBoundary() {
            assertThrows(IllegalArgumentException.class, () -> generator.exposedGenerate(conf(0, 1)));
        }

        @Test
        void generateWithMinimumEqualToMaximumAtLowerUsefulBoundaryProducesExactLength() {
            String password = generator.exposedGenerate(conf(8, 8));

            assertEquals(8, password.length());
        }

        @Test
        void generateWithMinimumEqualToMaximumAtVeryMaximumBoundaryProducesExactLength() {
            String password = generator.exposedGenerate(conf(64, 64));

            assertEquals(64, password.length());
        }

        @Test
        void generateWithMinimumGreaterThanMaximumThrowsForInteractingLengthBoundaries() {
            assertThrows(IllegalArgumentException.class, () -> generator.exposedGenerate(conf(9, 8)));
        }

        @Test
        void generateSatisfiesExactCharacterClassBoundaryCombination() {
            DefaultPasswordRuleConf conf = conf(8, 8);
            conf.setUppercase(1);
            conf.setLowercase(1);
            conf.setDigit(1);
            conf.setSpecial(1);

            String password = generator.exposedGenerate(conf);

            assertEquals(8, password.length());
            assertTrue(uppercaseCount(password) >= 1);
            assertTrue(lowercaseCount(password) >= 1);
            assertTrue(digitCount(password) >= 1);
            assertTrue(nonAlphanumericCount(password) >= 1);
        }

        @Test
        void generateThrowsWhenRequiredCharacterCountsExceedMaximumLength() {
            DefaultPasswordRuleConf conf = conf(4, 4);
            conf.setUppercase(2);
            conf.setLowercase(2);
            conf.setDigit(1);

            assertThrows(IllegalArgumentException.class, () -> generator.exposedGenerate(conf));
        }

        @Test
        void generateHonorsSingleAllowedSpecialCharacterAtBoundary() {
            DefaultPasswordRuleConf conf = conf(8, 8);
            conf.setSpecial(1);
            conf.getSpecialChars().add('!');

            String password = generator.exposedGenerate(conf);

            assertEquals(8, password.length());
            assertTrue(password.indexOf('!') >= 0);
            assertEquals(1, nonAlphanumericCount(password));
        }
    }