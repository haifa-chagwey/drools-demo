package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.model.Fact;
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
    public List<Fact> getAllFacts() {
        return factService.getAllFacts();
    }

    @GetMapping("/{id}")
    public Fact getFact(@PathVariable Integer id) {
        return factService.getFactById(id).orElseThrow();
    }

    @PostMapping
    public Fact addFact(@RequestBody Fact fact) {
        return factService.saveFact(fact);
    }

    @PutMapping("/{id}")
    public Fact updateFact(@PathVariable Integer id, @RequestBody Fact fact) {
        fact.setId(id);
        return factService.saveFact(fact);
    }

    @DeleteMapping("/{id}")
    public void deleteFact(@PathVariable Integer id) {
        factService.deleteFact(id);
    }
}
