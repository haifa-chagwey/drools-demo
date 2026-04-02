package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.model.FactProperty;
import com.haifachagwey.ruleengine.service.FactPropertyService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/admin/facts")
public class FactPropertyController {

    private final FactPropertyService factPropertyService;

    public FactPropertyController(FactPropertyService factPropertyService) {
        this.factPropertyService = factPropertyService;
    }

    @GetMapping
    public List<FactProperty> getAllFacts() {
        return factPropertyService.getAllFacts();
    }

    @PostMapping
    public FactProperty addFact(@RequestBody FactProperty factProperty) {
        return factPropertyService.saveFact(factProperty);
    }

    @DeleteMapping("/{id}")
    public void deleteFact(@PathVariable Integer id) {
        factPropertyService.deleteFact(id);
    }
}
