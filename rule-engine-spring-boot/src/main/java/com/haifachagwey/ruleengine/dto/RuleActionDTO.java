package com.haifachagwey.ruleengine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleActionDTO {
    private Long id;
    private Integer actionId;
    private String actionKey;
    private String actionLabel;
    private String value;
}
