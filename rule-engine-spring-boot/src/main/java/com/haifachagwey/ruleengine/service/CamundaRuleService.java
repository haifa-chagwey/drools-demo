package com.haifachagwey.ruleengine.service;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;

@Service
public class CamundaRuleService {

    private final DmnEngine dmnEngine;

    public CamundaRuleService() {
        this.dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();
    }

    public String evaluateDish(String season, int guestCount) {
        return evaluateFromClasspath("dish-decision.dmn", "dish-decision", Map.of(
                "season", season,
                "guestCount", guestCount
        ));
    }

    public String evaluateBeverage(String timeOfDay) {
        return evaluateFromClasspath("beverage-decision.dmn", "beverage-decision", Map.of(
                "timeOfDay", timeOfDay
        ));
    }

    private String evaluateFromClasspath(String dmnFileName, String decisionId, Map<String, Object> variables) {
        InputStream inputStream = getClass().getResourceAsStream("/" + dmnFileName);
        DmnDecision decision = dmnEngine.parseDecision(decisionId, inputStream);
        DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);
        return result.getSingleResult().getSingleEntry();
    }

    public <T> T evaluateFromXml(String dmnXml, String decisionId, Map<String, Object> variables) {
        InputStream inputStream = new java.io.ByteArrayInputStream(dmnXml.getBytes());
        DmnDecision decision = dmnEngine.parseDecision(decisionId, inputStream);
        DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);
        return result.getSingleResult().getSingleEntry();
    }
}