package com.haifachagwey.ruleengine.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "rule_conditions")
public class RuleCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String operator;

    private String rightOperandType;

    private String constantValue;

    private String configKey;

    private Integer orderIndex;

    @Builder.Default
    private boolean enabled = true;

    @ManyToOne
    @JoinColumn(name = "rule_id")
    private Rule rule;

    @ManyToOne
    @JoinColumn(name = "fact_definition_id")
    private Fact factDefinition;

}
