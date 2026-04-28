package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.dto.FactDTO;
import com.haifachagwey.ruleengine.exception.ResourceNotFoundException;
import com.haifachagwey.ruleengine.mapper.FactMapper;
import com.haifachagwey.ruleengine.model.Fact;
import com.haifachagwey.ruleengine.repository.FactRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FactService {

    private final FactRepository factRepository;
    private final FactMapper factMapper;
    private final RuleService ruleService;



    @Transactional
    public List<FactDTO> getAllFacts() {
        return factRepository.findAll().stream()
                .map(factMapper::toDTO)
                .toList();
    }

    @Transactional
    public FactDTO getFactById(Integer id) {
        return factRepository.findById(id)
                .map(factMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Fact not found with id: " + id));
    }

    @Transactional // added
    public FactDTO saveFact(FactDTO factDTO) {
        Fact fact = factMapper.toEntity(factDTO); // mapper already links children to parents
        Fact saved = factRepository.save(fact);
        ruleService.reloadRules();
        return factMapper.toDTO(saved);
    }

    @Transactional // added
    public void deleteFact(Integer id) {
        if (!factRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fact not found with id: " + id);
        }
        factRepository.deleteById(id);
        ruleService.reloadRules();
    }

}
