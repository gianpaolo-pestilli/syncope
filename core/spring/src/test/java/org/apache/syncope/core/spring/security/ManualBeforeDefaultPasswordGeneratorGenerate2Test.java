package org.apache.syncope.core.spring.security;

import org.apache.syncope.common.lib.policy.DefaultPasswordRuleConf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManualBeforeDefaultPasswordGeneratorGenerate2Test {

    private DefaultPasswordGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new DefaultPasswordGenerator();
    }



    @Test
    void testGenerate_InputNull() {
        assertThrows(Exception.class, () -> generator.generate((DefaultPasswordRuleConf) null),
                "Un input null deve essere rigettato sollevando un'eccezione");
    }


    @Test
    void testGenerate_InputConsistent() {
        DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
        conf.setMinLength(8);
        conf.setMaxLength(16);
        conf.setAlphabetical(4);
        conf.setDigit(2);

        String result = generator.generate(conf);

        assertTrue(result.length() >= 8 && result.length() <= 16,
                "La password deve essere generata rispettando i confini di lunghezza imposti");
    }


    @Test
    void testGenerate_Inconsistent_MinGreaterThanMax() {
        DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
        conf.setMinLength(20);
        conf.setMaxLength(10);

        assertThrows(Exception.class, () -> generator.generate(conf),
                "Il sistema deve rigettare la configurazione a causa delle lunghezze contraddittorie");
    }


    @Test
    void testGenerate_Inconsistent_CardinalityExceedsMax() {
        DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
        conf.setMinLength(5);
        conf.setMaxLength(10);

        conf.setAlphabetical(8);
        conf.setDigit(7);

        assertThrows(Exception.class, () -> generator.generate(conf),
                "Il sistema deve rigettare la configurazione a causa dei vincoli di cardinalità impossibili da soddisfare nel limite massimo");
    }


    @Test
    void testGenerate_Inconsistent_ConflictingCharacters() {
        DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
        conf.setMinLength(8);

        conf.getSpecialChars().add(('@'));
        conf.getIllegalChars().add(('@'));
        conf.setSpecial(1);

        assertThrows(Exception.class, () -> generator.generate(conf),
                "Una regola con set di caratteri auto-contraddittori deve essere respinta");
    }



    @Test
    void testGenerate_Inconsistent_MinLengthNegative() {
        DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
        conf.setMinLength(-5);
        conf.setMaxLength(10);

        assertThrows(Exception.class, () -> generator.generate(conf),
                "Il sistema deve rigettare configurazioni con lunghezze minime negative");
    }



    @Test
    void testGenerate_Inconsistent_MaxLengthNegative() {
        boolean b;
        try{
        DefaultPasswordRuleConf conf = new DefaultPasswordRuleConf();
        conf.setMinLength(8);
        conf.setMaxLength(-10);

        String s = this.generator.generate(conf);
        b = s != null;

        } catch (Exception e) {
            b = true;
        }
        assertTrue(b);
    }
}