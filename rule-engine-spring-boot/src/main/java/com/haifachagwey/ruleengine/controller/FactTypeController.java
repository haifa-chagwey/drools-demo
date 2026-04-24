package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.model.Fact;
import com.haifachagwey.ruleengine.service.FactService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/api/admin/facts")
public class FactTypeController {

    private final FactService factService;

    public FactTypeController(FactService factService) {
        this.factService = factService;
    }

    @GetMapping
    public List<Fact> getAllFactsTypes() {
        return factService.getAllFactTypes();
    }

    @GetMapping("/{id}")
    public Fact getFactType(@PathVariable Integer id) {
        return factService.getFactTypeById(id).orElseThrow();
    }

    @PostMapping
    public Fact addFactType(@RequestBody Fact fact) {
        return factService.saveFactType(fact);
    }

    @PutMapping("/{id}")
    public Fact updateFactType(@PathVariable Integer id, @RequestBody Fact fact) {
        fact.setId(id);
        return factService.saveFactType(fact);
    }

    @DeleteMapping("/{id}")
    public void deleteFactType(@PathVariable Integer id) {
        factService.deleteFactType(id);
    }
}
