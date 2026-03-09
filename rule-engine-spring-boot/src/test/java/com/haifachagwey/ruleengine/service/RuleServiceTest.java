package com.haifachagwey.ruleengine.service;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RuleServiceTest {

    private final RuleService ruleService = new RuleService();

    @Test
    public void testEvaluateUnder18() {
        Map<String, Object> input = new HashMap<>();
        input.put("age", 16);
        Map<String, Object> output = ruleService.evaluate(input);
        assertEquals(20.0, output.get("discount"));
    }

    @Test
    public void testEvaluateOver18() {
        Map<String, Object> input = new HashMap<>();
        input.put("age", 25);
        Map<String, Object> output = ruleService.evaluate(input);
        assertEquals(0.0, output.get("discount"));
    }
}