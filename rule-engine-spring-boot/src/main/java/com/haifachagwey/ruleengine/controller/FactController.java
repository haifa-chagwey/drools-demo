package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.dto.FactDTO;
import com.haifachagwey.ruleengine.service.FactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<FactDTO>> getAllFacts() {
        return ResponseEntity.ok(factService.getAllFacts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactDTO> getFact(@PathVariable Integer id) {
        return ResponseEntity.ok(factService.getFactById(id));
    }

    @PostMapping
    public ResponseEntity<FactDTO> addFact(@RequestBody FactDTO factDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(factService.saveFact(factDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FactDTO> updateFact(@PathVariable Integer id, @RequestBody FactDTO factDTO) {
        factDTO.setId(id);
        return ResponseEntity.ok(factService.saveFact(factDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFact(@PathVariable Integer id) {
        factService.deleteFact(id);
        return ResponseEntity.noContent().build();
    }
}