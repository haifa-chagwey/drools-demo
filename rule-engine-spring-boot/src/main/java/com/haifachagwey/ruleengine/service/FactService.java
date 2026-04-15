package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.*;
import com.haifachagwey.ruleengine.repository.FactPropertyRepository;
import com.haifachagwey.ruleengine.repository.FactTypeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FactService {

    private final FactTypeRepository factTypeRepository;
    private final FactPropertyRepository factPropertyRepository;

    public FactService(FactTypeRepository factTypeRepository, FactPropertyRepository factPropertyRepository) {
        this.factTypeRepository = factTypeRepository;
        this.factPropertyRepository = factPropertyRepository;
    }

    @PostConstruct
    public void init() {
        if (factTypeRepository.count() == 0) {
            FactType transactionFactType = FactType.builder()
                    .name("Transaction")
                    .description("Transaction fact")
                    .build();
            factTypeRepository.save(transactionFactType);

            FactType customerFactType = FactType.builder()
                    .name("Customer")
                    .description("Customer fact")
                    .build();
            factTypeRepository.save(customerFactType);

//            Fact properties
            factPropertyRepository.save(FactProperty.builder()
                    .key("amount")
                    .label("Transaction Amount")
                    .type(FactPropertyType.NUMBER)
                    .description("The total amount of the transaction")
                    .factType(transactionFactType)
                    .build());
            factPropertyRepository.save(FactProperty.builder()
                    .key("country")
                    .label("Customer Country")
                    .type(FactPropertyType.STRING)
                    .description("ISO 3166-1 alpha-2 country code")
                    .factType(customerFactType)
                    .build());
            factPropertyRepository.save(FactProperty.builder()
                    .key("age")
                    .label("Customer Age")
                    .type(FactPropertyType.NUMBER)
                    .description("Age of the customer in years")
                    .factType(customerFactType)
                    .build());
            factPropertyRepository.save(FactProperty.builder()
                    .key("vip")
                    .label("Is VIP")
                    .type(FactPropertyType.BOOLEAN)
                    .description("Whether the customer has VIP status")
                    .factType(customerFactType)
                    .build());

            FactProperty productGroup = FactProperty.builder()
                    .key("productGroup")
                    .label("Product Group")
                    .type(FactPropertyType.STRING)
                    .description("The group the product belongs to")
                    .factType(transactionFactType)
                    .build();

            FactPropertyAllowedValue v1 = FactPropertyAllowedValue.builder()
                    .value("shortparker")
                    .factProperty(productGroup) // ✅ IMPORTANT
                    .build();

            FactPropertyAllowedValue v2 = FactPropertyAllowedValue.builder()
                    .value("contract")
                    .factProperty(productGroup) // ✅ IMPORTANT
                    .build();

            productGroup.setAllowedValues(List.of(v1, v2));

            factPropertyRepository.save(productGroup);


        }
    }

    public List<FactType> getAllFactTypes() {
        return factTypeRepository.findAll();
    }

    public Optional<FactType> getFactTypeById(Integer id) {
        return factTypeRepository.findById(id);
    }

    public Optional<FactType> getFactTypeByName(String name) {
        return factTypeRepository.findByName(name);
    }

    public FactType saveFactType(FactType factType) {
        return factTypeRepository.save(factType);
    }

    public void deleteFactType(Integer id) {
        factTypeRepository.deleteById(id);
    }

    // Fact Property Management
    public List<FactProperty> getAllProperties() {
        return factPropertyRepository.findAll();
    }

    public List<FactProperty> getPropertiesByFactTypeId(Integer factTypeId) {
        return factPropertyRepository.findByFactTypeId(factTypeId);
    }

    public Optional<FactProperty> getPropertyByKey(String key) {
        return factPropertyRepository.findByKey(key);
    }

    public FactProperty saveProperty(FactProperty factProperty) {
        return factPropertyRepository.save(factProperty);
    }

    public void deleteProperty(Integer id) {
        factPropertyRepository.deleteById(id);
    }
}
