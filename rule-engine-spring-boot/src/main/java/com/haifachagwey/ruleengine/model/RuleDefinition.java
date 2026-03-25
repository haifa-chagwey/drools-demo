package com.haifachagwey.ruleengine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleDefinition {

    private String description;

    private String combinator; // AND or OR
    
    @Builder.Default
    private List<RuleCondition> conditions = new ArrayList<>();

    @Builder.Default
    private List<RuleAction> actions = new ArrayList<>();

}
