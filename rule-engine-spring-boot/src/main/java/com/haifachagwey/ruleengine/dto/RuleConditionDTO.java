package com.haifachagwey.ruleengine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleConditionDTO {
    private Long id;
    private Integer attributeId;
    private String attributeKey;
    private String attributeLabel;
    private String operator;
    private String value;
}
