package com.haifachagwey.ruleengine.config;

import org.kie.api.KieServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

//    private static final String drl_file_path = "rules/discount-rules.drl";

    @Bean
    public KieServices kieServices() {
        return KieServices.Factory.get();
    }



}
