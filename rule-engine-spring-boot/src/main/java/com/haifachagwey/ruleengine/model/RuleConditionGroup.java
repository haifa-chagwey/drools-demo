package com.haifachagwey.ruleengine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleConditionGroup {

    private String combinator; // AND or OR

    private List<RuleCondition> conditions;

    private List<RuleConditionGroup> subConditionGroups;

}
