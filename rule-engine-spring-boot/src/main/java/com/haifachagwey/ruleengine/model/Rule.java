package com.haifachagwey.ruleengine.model;

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
@Table(name = "rules")
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference("rule-condition-groups")
    private List<RuleConditionGroup> conditionGroups;

    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference("rule-actions")
    private List<RuleAction> actions;

    private String dateCreated;
    private String dateModified;
    private String createdBy;
    private String modifiedBy;

}
