package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.Rule;
import com.haifachagwey.ruleengine.repository.RuleRepository;
import jakarta.annotation.PostConstruct;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;
    private final KieServices kieServices;
    private KieContainer kieContainer;

    public RuleService(RuleRepository ruleRepository, KieServices kieServices) {
        this.ruleRepository = ruleRepository;
        this.kieServices = kieServices;
    }

//     Load & compile all rules from DB When application starts
    @PostConstruct
    public void init() {
        reloadRules();
    }

    public synchronized void reloadRules() {

//        Create KieFileSystem
//        A virtual folder where you put .drl files before compiling them.
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        List<Rule> rules = ruleRepository.findAll();

        for (Rule rule : rules) {
            String drlContent = "";
            if (rule.getDrl() != null && !rule.getDrl().isEmpty()) {
                drlContent = rule.getDrl();
            } else if (rule.getCondition() != null && rule.getAction() != null) {
                // Construct DRL from condition and action if DRL is missing
                drlContent = "package rules;\n" +
                        "import java.util.Map;\n" +
                        "global java.util.Map output;\n" +
                        "rule \"" + rule.getName() + "\"\n" +
                        "when\n" +
                        "    $input : Map(" + rule.getCondition() + ")\n" +
                        "then\n" +
                        "    " + rule.getAction() + "\n" +
                        "end";
            }

            if (!drlContent.isEmpty()) {
                kieFileSystem.write("src/main/resources/rules/" + rule.getName() + ".drl", drlContent);
            }
        }

//        Compile all DRL files
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

//        Check errors (Syntax errors)
        if (kieBuilder.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kieBuilder.getResults().toString());
        }
//        Create KieContainer
//        When rules are compiled → they go inside KieContainer
//        When executing rules → you create sessions from this container
        KieModule kieModule = kieBuilder.getKieModule();
        this.kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
    }

    public Map<String, Object> executeRules(Map<String, Object> input) {
        if (kieContainer == null) {
            reloadRules();
        }
        KieSession kieSession = kieContainer.newKieSession();
        Map<String, Object> output = new ConcurrentHashMap<>();
        kieSession.setGlobal("output", output);
        kieSession.insert(input);
        kieSession.fireAllRules();
        kieSession.dispose();
        return output;
    }
}
