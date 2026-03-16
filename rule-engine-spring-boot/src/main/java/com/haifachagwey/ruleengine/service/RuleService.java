package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.Rule;
import com.haifachagwey.ruleengine.model.TenantConfig;
import com.haifachagwey.ruleengine.repository.RuleRepository;
import com.haifachagwey.ruleengine.repository.TenantConfigRepository;
import jakarta.annotation.PostConstruct;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
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
    private KieContainer kieContainer;

    public RuleService(RuleRepository ruleRepository, TenantConfigRepository tenantConfigRepository, KieServices kieServices) {
        this.ruleRepository = ruleRepository;
        this.tenantConfigRepository = tenantConfigRepository;
        this.kieServices = kieServices;
    }

//     Load & compile all rules from DB When application starts
    @PostConstruct
    public void init() {
        reloadRules();
    }

    public synchronized void reloadRules() {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        List<Rule> rules = ruleRepository.findAll();

        for (Rule rule : rules) {
            String drlContent = "";
                if (rule.getCondition() != null && rule.getAction() != null) {
                drlContent = String.format(
                    "package rules;\nimport java.util.Map;\nglobal java.util.Map output;\nglobal java.util.Map tenantConfigs;\nrule \"%s\"\nwhen\n    $input : Map(%s)\nthen\n    %s\nend",
                    rule.getName(), rule.getCondition(), rule.getAction()
                );
            }

            if (!drlContent.isEmpty()) {
                System.out.println("drlContent: " + drlContent);
                kieFileSystem.write("src/main/resources/rules/" + rule.getName() + ".drl", drlContent);
            }
        }

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
        if (kieBuilder.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kieBuilder.getResults().toString());
        }
        this.kieContainer = kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());
    }

//    public Map<String, Object> executeRules(Map<String, Object> input) {
//        return executeRules(input, null);
//    }

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
