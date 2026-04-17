package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.*;
import com.haifachagwey.ruleengine.repository.FactPropertyRepository;
import com.haifachagwey.ruleengine.repository.FactTypeRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
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


    @Transactional
    public List<FactType> getAllFactTypes() {
        return factTypeRepository.findAll();
    }

    @Transactional
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

    @Transactional
    public List<FactProperty> getAllProperties() {
        return factPropertyRepository.findAll();
    }

    @Transactional
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
