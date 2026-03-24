package com.haifachagwey.ruleengine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleCondition {

    private String targetField;

    private OperatorType operator; // GREATER_THAN, LESS_THAN, EQUALS, etc.

    private OperandType valueType;

    private String value;

}
