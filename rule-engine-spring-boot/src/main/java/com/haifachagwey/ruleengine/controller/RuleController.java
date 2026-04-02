package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.model.Rule;
import com.haifachagwey.ruleengine.service.RuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/admin/rules")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @GetMapping
    public List<Rule> getAllRules() {
        return ruleService.getAllRules();
    }

    @PostMapping
    public Rule addRule(@RequestBody Rule rule) {
        return ruleService.saveRule(rule);
    }

    @DeleteMapping("/{id}")
    public void deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
    }

    @PostMapping("/reload")
    public String reload() {
        ruleService.reloadRules();
        return "Rules reloaded successfully";
    }
}
