package org.apache.syncope.core.spring.security;

import org.apache.syncope.common.lib.policy.DefaultPasswordRuleConf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ManualAfterAllDefaultPasswordGeneratorTest {

    private DefaultPasswordGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new DefaultPasswordGenerator();
    }


    @Test
    void testMerge_EmptyList_MinLengthIsEight() {
        DefaultPasswordRuleConf result = generator.merge(List.of());
        assertEquals(8, result.getMinLength());
    }

    @Test
    void testMerge_EmptyList_MaxLengthIsSixtyFour() {
        DefaultPasswordRuleConf result = generator.merge(List.of());
        assertEquals(64, result.getMaxLength());
    }

    @Test
    void testMerge_EmptyList_UsernameAllowedIsTrue() {
        DefaultPasswordRuleConf result = generator.merge(List.of());
        assertTrue(result.isUsernameAllowed());
    }


    @Test
    void testMerge_MinLength_UpdatesWhenHigher() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.setMinLength(10);
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.setMinLength(20);
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(20, result.getMinLength());
    }

    @Test
    void testMerge_MaxLength_UpdatesWhenLowerAndGreaterThanZero() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.setMaxLength(64);
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.setMaxLength(30);
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(30, result.getMaxLength());
    }

    @Test
    void testMerge_Alphabetical_UpdatesWhenHigher() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.setAlphabetical(2);
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.setAlphabetical(5);
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(5, result.getAlphabetical());
    }

    @Test
    void testMerge_Uppercase_UpdatesWhenHigher() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.setUppercase(1);
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.setUppercase(3);
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(3, result.getUppercase());
    }

    @Test
    void testMerge_Lowercase_UpdatesWhenHigher() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.setLowercase(1);
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.setLowercase(3);
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(3, result.getLowercase());
    }

    @Test
    void testMerge_Digit_UpdatesWhenHigher() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.setDigit(1);
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.setDigit(3);
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(3, result.getDigit());
    }

    @Test
    void testMerge_Special_UpdatesWhenHigher() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.setSpecial(1);
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.setSpecial(3);
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(3, result.getSpecial());
    }

    @Test
    void testMerge_RepeatSame_UpdatesWhenHigher() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.setRepeatSame(1);
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.setRepeatSame(3);
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(3, result.getRepeatSame());
    }

    @Test
    void testMerge_MinLength_KeepsWhenEqual() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.setMinLength(10);
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.setMinLength(10);
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(10, result.getMinLength());
    }

    @Test
    void testMerge_Alphabetical_KeepsWhenEqual() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.setAlphabetical(5);
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.setAlphabetical(5);
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(5, result.getAlphabetical());
    }

    @Test
    void testMerge_Uppercase_KeepsWhenEqual() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.setUppercase(3);
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.setUppercase(3);
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(3, result.getUppercase());
    }

    @Test
    void testMerge_Lowercase_KeepsWhenEqual() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.setLowercase(3);
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.setLowercase(3);
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(3, result.getLowercase());
    }

    @Test
    void testMerge_Digit_KeepsWhenEqual() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.setDigit(3);
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.setDigit(3);
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(3, result.getDigit());
    }

    @Test
    void testMerge_Special_KeepsWhenEqual() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.setSpecial(3);
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.setSpecial(3);
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(3, result.getSpecial());
    }

    @Test
    void testMerge_RepeatSame_KeepsWhenEqual() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.setRepeatSame(3);
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.setRepeatSame(3);
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(3, result.getRepeatSame());
    }


    @Test
    void testMerge_SpecialChars_FiltersDuplicates() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.getSpecialChars().add('$');
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.getSpecialChars().add('$');
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(1, result.getSpecialChars().size());
        assertTrue(result.getSpecialChars().contains('$'));
    }

    @Test
    void testMerge_IllegalChars_FiltersDuplicates() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.getIllegalChars().add('!');
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.getIllegalChars().add('!');
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(1, result.getIllegalChars().size());
        assertTrue(result.getIllegalChars().contains('!'));
    }

    @Test
    void testMerge_WordsNotPermitted_FiltersDuplicates() {
        DefaultPasswordRuleConf r1 = new DefaultPasswordRuleConf(); r1.getWordsNotPermitted().add("admin");
        DefaultPasswordRuleConf r2 = new DefaultPasswordRuleConf(); r2.getWordsNotPermitted().add("admin");
        DefaultPasswordRuleConf result = generator.merge(List.of(r1, r2));
        assertEquals(1, result.getWordsNotPermitted().size());
        assertTrue(result.getWordsNotPermitted().contains("admin"));
    }


    @Test
    void testMerge_MinLengthZero_FallbackToDefaultMax() {
        DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
        conf.setMinLength(0);
        conf.setMaxLength(5);
        DefaultPasswordRuleConf result = generator.merge(List.of(conf));
        assertEquals(5, result.getMinLength());
    }

    @Test
    void testMerge_MinLengthGreaterThanMax_FixesMax() {
        DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
        conf.setMinLength(20);
        conf.setMaxLength(10);
        DefaultPasswordRuleConf result = generator.merge(List.of(conf));
        assertEquals(20, result.getMaxLength());
    }
}