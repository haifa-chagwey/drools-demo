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

    private String leftOperand;

    @Enumerated(EnumType.STRING)
    private OperatorType operator; // ==, !=, >, <, >=, <=, contains, in

    @Enumerated(EnumType.STRING)
    private OperandType rightOperandType;

    private String rightOperandValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @JsonBackReference("group-conditions")
    private RuleConditionGroup group;

}
