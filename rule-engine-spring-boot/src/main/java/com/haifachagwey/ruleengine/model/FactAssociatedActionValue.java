package com.haifachagwey.ruleengine.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "action_allowed_value")
public class FactAssociatedActionValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "value", nullable = false)
    private String value;

    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id")
    @JsonBackReference
    private FactAssociatedAction factAssociatedAction;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FactAssociatedActionValue)) return false;
        FactAssociatedActionValue that = (FactAssociatedActionValue) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
