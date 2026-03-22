package com.haifachagwey.ruleengine.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private Integer id;

    private String operator; // ==, !=, >, <, >=, <=, contains, in

    @Enumerated(EnumType.STRING)
    private OperandType leftType;

    private String leftValue;

    @Enumerated(EnumType.STRING)
    private OperandType rightType;

    private String rightValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @JsonBackReference
    private RuleConditionGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    @JsonBackReference
    private Rule rule;

}
