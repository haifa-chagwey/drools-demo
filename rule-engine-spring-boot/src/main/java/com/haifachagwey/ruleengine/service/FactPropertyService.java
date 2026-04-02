package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.FactProperty;
import com.haifachagwey.ruleengine.model.FactPropertyAllowedValue;
import com.haifachagwey.ruleengine.model.FactPropertyType;
import com.haifachagwey.ruleengine.repository.FactPropertyRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FactPropertyService {

    private final FactPropertyRepository factPropertyRepository;

    public FactPropertyService(FactPropertyRepository factPropertyRepository) {
        this.factPropertyRepository = factPropertyRepository;
    }

    @PostConstruct
    public void init() {
        if (factPropertyRepository.count() == 0) {
            factPropertyRepository.save(FactProperty.builder()
                    .key("amount")
                    .label("Transaction Amount")
                    .type(FactPropertyType.NUMBER)
                    .description("The total amount of the transaction")
                    .build());
            factPropertyRepository.save(FactProperty.builder()
                    .key("country")
                    .label("Customer Country")
                    .type(FactPropertyType.STRING)
                    .description("ISO 3166-1 alpha-2 country code")
                    .build());
            factPropertyRepository.save(FactProperty.builder()
                    .key("age")
                    .label("Customer Age")
                    .type(FactPropertyType.NUMBER)
                    .description("Age of the customer in years")
                    .build());
            factPropertyRepository.save(FactProperty.builder()
                    .key("vip")
                    .label("Is VIP")
                    .type(FactPropertyType.BOOLEAN)
                    .description("Whether the customer has VIP status")
                    .build());

            factPropertyRepository.save(FactProperty.builder()
                    .key("productGroup")
                    .label("Product Group")
                    .type(FactPropertyType.STRING)
                    .description("The group the product belongs to")
                    .allowedValues(List.of(
                            FactPropertyAllowedValue.builder().value("shortparker").build(),
                            FactPropertyAllowedValue.builder().value("contract").build()
                    ))
                    .build());
        }
    }

    public List<FactProperty> getAllFacts() {
        return factPropertyRepository.findAll();
    }

    public Optional<FactProperty> getFactByKey(String key) {
        return factPropertyRepository.findByKey(key);
    }

    public FactProperty saveFact(FactProperty factProperty) {
        return factPropertyRepository.save(factProperty);
    }

    public void deleteFact(Integer id) {
        factPropertyRepository.deleteById(id);
    }
}
