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
@Table(name = "rule_actions")
public class RuleAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String outputValue;

    private String valueType;

    private Integer orderIndex;

    @Builder.Default
    private boolean enabled = true;

    @ManyToOne
    @JoinColumn(name = "rule_id")
    private Rule rule;

    @ManyToOne
    @JoinColumn(name = "action_definition_id")
    private ActionDefinition actionDefinition;

}
