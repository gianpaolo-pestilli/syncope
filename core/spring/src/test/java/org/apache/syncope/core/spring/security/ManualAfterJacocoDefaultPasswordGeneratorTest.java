package org.apache.syncope.core.spring.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.syncope.common.lib.policy.DefaultPasswordRuleConf;
import org.apache.syncope.core.persistence.api.entity.Implementation;
import org.apache.syncope.core.persistence.api.entity.policy.PasswordPolicy;
import org.apache.syncope.core.provisioning.api.rules.PasswordRule;
import org.apache.syncope.core.spring.implementation.ImplementationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;

public class ManualAfterJacocoDefaultPasswordGeneratorTest {

    private DefaultPasswordGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new DefaultPasswordGenerator();
    }

    @Test
    void testGetPasswordRulesLambdaCoverage() {
        PasswordPolicy policy = mock(PasswordPolicy.class);
        Implementation impl = mock(Implementation.class);
        when(impl.getKey()).thenReturn("testKey");

        doReturn(Collections.singletonList(impl)).when(policy).getRules();

        PasswordRule mockRule = mock(PasswordRule.class);

        try (MockedStatic<ImplementationManager> managerMock = mockStatic(ImplementationManager.class)) {
            managerMock.when(() -> ImplementationManager.buildPasswordRule(
                    eq(impl),
                    ArgumentMatchers.<Supplier<PasswordRule>>any(),
                    ArgumentMatchers.<Consumer<PasswordRule>>any()
            )).thenAnswer(invocation -> {
                Supplier<PasswordRule> supplier = invocation.getArgument(1);
                Consumer<PasswordRule> consumer = invocation.getArgument(2);

                consumer.accept(mockRule);
                PasswordRule supplied = supplier.get();

                assertEquals(mockRule, supplied);
                return Optional.of(mockRule);
            });

            List<PasswordRule> rules = generator.getPasswordRules(policy);
            assertEquals(1, rules.size());
        }
    }

    @Test
    void testMergeMaxLengthBranchCoverage() {

        DefaultPasswordRuleConf ruleConfValid = new DefaultPasswordRuleConf();
        ruleConfValid.setMaxLength(10);
        DefaultPasswordRuleConf res1 = generator.merge(Collections.singletonList(ruleConfValid));
        assertEquals(10, res1.getMaxLength());


        DefaultPasswordRuleConf ruleConfTooLong = new DefaultPasswordRuleConf();
        ruleConfTooLong.setMaxLength(100);
        DefaultPasswordRuleConf res2 = generator.merge(Collections.singletonList(ruleConfTooLong));
        assertEquals(64, res2.getMaxLength());
    }

    @Test
    void testMergeWordsNotPermittedCoverage() {
        DefaultPasswordRuleConf rule1 = new DefaultPasswordRuleConf();
        rule1.getWordsNotPermitted().addAll(Arrays.asList("admin", "test"));

        DefaultPasswordRuleConf rule2 = new DefaultPasswordRuleConf();
        rule2.getWordsNotPermitted().addAll(Arrays.asList("admin", "root"));

        DefaultPasswordRuleConf result = generator.merge(Arrays.asList(rule1, rule2));

        assertEquals(3, result.getWordsNotPermitted().size());
        assertTrue(result.getWordsNotPermitted().containsAll(Arrays.asList("admin", "test", "root")));
    }
}