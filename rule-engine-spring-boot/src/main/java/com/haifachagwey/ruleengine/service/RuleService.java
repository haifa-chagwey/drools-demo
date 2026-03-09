package com.haifachagwey.ruleengine.service;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;

@Service
public class RuleService {

    private final DmnEngine dmnEngine;

    public RuleService() {
        this.dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();
    }

    public Map<String, Object> evaluate(Map<String, Object> input) {
        String dmnFilePath = "rules/discount-rules.dmn";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(dmnFilePath);

        if (inputStream == null) {
            throw new RuntimeException("DMN file not found: " + dmnFilePath);
        }

        DmnDecision decision = dmnEngine.parseDecision("discount-rules", inputStream);
        VariableMap variables = Variables.createVariables();
        variables.putAll(input);

        DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);

        return result.getFirstResult().getEntryMap();
    }
}