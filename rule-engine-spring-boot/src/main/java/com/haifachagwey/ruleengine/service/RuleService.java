package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.*;
import com.haifachagwey.ruleengine.repository.FactPropertyRepository;
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
    private KieContainer kieContainer;

    public RuleService(RuleRepository ruleRepository, TenantConfigRepository tenantConfigRepository, KieServices kieServices, RuleDrlCompiler ruleDrlCompiler, FactPropertyRepository factPropertyRepository) {
        this.ruleRepository = ruleRepository;
        this.tenantConfigRepository = tenantConfigRepository;
        this.kieServices = kieServices;
        this.ruleDrlCompiler = ruleDrlCompiler;
        this.factPropertyRepository = factPropertyRepository;
    }

//    Rule Management

    @PostConstruct
    public void init() {
        reloadRules();
    }

    public Rule saveRule(Rule rule) {
        validateRule(rule);
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
        if (rule.getConditions() != null) {
            for (RuleCondition condition : rule.getConditions()) {
                if (condition.getFactPropertyDefinition() != null) {
                    factPropertyRepository.findById(condition.getFactPropertyDefinition().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Unknown fact definition ID: " + condition.getFactPropertyDefinition().getId()));
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

        RuleContext ruleContext = new RuleContext();
        ruleContext.setFacts(input);
        ruleContext.setTenantConfig(mergedConfigs);
        ruleContext.setResults(results);

        kieSession.insert(ruleContext);
        kieSession.fireAllRules();
        kieSession.dispose();
        return ruleContext.getResults();
    }
}
