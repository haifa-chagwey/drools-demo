package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.dto.RuleDTO;
import com.haifachagwey.ruleengine.exception.ResourceNotFoundException;
import com.haifachagwey.ruleengine.mapper.RuleMapper;
import com.haifachagwey.ruleengine.model.*;
import com.haifachagwey.ruleengine.repository.*;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RuleService {

    private final RuleRepository ruleRepository;
    private final TenantConfigRepository tenantConfigRepository;
    private final KieServices kieServices;
    private final RuleDrlCompiler ruleDrlCompiler;
    private final FactAttributeRepository factAttributeRepository;
    private final FactRepository factRepository;
    private final AssociatedActionRepository associatedActionRepository;
    private final RuleMapper ruleMapper;
    private KieContainer kieContainer;


    @PostConstruct
    public void init() {
        reloadRules();
    }

    @Transactional
    public RuleDTO saveRule(RuleDTO ruleDTO) {
        Rule rule = ruleMapper.toEntity(ruleDTO);
        validateRule(rule);
        Rule saved = ruleRepository.save(rule);
        reloadRules();
        return ruleMapper.toDTO(saved);
    }

    @Transactional
    public List<RuleDTO> getAllRules() {
        return ruleRepository.findAll().stream()
                .map(ruleMapper::toDTO)
                .toList();
    }

    @Transactional
    public RuleDTO getRuleById(Long id) {
        return ruleRepository.findById(id)
                .map(ruleMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found with id: " + id));
    }

    @Transactional
    public void deleteRule(Long id) {
        if (!ruleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rule not found with id: " + id);
        }
        ruleRepository.deleteById(id);
        reloadRules();
    }

    private void validateRule(Rule rule) {
        if (rule.getFact() == null) {
            throw new ValidationException("Rule must be associated with a Fact Type");
        }
        Fact ruleFact = factRepository.findById(rule.getFact().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unknown fact ID: " + rule.getFact().getId()));

        rule.setFact(ruleFact);

        validateConditions(rule.getConditions(), ruleFact);
        validateActions(rule.getActions(), ruleFact);
    }

    private void validateConditions(List<RuleCondition> conditions, Fact ruleFact) {
        if (conditions == null) return;
        for (RuleCondition condition : conditions) {
            if (condition.getAttribute() != null) {
                FactAttribute factAttr = factAttributeRepository.findById(condition.getAttribute().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Unknown fact attribute: " + condition.getAttribute().getId()));
                if (!factAttr.getFact().getId().equals(ruleFact.getId())) {
                    throw new ValidationException("Condition attribute '" + factAttr.getKey() + "' does not belong to domain '" + ruleFact.getName() + "'");
                }
                condition.setAttribute(factAttr);
            }
        }
    }

    private void validateActions(List<RuleAction> actions, Fact ruleFact) {
        if (actions == null) return;
        for (RuleAction action : actions) {
            if (action.getAction() != null) {
                AssociatedAction associatedActionDef = associatedActionRepository.findById(action.getAction().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Unknown action definition ID: " + action.getAction().getId()));
                if (!associatedActionDef.getFact().getId().equals(ruleFact.getId())) {
                    throw new IllegalArgumentException("Action '" + associatedActionDef.getKey() + "' does not belong to domain '" + ruleFact.getName() + "'");
                }
                action.setAction(associatedActionDef);
            }
        }
    }

    @Transactional
    public synchronized void reloadRules() {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        List<Rule> rules = ruleRepository.findAll();
        for (Rule rule : rules) {
            if (rule.isActive()) {
                String drlContent = ruleDrlCompiler.compile(rule);
                if (drlContent != null && !drlContent.isEmpty()) {
                    System.out.println("Compiled DRL for rule '" + rule.getName() + "':\n" + drlContent);
                    kieFileSystem.write("src/main/resources/rules/" + rule.getName() + "_" + rule.getId() + ".drl", drlContent);
                }
            }
        }
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
        if (kieBuilder.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kieBuilder.getResults().toString());
        }

        KieContainer oldContainer = this.kieContainer;
        this.kieContainer = kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());

        if (oldContainer != null) {
            oldContainer.dispose();
        }
    }

//    Rule Execution

    public Map<String, Object> executeRules(Map<String, Object> input, String tenantId) {
        if (kieContainer == null) {
            reloadRules();
        }
        KieSession kieSession = kieContainer.newKieSession();

//        Map<String, Object> mergedConfigs = new HashMap<>();
//        tenantConfigRepository.findByTenantId("GLOBAL")
//                .forEach(c -> mergedConfigs.put(c.getConfigKey(), c.getConfigValue()));
//        if (tenantId != null && !"GLOBAL".equals(tenantId)) {
//            tenantConfigRepository.findByTenantId(tenantId)
//                    .forEach(c -> mergedConfigs.put(c.getConfigKey(), c.getConfigValue()));
//        }

        Map<String, Object> results = new HashMap<>();
//        Fact globalFact = Fact.builder()
//                .attributes(input)
//                .build();
        kieSession.setGlobal("outputs", results);
//        kieSession.setGlobal("configs", mergedConfigs);
//        kieSession.insert(globalFact);
        kieSession.fireAllRules();
        kieSession.dispose();
        return results;
    }
}
