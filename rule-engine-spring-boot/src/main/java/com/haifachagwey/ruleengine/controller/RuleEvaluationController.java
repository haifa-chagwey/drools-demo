package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.service.CamundaRuleService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rules")
public class RuleEvaluationController {

    private final CamundaRuleService camundaRuleService;

    public RuleEvaluationController(CamundaRuleService camundaRuleService) {
        this.camundaRuleService = camundaRuleService;
    }

    @PostMapping("/evaluate/dish")
    public String evaluateDish(@RequestBody DishRequest request) {
        return camundaRuleService.evaluateDish(request.getSeason(), request.getGuestCount());
    }

    @PostMapping("/evaluate/beverage")
    public String evaluateBeverage(@RequestBody BeverageRequest request) {
        return camundaRuleService.evaluateBeverage(request.getTimeOfDay());
    }

    @PostMapping("/evaluate/xml")
    public Object evaluateFromXml(@RequestBody XmlEvaluationRequest request) {
        return camundaRuleService.evaluateFromXml(request.getDmnXml(), request.getDecisionId(), request.getVariables());
    }

    @Data
    public static class DishRequest {
        private String season;
        private int guestCount;
    }

    @Data
    public static class BeverageRequest {
        private String timeOfDay;
    }

    @Data
    public static class XmlEvaluationRequest {
        private String dmnXml;
        private String decisionId;
        private Map<String, Object> variables;
    }
}
