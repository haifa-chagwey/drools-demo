package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.*;
import com.haifachagwey.ruleengine.repository.FactActionRepository;
import com.haifachagwey.ruleengine.repository.FactPropertyRepository;
import com.haifachagwey.ruleengine.repository.FactTypeRepository;
import com.haifachagwey.ruleengine.repository.RuleRepository;
import com.haifachagwey.ruleengine.repository.TenantConfigRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
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
public class RuleService {

    private final RuleRepository ruleRepository;
    private final TenantConfigRepository tenantConfigRepository;
    private final KieServices kieServices;
    private final RuleDrlCompiler ruleDrlCompiler;
    private final FactPropertyRepository factPropertyRepository;
    private final FactTypeRepository factTypeRepository;
    private final FactActionRepository factActionRepository;
    private KieContainer kieContainer;

    public RuleService(RuleRepository ruleRepository, TenantConfigRepository tenantConfigRepository, KieServices kieServices, RuleDrlCompiler ruleDrlCompiler, FactPropertyRepository factPropertyRepository, FactTypeRepository factTypeRepository, FactActionRepository factActionRepository) {
        this.ruleRepository = ruleRepository;
        this.tenantConfigRepository = tenantConfigRepository;
        this.kieServices = kieServices;
        this.ruleDrlCompiler = ruleDrlCompiler;
        this.factPropertyRepository = factPropertyRepository;
        this.factTypeRepository = factTypeRepository;
        this.factActionRepository = factActionRepository;
    }

//    Rule Management

    @PostConstruct
    public void init() {
        reloadRules();
    }

    @Transactional
    public Rule saveRule(Rule rule) {
        if (rule.getFact() != null) {
            Fact fact = null;
            if (rule.getFact().getId() != null) {
                fact = factTypeRepository.findById(rule.getFact().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Unknown fact type ID: " + rule.getFact().getId()));
            } else if (rule.getFact().getName() != null) {
                fact = factTypeRepository.findByName(rule.getFact().getName())
                        .orElseThrow(() -> new IllegalArgumentException("Unknown fact type name: " + rule.getFact().getName()));
            }
            if (fact != null) {
                rule.setFact(fact);
            }
        }
        validateRule(rule);
        // Ensure child objects are linked correctly
        if (rule.getConditions() != null) {
            rule.getConditions().forEach(c -> c.setRule(rule));
        }
        if (rule.getActions() != null) {
            rule.getActions().forEach(a -> a.setRule(rule));
        }
        Rule saved = ruleRepository.save(rule);
        reloadRules();
        return saved;
    }

    public List<Rule> getAllRules() {
        return ruleRepository.findAll();
    }

    public void deleteRule(Long id) {
        ruleRepository.deleteById(id);
        reloadRules();
    }

    private void validateRule(Rule rule) {
        Fact ruleFact = null;
        if (rule.getFact() != null) {
            if (rule.getFact().getId() != null) {
                ruleFact = factTypeRepository.findById(rule.getFact().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Unknown fact ID: " + rule.getFact().getId()));
            } else if (rule.getFact().getName() != null) {
                ruleFact = factTypeRepository.findByName(rule.getFact().getName())
                        .orElseThrow(() -> new IllegalArgumentException("Unknown fact name: " + rule.getFact().getName()));
            }
        }
        
        if (ruleFact == null) {
            throw new IllegalArgumentException("Rule must be associated with a Fact Type");
        }
        
        rule.setFact(ruleFact);

        if (rule.getConditions() != null) {
            for (RuleCondition condition : rule.getConditions()) {
                if (condition.getFactProperty() != null) {
                    FactProperty factPropDef = factPropertyRepository.findById(condition.getFactProperty().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Unknown fact definition ID: " + condition.getFactProperty().getId()));
                    if (ruleFact != null && !factPropDef.getFact().getId().equals(ruleFact.getId())) {
                        throw new IllegalArgumentException("Condition property '" + factPropDef.getKey() + "' does not belong to domain '" + ruleFact.getName() + "'");
                    }
                    condition.setFactProperty(factPropDef);
                }
            }
        }
        if (rule.getActions() != null) {
            for (RuleAction action : rule.getActions()) {
                if (action.getFactAssociatedActionProperty() != null) {
                    FactAssociatedAction factAssociatedActionDef = factActionRepository.findById(action.getFactAssociatedActionProperty().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Unknown action definition ID: " + action.getFactAssociatedActionProperty().getId()));
                    if (ruleFact != null && !factAssociatedActionDef.getFact().getId().equals(ruleFact.getId())) {
                        throw new IllegalArgumentException("Action property '" + factAssociatedActionDef.getKey() + "' does not belong to domain '" + ruleFact.getName() + "'");
                    }
                    action.setFactAssociatedActionProperty(factAssociatedActionDef);
                }
            }
        }
    }

    @Transactional
    public synchronized void reloadRules() {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        List<Rule> rules = ruleRepository.findAll();
        for (Rule rule : rules) {
            String drlContent = ruleDrlCompiler.compile(rule);
            if (drlContent != null && !drlContent.isEmpty()) {
                kieFileSystem.write("src/main/resources/rules/" + rule.getName() + ".drl", drlContent);
            }
        }
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
        if (kieBuilder.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kieBuilder.getResults().toString());
        }
        this.kieContainer = kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());
    }

//    Rule Execution

    public Map<String, Object> executeRules(Map<String, Object> input, String tenantId) {
        if (kieContainer == null) {
            reloadRules();
        }
        KieSession kieSession = kieContainer.newKieSession();

        Map<String, Object> mergedConfigs = new HashMap<>();
        tenantConfigRepository.findByTenantId("GLOBAL")
                .forEach(c -> mergedConfigs.put(c.getConfigKey(), c.getConfigValue()));
        if (tenantId != null && !"GLOBAL".equals(tenantId)) {
            tenantConfigRepository.findByTenantId(tenantId)
                    .forEach(c -> mergedConfigs.put(c.getConfigKey(), c.getConfigValue()));
        }

        Map<String, Object> results = new HashMap<>();
        GlobalFact globalFact = GlobalFact.builder()
                .properties(input)
                .build();
        kieSession.setGlobal("outputs", results);
        kieSession.setGlobal("configs", mergedConfigs);
        kieSession.insert(globalFact);
        kieSession.fireAllRules();
        kieSession.dispose();
        return results;
    }
}
