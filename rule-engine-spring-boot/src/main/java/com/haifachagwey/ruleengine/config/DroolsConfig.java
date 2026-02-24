package com.haifachagwey.ruleengine.config;

import org.kie.api.KieServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

//    Main Drools entry point
    @Bean
    public KieServices kieServices() {
        return KieServices.Factory.get();
    }
}
