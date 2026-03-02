package com.haifachagwey.ruleengine;

import com.haifachagwey.ruleengine.model.RuleEntity;
import com.haifachagwey.ruleengine.repository.RuleRepository;
import com.haifachagwey.ruleengine.service.RuleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DynamicRuleEntityIntegrationTest {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleRepository ruleRepository;

    @Test
    public void testDynamicRuleLoading() {
        // 1. Define a new rule using easy rules condition and action
        RuleEntity ruleEntity = RuleEntity.builder()
                .name("VIPDiscount")
                .condition("type == \"VIP\"")
                .action("output.put(\"discount\", 20);")
                .build();

        // 2. Save it to DB
        ruleRepository.save(ruleEntity);

        // 3. Reload rules in the engine
        ruleService.reloadRules();

        // 4. Execute and verify
        Map<String, Object> input = new HashMap<>();
        input.put("type", "VIP");

        Map<String, Object> output = ruleService.executeRules(input);

        assertEquals(20, output.get("discount"));
        
        // 5. Update the rule and verify again
        ruleEntity.setAction("output.put(\"discount\", 30);");
        ruleRepository.save(ruleEntity);
        ruleService.reloadRules();
        
        output = ruleService.executeRules(input);
        assertEquals(30, output.get("discount"));
    }
}
