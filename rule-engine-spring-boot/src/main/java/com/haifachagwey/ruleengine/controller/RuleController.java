package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.model.RuleEntity;
import com.haifachagwey.ruleengine.repository.RuleRepository;
import com.haifachagwey.ruleengine.service.RuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<RuleEntity> getAllRules() {
        return ruleRepository.findAll();
    }

    @PostMapping
    public RuleEntity addRule(@RequestBody RuleEntity ruleEntity) {
        RuleEntity saved = ruleRepository.save(ruleEntity);
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
