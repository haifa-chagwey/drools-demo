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

    @PostMapping("/{tenantId}")
    public Map<String, Object> execute(@PathVariable String tenantId, @RequestBody Map<String, Object> input) {
        return ruleService.executeRules(input, tenantId);
    }
}
