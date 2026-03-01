package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.OrderDiscount;
import com.haifachagwey.ruleengine.model.OrderRequest;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService {


    private final KieServices kieServices;

    public OrderService(KieServices kieServices) {
        this.kieServices = kieServices;
    }

    public OrderDiscount evaluate(OrderRequest orderRequest) {

        String drl_file_path = "rules/discount-rules.drl";

        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource(drl_file_path));

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        KieModule kieModule = kieBuilder.getKieModule();

        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());

        KieSession kieSession = kieContainer.newKieSession();

        OrderDiscount orderDiscount = new OrderDiscount();
        kieSession.setGlobal("orderDiscount", orderDiscount);
        kieSession.insert(orderRequest);

        kieSession.fireAllRules();
        kieSession.dispose();

        return orderDiscount;
    }

}