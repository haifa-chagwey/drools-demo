package com.haifachagwey.ruleengine;

import com.haifachagwey.ruleengine.model.Rule;
import com.haifachagwey.ruleengine.repository.RuleRepository;
import com.haifachagwey.ruleengine.service.RuleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DynamicRuleIntegrationTest {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleRepository ruleRepository;

    @Test
    public void testDynamicRuleLoading() {
        // 1. Define a new rule
        String drl = "package rules;\n" +
                "import java.util.Map;\n" +
                "global java.util.Map output;\n" +
                "rule \"Dynamic Discount\"\n" +
                "when\n" +
                "    $input : Map(this[\"type\"] == \"VIP\")\n" +
                "then\n" +
                "    output.put(\"discount\", 20);\n" +
                "end";

        Rule rule = Rule.builder()
                .name("VIPDiscount")
                .drl(drl)
                .build();

        // 2. Save it to DB
        ruleRepository.save(rule);

        // 3. Reload rules in the engine
        ruleService.reloadRules();

        // 4. Execute and verify
        Map<String, Object> input = new HashMap<>();
        input.put("type", "VIP");

        Map<String, Object> output = ruleService.executeRules(input);

        assertEquals(20, output.get("discount"));
        
        // 5. Update the rule and verify again
        String updatedDrl = "package rules;\n" +
                "import java.util.Map;\n" +
                "global java.util.Map output;\n" +
                "rule \"Dynamic Discount\"\n" +
                "when\n" +
                "    $input : Map(this[\"type\"] == \"VIP\")\n" +
                "then\n" +
                "    output.put(\"discount\", 30);\n" +
                "end";
        
        rule.setDrl(updatedDrl);
        ruleRepository.save(rule);
        ruleService.reloadRules();
        
        output = ruleService.executeRules(input);
        assertEquals(30, output.get("discount"));
    }
}
