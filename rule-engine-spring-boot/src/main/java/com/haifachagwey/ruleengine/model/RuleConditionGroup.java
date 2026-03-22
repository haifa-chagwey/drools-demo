package com.haifachagwey.ruleengine.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "rule_condition_groups")
public class RuleConditionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String combinator; // AND or OR

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    @JsonBackReference
    private Rule rule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_group_id")
    @JsonBackReference
    private RuleConditionGroup parentGroup;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RuleCondition> conditions;

    @OneToMany(mappedBy = "parentGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RuleConditionGroup> childGroups;
}
