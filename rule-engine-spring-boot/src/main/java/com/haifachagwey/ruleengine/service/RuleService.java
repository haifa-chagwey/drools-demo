package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.*;
import com.haifachagwey.ruleengine.repository.FactDefinitionRepository;
import com.haifachagwey.ruleengine.repository.RuleRepository;
import com.haifachagwey.ruleengine.repository.TenantConfigRepository;
import jakarta.annotation.PostConstruct;
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
    private final FactDefinitionRepository factDefinitionRepository;
    private KieContainer kieContainer;

    public RuleService(RuleRepository ruleRepository, TenantConfigRepository tenantConfigRepository, KieServices kieServices, RuleDrlCompiler ruleDrlCompiler, FactDefinitionRepository factDefinitionRepository) {
        this.ruleRepository = ruleRepository;
        this.tenantConfigRepository = tenantConfigRepository;
        this.kieServices = kieServices;
        this.ruleDrlCompiler = ruleDrlCompiler;
        this.factDefinitionRepository = factDefinitionRepository;
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

    private void validateRule(Rule rule) {
        if (rule.getConditions() != null) {
            for (RuleCondition condition : rule.getConditions()) {
                if (condition.getFactDefinition() != null) {
                    factDefinitionRepository.findById(condition.getFactDefinition().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Unknown fact definition ID: " + condition.getFactDefinition().getId()));
                }
            }
        }
    }

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
        KieSession kieSession = kieContainer.newKieSession();

//        Create a new context
        RuleContext ruleContext = new RuleContext();
//        Insert facts into context
        ruleContext.setFacts(input);

//        Prepare tenant configs and insert into context
        Map<String, Object> mergedConfigs = new HashMap<>();
        tenantConfigRepository.findByTenantId("GLOBAL")
                .forEach(c -> mergedConfigs.put(c.getConfigKey(), c.getConfigValue()));
        if (tenantId != null && !"GLOBAL".equals(tenantId)) {
            tenantConfigRepository.findByTenantId(tenantId)
                    .forEach(c -> mergedConfigs.put(c.getConfigKey(), c.getConfigValue()));
        }
        ruleContext.setTenantConfig(mergedConfigs);
//        Execute rules
        kieSession.insert(ruleContext);
        kieSession.fireAllRules();
        kieSession.dispose();
        return ruleContext.getResults();
    }
}
