package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.*;
import com.haifachagwey.ruleengine.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ReloadRulesTest {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private FactRepository factRepository;

    @Autowired
    private FactAttributeRepository factAttributeRepository;

    @Autowired
    private AssociatedActionRepository associatedActionRepository;

    @Test
    public void testReloadRulesReflectsChanges() {
        // 1. Create a dummy rule
        Fact fact = Fact.builder().name("TestFact").build();
        fact = factRepository.save(fact);

        FactAttribute attr = FactAttribute.builder()
                .key("testKey")
                .label("Test Key")
                .type(AttributeType.STRING)
                .fact(fact)
                .build();
        attr = factAttributeRepository.save(attr);

        AssociatedAction actionDef = AssociatedAction.builder()
                .key("testAction")
                .label("Test Action")
                .fact(fact)
                .build();
        actionDef = associatedActionRepository.save(actionDef);

        Rule rule = Rule.builder()
                .name("TestRule")
                .fact(fact)
                .active(true)
                .build();

        RuleCondition condition = RuleCondition.builder()
                .attribute(attr)
                .operator("EQUAL")
                .value("testValue")
                .rule(rule)
                .build();

        RuleAction action = RuleAction.builder()
                .action(actionDef)
                .value("\"executed\"")
                .rule(rule)
                .build();

        rule.setConditions(new java.util.ArrayList<>(Collections.singletonList(condition)));
        rule.setActions(new java.util.ArrayList<>(Collections.singletonList(action)));

        ruleRepository.save(rule);

        // 2. Reload rules
        ruleService.reloadRules();

        // 3. Execute and verify
        Map<String, Object> input = new HashMap<>();
        input.put("testKey", "testValue");
        Map<String, Object> results = ruleService.executeRules(input, "GLOBAL");
        assertEquals("executed", results.get("testAction"));

        // 4. Modify rule
        action.setValue("\"modified\"");
        ruleRepository.save(rule);

        // 5. Reload rules again
        ruleService.reloadRules();

        // 6. Execute and verify modified result
        results = ruleService.executeRules(input, "GLOBAL");
        assertEquals("modified", results.get("testAction"), "Reload should reflect rule changes");
    }
}
