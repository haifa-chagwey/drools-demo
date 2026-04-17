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
@Table(name = "rule_condition")
public class RuleCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fact_property_id")
    private FactProperty factProperty;

    private String operator;

    private String value;

    @ManyToOne
    @JoinColumn(name = "rule_id")
    private Rule rule;



}
