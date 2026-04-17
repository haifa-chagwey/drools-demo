package com.haifachagwey.ruleengine.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString(exclude = {"factType", "allowedValues"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "action")
public class Action {

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
    @JoinColumn(name = "fact_type_id")
    @JsonBackReference
    private FactType factType;

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private Set<ActionAllowedValue> allowedValues = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return Objects.equals(id, action.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
