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
public class FactDTO {
    private Integer id;
    private String name;
    private String description;
    private List<FactAttributeDTO> attributes;
    private List<AssociatedActionDTO> associatedActions;
}
