package com.haifachagwey.ruleengine.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "associated_action")
public class AssociatedAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String key;

    @Column(nullable = false)
    private String label;


    /*
     * Many-to-One relationship with Fact
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fact_id")
    @JsonIgnore
    private Fact fact;

    /*
     * One-to-Many relationship with ActionOption
     */

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ActionOption> options = new java.util.ArrayList<>();

    /*
     * One-to-Many relationship with RuleAction
     */

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private List<RuleAction> ruleActions = new ArrayList<>();

}
