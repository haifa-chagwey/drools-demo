package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.RuleEntity;
import com.haifachagwey.ruleengine.repository.RuleRepository;
import jakarta.annotation.PostConstruct;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.RuleBuilder;
import org.jeasy.rules.mvel.MVELCondition;
import org.jeasy.rules.mvel.MVELAction;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;
    private final RulesEngine rulesEngine;
    private Rules rules;

    public RuleService(RuleRepository ruleRepository, RulesEngine rulesEngine) {
        this.ruleRepository = ruleRepository;
        this.rulesEngine = rulesEngine;
    }

    @PostConstruct
    public void init() {
        reloadRules();
    }

    public synchronized void reloadRules() {
        List<RuleEntity> ruleEntities = ruleRepository.findAll();
        Rules newRules = new Rules();

        for (RuleEntity entity : ruleEntities) {
            if (entity.getCondition() != null && entity.getAction() != null) {
                try {
                    Rule easyRule = new RuleBuilder()
                            .name(entity.getName())
                            .description(entity.getDescription())
                            .when(new MVELCondition(entity.getCondition()))
                            .then(new MVELAction(entity.getAction()))
                            .build();
                    newRules.register(easyRule);
                } catch (Exception e) {
                    System.err.println("Error registering rule " + entity.getName() + ": " + e.getMessage());
                }
            }
        }
        this.rules = newRules;
    }

    public Map<String, Object> executeRules(Map<String, Object> input) {
        if (rules == null) {
            reloadRules();
        }
        
        Facts facts = new Facts();
        // Add all input map entries as individual facts
        input.forEach((key, value) -> facts.put(key, value));
        // Prepare an output map as a fact
        Map<String, Object> output = new HashMap<>();
        facts.put("output", output);
        
        rulesEngine.fire(rules, facts);
        
        return output;
    }
}
