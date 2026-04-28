package com.haifachagwey.ruleengine.dto;

import com.haifachagwey.ruleengine.model.AttributeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactAttributeDTO {
    private Integer id;
    private String key;
    private String label;
    private AttributeType type;
    private List<AttributeOptionDTO> options;
}
