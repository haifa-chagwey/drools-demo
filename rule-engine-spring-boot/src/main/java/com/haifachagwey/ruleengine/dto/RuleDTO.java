package com.haifachagwey.ruleengine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleDTO {
    private Long id;
    private String name;
    private String description;
    private Long factId;
    private String factName;
    private boolean active;
    private List<RuleConditionDTO> conditions;
    private List<RuleActionDTO> actions;
}
