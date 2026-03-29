package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.model.Fact;
import com.haifachagwey.ruleengine.service.FactDefinitionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/admin/facts")
public class FactDefinitionController {

    private final FactDefinitionService factDefinitionService;

    public FactDefinitionController(FactDefinitionService factDefinitionService) {
        this.factDefinitionService = factDefinitionService;
    }

    @GetMapping
    public List<Fact> getAllFacts() {
        return factDefinitionService.getAllFacts();
    }

    @PostMapping
    public Fact addFact(@RequestBody Fact fact) {
        return factDefinitionService.saveFact(fact);
    }

    @DeleteMapping("/{id}")
    public void deleteFact(@PathVariable Integer id) {
        factDefinitionService.deleteFact(id);
    }
}
