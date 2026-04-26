package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.*;
import com.haifachagwey.ruleengine.repository.FactRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FactService {

    private final FactRepository factRepository;

    public FactService(FactRepository factRepository) {
        this.factRepository = factRepository;
    }


    @Transactional
    public List<Fact> getAllFacts() {
        return factRepository.findAll();
    }

    @Transactional
    public Optional<Fact> getFactById(Integer id) {
        return factRepository.findById(id);
    }



    public Fact saveFact(Fact fact) {
        if (fact.getAttributes() != null) {
            fact.getAttributes().forEach(att -> {
                att.setFact(fact);
                if (att.getOptions() != null) {
                    att.getOptions().forEach(option -> option.setAttribute(att));
                }
            });
        }
        if (fact.getAssociatedActions() != null) {
            fact.getAssociatedActions().forEach(assac -> {
                assac.setFact(fact);
                if (assac.getOptions() != null) {
                    assac.getOptions().forEach(option -> option.setAction(assac));
                }
            });
        }
        return factRepository.save(fact);
    }

    public void deleteFact(Integer id) {
        factRepository.deleteById(id);
    }
}
