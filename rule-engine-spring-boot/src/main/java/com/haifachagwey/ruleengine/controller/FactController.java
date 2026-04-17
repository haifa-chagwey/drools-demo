package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.model.FactType;
import com.haifachagwey.ruleengine.service.FactService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/api/admin/facts")
public class FactController {

    private final FactService factService;

    public FactController(FactService factService) {
        this.factService = factService;
    }

    @GetMapping
    public List<FactType> getAllFacts() {
        return factService.getAllFactTypes();
    }

    @GetMapping("/{id}")
    public FactType getFact(@PathVariable Integer id) {
        return factService.getFactTypeById(id).orElseThrow();
    }

    @PostMapping
    public FactType addFact(@RequestBody FactType factType) {
        return factService.saveFactType(factType);
    }

    @DeleteMapping("/{id}")
    public void deleteFact(@PathVariable Integer id) {
        factService.deleteFactType(id);
    }
}
