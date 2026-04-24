package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.*;
import com.haifachagwey.ruleengine.repository.FactTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FactService {

    private final FactTypeRepository factTypeRepository;

    public FactService(FactTypeRepository factTypeRepository) {
        this.factTypeRepository = factTypeRepository;
    }


    @Transactional
    public List<Fact> getAllFactTypes() {
        return factTypeRepository.findAll();
    }

    @Transactional
    public Optional<Fact> getFactTypeById(Integer id) {
        return factTypeRepository.findById(id);
    }

    public Optional<Fact> getFactTypeByName(String name) {
        return factTypeRepository.findByName(name);
    }

    public Fact saveFactType(Fact fact) {
        if (fact.getProperties() != null) {
            fact.getProperties().forEach(property -> {
                property.setFact(fact);
                if (property.getAllowedValues() != null) {
                    property.getAllowedValues().forEach(value -> value.setFactProperty(property));
                }
            });
        }
        if (fact.getAssociatedActions() != null) {
            fact.getAssociatedActions().forEach(action -> {
                action.setFact(fact);
                if (action.getAllowedValues() != null) {
                    action.getAllowedValues().forEach(value -> value.setFactAssociatedAction(action));
                }
            });
        }
        return factTypeRepository.save(fact);
    }

    public void deleteFactType(Integer id) {
        factTypeRepository.deleteById(id);
    }
}
