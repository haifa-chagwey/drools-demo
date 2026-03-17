package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.Rule;
import com.haifachagwey.ruleengine.model.RuleAction;
import com.haifachagwey.ruleengine.model.RuleCondition;
import com.haifachagwey.ruleengine.model.TenantConfig;
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
        if (rule.getDrl() != null && !rule.getDrl().isEmpty()) {
            String drl = rule.getDrl();
            if (!drl.contains("global java.util.Map tenantConfigs;")) {
                drl = drl.contains("package")
                        ? drl.replaceFirst("(?m)^package\\s+.*\\s*;", "$0\nglobal java.util.Map tenantConfigs;")
                        : "global java.util.Map tenantConfigs;\n" + drl;
            }
            return drl;
        }

        if (rule.getConditions() == null || rule.getConditions().isEmpty() ||
            rule.getActions() == null || rule.getActions().isEmpty()) {
            return null;
        }

        StringBuilder drl = new StringBuilder();
        drl.append("package rules;\n");
        drl.append("import java.util.Map;\n");
        drl.append("global java.util.Map output;\n");
        drl.append("global java.util.Map tenantConfigs;\n\n");
        drl.append(String.format("rule \"%s\"\n", rule.getName()));
        drl.append("when\n");
        
        List<String> conditionStrings = new ArrayList<>();
        for (RuleCondition cond : rule.getConditions()) {
            String parser = getParser(cond.getThresholdType());
            String condition = String.format("this[\"%s\"] %s %s((String)tenantConfigs.get(\"%s\"))",
                    cond.getFieldName(),
                    cond.getOperator(),
                    parser,
                    cond.getThresholdKey());
            conditionStrings.add(condition);
        }
        
        drl.append("    $input : Map(").append(String.join(" && ", conditionStrings)).append(")\n");
        drl.append("then\n");
        
        for (RuleAction action : rule.getActions()) {
            drl.append(String.format("    output.put(\"%s\", \"%s\");\n", 
                    action.getOutputKey(), action.getOutputValue()));
        }
        
        drl.append("end");
        return drl.toString();
    }

    private String getParser(String type) {
        if (type == null) return "String.valueOf";
        switch (type) {
            case "Integer": return "Integer.parseInt";
            case "Double": return "Double.parseDouble";
            case "Boolean": return "Boolean.parseBoolean";
            default: return "(String)";
        }
    }

    public Map<String, Object> executeRules(Map<String, Object> input, String tenantId) {
        KieSession kieSession = kieContainer.newKieSession();
        Map<String, Object> output = new HashMap<>();
        Map<String, Object> mergedConfigs = new HashMap<>();

        tenantConfigRepository.findByTenantId("GLOBAL")
                .forEach(c -> mergedConfigs.put(c.getConfigKey(), c.getConfigValue()));

        if (tenantId != null && !"GLOBAL".equals(tenantId)) {
            tenantConfigRepository.findByTenantId(tenantId)
                    .forEach(c -> mergedConfigs.put(c.getConfigKey(), c.getConfigValue()));
        }

        kieSession.setGlobal("output", output);
        kieSession.setGlobal("tenantConfigs", mergedConfigs);
        kieSession.insert(input);
        kieSession.fireAllRules();
        kieSession.dispose();
        return output;
    }
}
