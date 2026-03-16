package com.haifachagwey.ruleengine;

import com.haifachagwey.ruleengine.model.Rule;
import com.haifachagwey.ruleengine.model.TenantConfig;
import com.haifachagwey.ruleengine.repository.RuleRepository;
import com.haifachagwey.ruleengine.repository.TenantConfigRepository;
import com.haifachagwey.ruleengine.service.RuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class TenantSpecificThresholdTest {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private TenantConfigRepository tenantConfigRepository;

    @BeforeEach
    public void setup() {
        tenantConfigRepository.deleteAll();
        ruleRepository.deleteAll();
    }

    @Test
    public void testTenantSpecificThresholdWithGlobalFallback() {
        // 1. Define a rule that uses tenantConfigs
//        String drl = "package rules;\n" +
//                "import java.util.Map;\n" +
//                "global java.util.Map output;\n" +
//                "global java.util.Map tenantConfigs;\n" +
//                "rule \"Threshold Rule\"\n" +
//                "when\n" +
//                "    $input : Map(this[\"amount\"] > Integer.parseInt((String)tenantConfigs.get(\"threshold\")))\n" +
//                "then\n" +
//                "    output.put(\"triggered\", true);\n" +
//                "    output.put(\"value\", tenantConfigs.get(\"threshold\"));\n" +
//                "end";
        String condition = "this[\"amount\"] > Integer.parseInt((String)tenantConfigs.get(\"threshold\"))";
        String action = "output.put(\"triggered\", true);\n" +
                "output.put(\"value\", tenantConfigs.get(\"threshold\"));";


        Rule rule = Rule.builder()
                .name("ThresholdRule")
                .description("Triggers when amount exceeds tenant-specific threshold with global fallback")
                .condition(condition)
                .action(action)
                .build();

        ruleRepository.save(rule);
        ruleService.reloadRules();

        // 2. Set up GLOBAL threshold
        tenantConfigRepository.save(TenantConfig.builder()
                .tenantId("GLOBAL")
                .configKey("threshold")
                .configValue("100")
                .build());

        // 3. Set up Tenant-specific threshold (TenantA overrides Global)
        tenantConfigRepository.save(TenantConfig.builder()
                .tenantId("TenantA")
                .configKey("threshold")
                .configValue("200")
                .build());

        // 4. Test Global fallback (TenantB has no specific threshold)
        Map<String, Object> input = new HashMap<>();
        input.put("amount", 150);
        
        // TenantB should use GLOBAL threshold (100) -> 150 > 100 -> Triggered
        Map<String, Object> outputB = ruleService.executeRules(input, "TenantB");
        assertEquals(true, outputB.get("triggered"), "TenantB should use global threshold 100");
        assertEquals("100", outputB.get("value"));

        // 5. Test Tenant override (TenantA uses its own 200)
        // TenantA: 150 > 200 -> Not Triggered
        Map<String, Object> outputA = ruleService.executeRules(input, "TenantA");
        assertNull(outputA.get("triggered"), "TenantA should use its specific threshold 200 and not trigger");

        // TenantA with 250 -> 250 > 200 -> Triggered
        input.put("amount", 250);
        Map<String, Object> outputA2 = ruleService.executeRules(input, "TenantA");
        assertEquals(true, outputA2.get("triggered"));
        assertEquals("200", outputA2.get("value"));
        
        // 6. Test direct "GLOBAL" tenant execution
        input.put("amount", 150);
        Map<String, Object> outputGlobal = ruleService.executeRules(input, "GLOBAL");
        assertEquals(true, outputGlobal.get("triggered"));
        assertEquals("100", outputGlobal.get("value"));
    }
}
