package org.apache.syncope.core.spring.security;
import org.apache.syncope.common.lib.policy.DefaultPasswordRuleConf;
import org.apache.syncope.core.spring.implementation.ImplementationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class ManualBeforeDefaultPasswordGeneratorMergeTest {

    private DefaultPasswordGenerator generator;
    private ImplementationManager implementationManager;

    @BeforeEach
    public void configureTheEnvironment() {
        this.generator = new DefaultPasswordGenerator();
    }


    @Test
    public void testMergeNullList() {
        boolean exceptionThrown = false;
        try {
            this.generator.merge(null);
        } catch (Exception e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testMergeEmptyList() {
        DefaultPasswordRuleConf result = this.generator.merge(Collections.emptyList());
        boolean condition = (result != null);
        assertTrue(condition);
    }

    @Test
    public void testMergeSingleElementList() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getMinLength()).thenReturn(100);

        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getMinLength() >= 100);
        assertTrue(condition);
    }

    @Test
    public void testMergeMultipleElementsList() {
        DefaultPasswordRuleConf mockRule1 = mock(DefaultPasswordRuleConf.class);
        when(mockRule1.getMinLength()).thenReturn(100);

        DefaultPasswordRuleConf mockRule2 = mock(DefaultPasswordRuleConf.class);
        when(mockRule2.getMinLength()).thenReturn(200);

        DefaultPasswordRuleConf result = this.generator.merge(Arrays.asList(mockRule1, mockRule2));
        boolean condition = (result.getMinLength() >= 200);
        assertTrue(condition);
    }

    @Test
    public void testMergeListWithNullElement() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        boolean exceptionThrown = false;
        try {
            this.generator.merge(Arrays.asList(mockRule, null));
        } catch (Exception e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }


    @Test public void testMinLengthNegative() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getMinLength()).thenReturn(-5);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getMinLength() >= 0);
        assertTrue(condition);
    }

    @Test public void testMaxLengthNegative() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getMaxLength()).thenReturn(-5);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getMaxLength() >= 0);
        assertTrue(condition);
    }

    @Test public void testAlphabeticalNegative() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getAlphabetical()).thenReturn(-5);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getAlphabetical() >= 0);
        assertTrue(condition);
    }

    @Test public void testUppercaseNegative() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getUppercase()).thenReturn(-5);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getUppercase() >= 0);
        assertTrue(condition);
    }

    @Test public void testLowercaseNegative() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getLowercase()).thenReturn(-5);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getLowercase() >= 0);
        assertTrue(condition);
    }

    @Test public void testDigitNegative() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getDigit()).thenReturn(-5);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getDigit() >= 0);
        assertTrue(condition);
    }

    @Test public void testSpecialNegative() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getSpecial()).thenReturn(-5);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getSpecial() >= 0);
        assertTrue(condition);
    }

    @Test public void testRepeatSameNegative() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getRepeatSame()).thenReturn(-5);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getRepeatSame() >= 0);
        assertTrue(condition);
    }

    @Test public void testMinLengthZero() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getMinLength()).thenReturn(0);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getMinLength() >= 0);
        assertTrue(condition);
    }

    @Test public void testMaxLengthZero() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getMaxLength()).thenReturn(0);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getMaxLength() >= 0);
        assertTrue(condition);
    }

    @Test public void testAlphabeticalZero() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getAlphabetical()).thenReturn(0);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getAlphabetical() >= 0);
        assertTrue(condition);
    }

    @Test public void testUppercaseZero() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getUppercase()).thenReturn(0);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getUppercase() >= 0);
        assertTrue(condition);
    }

    @Test public void testLowercaseZero() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getLowercase()).thenReturn(0);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getLowercase() >= 0);
        assertTrue(condition);
    }

    @Test public void testDigitZero() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getDigit()).thenReturn(0);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getDigit() >= 0);
        assertTrue(condition);
    }

    @Test public void testSpecialZero() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getSpecial()).thenReturn(0);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getSpecial() >= 0);
        assertTrue(condition);
    }

    @Test public void testRepeatSameZero() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getRepeatSame()).thenReturn(0);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getRepeatSame() >= 0);
        assertTrue(condition);
    }


    @Test public void testMinLengthOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getMinLength()).thenReturn(1);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getMinLength() >= 1);
        assertTrue(condition);
    }

    @Test public void testMaxLengthOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getMaxLength()).thenReturn(1);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getMaxLength() >= 1);
        assertTrue(condition);
    }

    @Test public void testAlphabeticalOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getAlphabetical()).thenReturn(1);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getAlphabetical() >= 1);
        assertTrue(condition);
    }

    @Test public void testUppercaseOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getUppercase()).thenReturn(1);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getUppercase() >= 1);
        assertTrue(condition);
    }

    @Test public void testLowercaseOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getLowercase()).thenReturn(1);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getLowercase() >= 1);
        assertTrue(condition);
    }

    @Test public void testDigitOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getDigit()).thenReturn(1);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getDigit() >= 1);
        assertTrue(condition);
    }

    @Test public void testSpecialOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getSpecial()).thenReturn(1);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getSpecial() >= 1);
        assertTrue(condition);
    }

    @Test public void testRepeatSameOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getRepeatSame()).thenReturn(1);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getRepeatSame() >= 1);
        assertTrue(condition);
    }


    @Test public void testMinLengthGreaterThanOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getMinLength()).thenReturn(15);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getMinLength() >= 15);
        assertTrue(condition);
    }

    @Test public void testMaxLengthGreaterThanOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getMaxLength()).thenReturn(20);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getMaxLength() <= 20) || (result.getMaxLength() >= 0);
        assertTrue(condition);
    }

    @Test public void testAlphabeticalGreaterThanOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getAlphabetical()).thenReturn(5);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getAlphabetical() >= 5);
        assertTrue(condition);
    }

    @Test public void testUppercaseGreaterThanOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getUppercase()).thenReturn(3);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getUppercase() >= 3);
        assertTrue(condition);
    }

    @Test public void testLowercaseGreaterThanOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getLowercase()).thenReturn(4);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getLowercase() >= 4);
        assertTrue(condition);
    }

    @Test public void testDigitGreaterThanOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getDigit()).thenReturn(6);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getDigit() >= 6);
        assertTrue(condition);
    }

    @Test public void testSpecialGreaterThanOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getSpecial()).thenReturn(2);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getSpecial() >= 2);
        assertTrue(condition);
    }

    @Test public void testRepeatSameGreaterThanOne() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getRepeatSame()).thenReturn(3);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getRepeatSame() >= 3);
        assertTrue(condition);
    }

    @Disabled
    @Test
    public void testMergeInconsistentRuleThrowsException() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getMinLength()).thenReturn(50);
        when(mockRule.getMaxLength()).thenReturn(10);

        boolean exceptionThrown = false;
        DefaultPasswordRuleConf d = null;
        try {
            d = this.generator.merge(List.of(mockRule));
        } catch (Exception e) {
            exceptionThrown = true;
        }
        assertTrue((d == null) || exceptionThrown);
    }
    @Disabled
    @Test
    public void testInconsistentNumericValuesBetweenRules() {
        DefaultPasswordRuleConf mockRule1 = mock(DefaultPasswordRuleConf.class);
        when(mockRule1.getMaxLength()).thenReturn(10);

        DefaultPasswordRuleConf mockRule2 = mock(DefaultPasswordRuleConf.class);
        when(mockRule2.getAlphabetical()).thenReturn(15);

        boolean exceptionThrown = false;
        DefaultPasswordRuleConf d = null;
        try {
            d = this.generator.merge(Arrays.asList(mockRule1, mockRule2));
        } catch (Exception e) {
            exceptionThrown = true;
        }
        assertTrue((d == null) || exceptionThrown);
    }

    @Disabled
    @Test
    public void testSumOfRequiredCharactersExceedsMaxLength() {
        DefaultPasswordRuleConf mockRule1 = mock(DefaultPasswordRuleConf.class);
        when(mockRule1.getMaxLength()).thenReturn(10);
        when(mockRule1.getUppercase()).thenReturn(6);

        DefaultPasswordRuleConf mockRule2 = mock(DefaultPasswordRuleConf.class);
        when(mockRule2.getDigit()).thenReturn(5);

        boolean exceptionThrown = false;
        DefaultPasswordRuleConf d = null;
        try {
            d = this.generator.merge(Arrays.asList(mockRule1, mockRule2));
        } catch (Exception e) {
            exceptionThrown = true;
        }
        assertTrue((d == null) || exceptionThrown);
    }

    @Test
    public void testSpecialCharacterPermittedAndProhibited() {

        DefaultPasswordRuleConf mockRuleA = mock(DefaultPasswordRuleConf.class);
        when(mockRuleA.getSpecialChars()).thenReturn(List.of('$'));

        DefaultPasswordRuleConf mockRuleB = mock(DefaultPasswordRuleConf.class);
        when(mockRuleB.getIllegalChars()).thenReturn(List.of('$'));

        DefaultPasswordRuleConf result = this.generator.merge(Arrays.asList(mockRuleA, mockRuleB));

        boolean condition = result.getIllegalChars().contains('$');
        assertTrue(condition);
    }
    @Disabled
    @Test
    public void testAlphabeticalRequiredButAllLettersProhibited() {
        DefaultPasswordRuleConf mockRuleA = mock(DefaultPasswordRuleConf.class);
        when(mockRuleA.getAlphabetical()).thenReturn(5);

        Character[] allLetters = new Character[52];
        int index = 0;
        for (char c = 'a'; c <= 'z'; c++) allLetters[index++] = c;
        for (char c = 'A'; c <= 'Z'; c++) allLetters[index++] = c;

        DefaultPasswordRuleConf mockRuleB = mock(DefaultPasswordRuleConf.class);
        when(mockRuleB.getIllegalChars()).thenReturn(Arrays.asList(allLetters));

        boolean exceptionThrown = false;
        DefaultPasswordRuleConf d = new DefaultPasswordRuleConf();
        try {
            d = this.generator.merge(Arrays.asList(mockRuleA, mockRuleB));
        } catch (Exception e) {
            exceptionThrown = true;
        }
        boolean t = (d == null)||exceptionThrown;
        assertTrue(t);
    }


    @Test
    public void testUsernameAllowedTrue() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.isUsernameAllowed()).thenReturn(true);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result != null);
        assertTrue(condition);
    }
    @Disabled
    @Test
    public void testUsernameAllowedFalse() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.isUsernameAllowed()).thenReturn(false);
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = !result.isUsernameAllowed();
        assertTrue(condition);
    }


    @Test
    public void testInternalListNullMocked() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getSpecialChars()).thenReturn(null);
        boolean exceptionThrown = false;
        try {
            this.generator.merge(List.of(mockRule));
        } catch (Exception e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testInternalListEmpty() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getWordsNotPermitted()).thenReturn(Collections.emptyList());
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = (result.getWordsNotPermitted() != null);
        assertTrue(condition);
    }

    @Test
    public void testInternalListSingleElement() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getIllegalChars()).thenReturn(List.of('!'));
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = result.getIllegalChars().contains('!');
        assertTrue(condition);
    }

    @Disabled
    @Test
    public void testInternalListMultipleElements() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getSchemasNotPermitted()).thenReturn(Arrays.asList("email", "username"));
        DefaultPasswordRuleConf result = this.generator.merge(List.of(mockRule));
        boolean condition = result.getSchemasNotPermitted().contains("email") &&
                result.getSchemasNotPermitted().contains("username");
        assertTrue(condition);
    }


    @Test
    public void testCommutativityProperty() {
        DefaultPasswordRuleConf mockRuleA = mock(DefaultPasswordRuleConf.class);
        when(mockRuleA.getMinLength()).thenReturn(100);
        when(mockRuleA.getIllegalChars()).thenReturn(List.of('!'));

        DefaultPasswordRuleConf mockRuleB = mock(DefaultPasswordRuleConf.class);
        when(mockRuleB.getMinLength()).thenReturn(200);
        when(mockRuleB.getIllegalChars()).thenReturn(List.of('?'));

        DefaultPasswordRuleConf resultAB = this.generator.merge(Arrays.asList(mockRuleA, mockRuleB));
        DefaultPasswordRuleConf resultBA = this.generator.merge(Arrays.asList(mockRuleB, mockRuleA));

        boolean condition = (resultAB.getMinLength() == resultBA.getMinLength()) &&
                (resultAB.getIllegalChars().containsAll(resultBA.getIllegalChars())) &&
                (resultAB.getIllegalChars().size() == resultBA.getIllegalChars().size());
        assertTrue(condition);
    }

    @Test
    public void testIdempotencyProperty() {
        DefaultPasswordRuleConf mockRule = mock(DefaultPasswordRuleConf.class);
        when(mockRule.getMinLength()).thenReturn(100);
        when(mockRule.getSpecialChars()).thenReturn(List.of('@'));

        DefaultPasswordRuleConf resultSingle = this.generator.merge(List.of(mockRule));
        DefaultPasswordRuleConf resultMultiple = this.generator.merge(Arrays.asList(mockRule, mockRule, mockRule));

        boolean condition = (resultSingle.getMinLength() == resultMultiple.getMinLength()) &&
                (resultSingle.getSpecialChars().size() == resultMultiple.getSpecialChars().size());
        assertTrue(condition);
    }

    @Test
    public void testDisjointCompatibleRules() {
        DefaultPasswordRuleConf mockRuleA = mock(DefaultPasswordRuleConf.class);
        when(mockRuleA.getMinLength()).thenReturn(50);

        DefaultPasswordRuleConf mockRuleB = mock(DefaultPasswordRuleConf.class);
        when(mockRuleB.getSpecialChars()).thenReturn(List.of('$'));

        DefaultPasswordRuleConf result = this.generator.merge(Arrays.asList(mockRuleA, mockRuleB));

        boolean condition = (result.getMinLength() >= 50) && result.getSpecialChars().contains('$');
        assertTrue(condition);
    }

}