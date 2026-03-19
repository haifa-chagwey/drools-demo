package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.*;
import com.haifachagwey.ruleengine.repository.RuleRepository;
import com.haifachagwey.ruleengine.repository.TenantConfigRepository;
import jakarta.annotation.PostConstruct;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;
    private final TenantConfigRepository tenantConfigRepository;
    private final KieServices kieServices;
    private KieContainer kieContainer;

    public RuleService(RuleRepository ruleRepository, TenantConfigRepository tenantConfigRepository, KieServices kieServices) {
        this.ruleRepository = ruleRepository;
        this.tenantConfigRepository = tenantConfigRepository;
        this.kieServices = kieServices;
    }

    @PostConstruct
    public void init() {
        reloadRules();
    }

    public synchronized void reloadRules() {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        List<Rule> rules = ruleRepository.findAll();

        for (Rule rule : rules) {
            String drlContent = generateDrl(rule);
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

    private String generateDrl(Rule rule) {
        if (rule.getConditions() == null || rule.getConditions().isEmpty() ||
            rule.getActions() == null || rule.getActions().isEmpty()) {
            return null;
        }

        StringBuilder drl = new StringBuilder();
        drl.append("package rules;\n");
        drl.append("import com.haifachagwey.ruleengine.model.RuleContext;\n");

        drl.append("dialect \"mvel\"\n");

        drl.append(String.format("rule \"%s\"\n", rule.getName()));
        drl.append("when\n");
        
        List<String> conditionStrings = new ArrayList<>();
        for (RuleCondition cond : rule.getConditions()) {
            String condition = String.format("facts[\"%s\"] %s tenantConfigs[\"%s\"]",
                    cond.getFieldName(),
                    cond.getOperator(),
                    cond.getThresholdKey());
            conditionStrings.add(condition);
        }
        
        drl.append("    $c : RuleContext(").append(String.join(" && ", conditionStrings)).append(")\n");
        drl.append("then\n");
        
        for (RuleAction action : rule.getActions()) {
            drl.append(String.format("    $c.setResult(\"%s\", \"%s\");\n",
                    action.getOutputKey(), action.getOutputValue()));
        }
        
        drl.append("end");
        System.out.println(drl.toString());
        return drl.toString();
    }

    public Map<String, Object> executeRules(Map<String, Object> input, String tenantId) {
        KieSession kieSession = kieContainer.newKieSession();


        RuleContext ruleContext = new RuleContext();
        ruleContext.setFacts(input);
        Map<String, Object> mergedConfigs = new HashMap<>();
        tenantConfigRepository.findByTenantId("GLOBAL")
                .forEach(c -> mergedConfigs.put(c.getConfigKey(), c.getConfigValue()));

        if (tenantId != null && !"GLOBAL".equals(tenantId)) {
            tenantConfigRepository.findByTenantId(tenantId)
                    .forEach(c -> mergedConfigs.put(c.getConfigKey(), c.getConfigValue()));
        }
        ruleContext.setTenantConfig(mergedConfigs);

        kieSession.insert(ruleContext);
        kieSession.fireAllRules();
        kieSession.dispose();
        return ruleContext.getResults();
    }
}
