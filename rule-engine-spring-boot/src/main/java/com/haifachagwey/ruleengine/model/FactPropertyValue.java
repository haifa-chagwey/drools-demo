package com.haifachagwey.ruleengine.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString(exclude = "factProperty")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "fact_property_allowed_value")
public class FactPropertyValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "value", nullable = false)
    private String value;

    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fact_property_id")
    @JsonBackReference
    private FactProperty factProperty;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FactPropertyValue)) return false;
        FactPropertyValue that = (FactPropertyValue) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
