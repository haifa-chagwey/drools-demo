package com.haifachagwey.ruleengine.service;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RuleService {


    private final KieServices kieServices;

    public RuleService(KieServices kieServices) {
        this.kieServices = kieServices;
    }

    public Map<String, Object> evaluate(Map<String, Object> input) {

        String drl_file_path = "rules/discount-rules.drl";

        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource(drl_file_path));

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        KieModule kieModule = kieBuilder.getKieModule();

        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());

        KieSession kieSession = kieContainer.newKieSession();

        Map<String, Object> output = new java.util.HashMap<>();
        kieSession.setGlobal("output", output);
        kieSession.insert(input);

        kieSession.fireAllRules();
        kieSession.dispose();

        return output;
    }

}