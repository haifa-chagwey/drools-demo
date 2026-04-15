package com.haifachagwey.ruleengine.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "action_property")
public class ActionProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String key;

    @Column(nullable = false)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FactPropertyType type;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_type_id")
    @JsonBackReference
    private ActionType actionType;

    @OneToMany(mappedBy = "actionProperty", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ActionPropertyAllowedValue> allowedValues;

}
