package com.haifachagwey.ruleengine.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = {"fact", "allowedValues"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "fact_associated_action")
public class FactAssociatedAction {

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
    @JoinColumn(name = "fact_id")
    @JsonBackReference
    private Fact fact;

    @OneToMany(mappedBy = "factAssociatedAction", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private Set<FactAssociatedActionValue> allowedValues = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FactAssociatedAction)) return false;
        FactAssociatedAction factAssociatedAction = (FactAssociatedAction) o;
        return id != null && id.equals(factAssociatedAction.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
