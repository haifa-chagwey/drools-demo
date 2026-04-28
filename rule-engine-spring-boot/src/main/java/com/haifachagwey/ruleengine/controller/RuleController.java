package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.dto.RuleDTO;
import com.haifachagwey.ruleengine.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/admin/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    @GetMapping
    public ResponseEntity<List<RuleDTO>> getAllRules() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RuleDTO> getRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(ruleService.getRuleById(id));
    }

    @PostMapping
    public ResponseEntity<RuleDTO> addRule(@RequestBody RuleDTO ruleDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ruleService.saveRule(ruleDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RuleDTO> updateRule(@PathVariable Long id, @RequestBody RuleDTO ruleDTO) {
        ruleDTO.setId(id);
        return ResponseEntity.ok(ruleService.saveRule(ruleDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reload")
    public ResponseEntity<Void> reload() {
        ruleService.reloadRules();
        return ResponseEntity.noContent().build();
    }
}