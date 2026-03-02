package com.haifachagwey.ruleengine.config;

import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EasyRulesConfig {

    @Bean
    public RulesEngine rulesEngine() {
        return new DefaultRulesEngine();
    }
}
