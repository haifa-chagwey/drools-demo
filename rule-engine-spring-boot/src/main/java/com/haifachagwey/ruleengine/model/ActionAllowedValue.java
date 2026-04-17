package com.haifachagwey.ruleengine.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString(exclude = "action")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "action_allowed_value")
public class ActionAllowedValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "value", nullable = false)
    private String value;

    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id")
    @JsonBackReference
    private Action action;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionAllowedValue that = (ActionAllowedValue) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
