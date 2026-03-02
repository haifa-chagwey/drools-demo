package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.service.RuleService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/execute")
public class RuleExecutionController {

    private final RuleService ruleService;

    public RuleExecutionController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @PostMapping
    public Map<String, Object> execute(@RequestBody Map<String, Object> input) {
        // For now, ruleSetKey is ignored as we use a single global KieContainer
        return ruleService.executeRules(input);
    }
}
