package com.haifachagwey.ruleengine.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@ToString(exclude = {"properties", "actions"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "fact_type")
public class FactType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @OneToMany(mappedBy = "factType", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private Set<FactProperty> properties = new HashSet<>();

    @OneToMany(mappedBy = "factType", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private Set<Action> actions = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FactType factType = (FactType) o;
        return Objects.equals(id, factType.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
