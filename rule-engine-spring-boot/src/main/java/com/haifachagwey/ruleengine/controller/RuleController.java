package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.model.Rule;
import com.haifachagwey.ruleengine.repository.RuleRepository;
import com.haifachagwey.ruleengine.service.RuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/admin/rules")
public class RuleController {

    private final RuleRepository ruleRepository;
    private final RuleService ruleService;

    public RuleController(RuleRepository ruleRepository, RuleService ruleService) {
        this.ruleRepository = ruleRepository;
        this.ruleService = ruleService;
    }

    @GetMapping
    public List<Rule> getAllRules() {
        return ruleRepository.findAll();
    }

    @PostMapping
    public Rule addRule(@RequestBody Rule rule) {
        Rule saved = ruleRepository.save(rule);
        ruleService.reloadRules();
        return saved;
    }

    @DeleteMapping("/{id}")
    public void deleteRule(@PathVariable Integer id) {
        ruleRepository.deleteById(id);
        ruleService.reloadRules();
    }

    @PostMapping("/reload")
    public String reload() {
        ruleService.reloadRules();
        return "Rules reloaded successfully";
    }
}
