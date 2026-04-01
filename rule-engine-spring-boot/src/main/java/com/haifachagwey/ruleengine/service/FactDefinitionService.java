package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.Fact;
import com.haifachagwey.ruleengine.model.FactAllowedValue;
import com.haifachagwey.ruleengine.model.FactType;
import com.haifachagwey.ruleengine.repository.FactDefinitionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FactDefinitionService {

    private final FactDefinitionRepository factDefinitionRepository;

    public FactDefinitionService(FactDefinitionRepository factDefinitionRepository) {
        this.factDefinitionRepository = factDefinitionRepository;
    }

    @PostConstruct
    public void init() {
        if (factDefinitionRepository.count() == 0) {
            factDefinitionRepository.save(Fact.builder()
                    .key("amount")
                    .label("Transaction Amount")
                    .type(FactType.NUMBER)
                    .description("The total amount of the transaction")
                    .source("facts")
                    .build());
            factDefinitionRepository.save(Fact.builder()
                    .key("country")
                    .label("Customer Country")
                    .type(FactType.STRING)
                    .description("ISO 3166-1 alpha-2 country code")
                    .source("facts")
                    .build());
            factDefinitionRepository.save(Fact.builder()
                    .key("age")
                    .label("Customer Age")
                    .type(FactType.NUMBER)
                    .description("Age of the customer in years")
                    .source("facts")
                    .build());
            factDefinitionRepository.save(Fact.builder()
                    .key("vip")
                    .label("Is VIP")
                    .type(FactType.BOOLEAN)
                    .description("Whether the customer has VIP status")
                    .source("facts")
                    .build());

            factDefinitionRepository.save(Fact.builder()
                    .key("productGroup")
                    .label("Product Group")
                    .type(FactType.STRING)
                    .description("The group the product belongs to")
                    .source("facts")
                    .allowedValues(List.of(
                            FactAllowedValue.builder().value("shortparker").build(),
                            FactAllowedValue.builder().value("contract").build()
                    ))
                    .build());
        }
    }

    public List<Fact> getAllFacts() {
        return factDefinitionRepository.findAll();
    }

    public Optional<Fact> getFactByKey(String key) {
        return factDefinitionRepository.findByKey(key);
    }

    public Fact saveFact(Fact fact) {
        return factDefinitionRepository.save(fact);
    }

    public void deleteFact(Integer id) {
        factDefinitionRepository.deleteById(id);
    }
}
