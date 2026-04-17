package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.model.FactProperty;
import com.haifachagwey.ruleengine.service.FactService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/admin/fact-properties")
public class FactPropertyController {

    private final FactService factService;

    public FactPropertyController(FactService factService) {
        this.factService = factService;
    }

    @GetMapping
    public List<FactProperty> getAllFacts() {
        return factService.getAllProperties();
    }

    @GetMapping("/by-fact/{factId}")
    public List<FactProperty> getPropertiesByFactId(@PathVariable Integer factId) {
        return factService.getPropertiesByFactTypeId(factId);
    }

    @PostMapping
    public FactProperty addFact(@RequestBody FactProperty factProperty) {
        return factService.saveProperty(factProperty);
    }

    @DeleteMapping("/{id}")
    public void deleteFact(@PathVariable Integer id) {
        factService.deleteProperty(id);
    }
}
