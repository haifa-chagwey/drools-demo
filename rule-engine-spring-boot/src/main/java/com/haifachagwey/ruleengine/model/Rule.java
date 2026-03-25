package com.haifachagwey.ruleengine.model;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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

    @Column(unique = true, nullable = false)
    private String name;

    private boolean enabled = true;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private RuleDefinition definition;

    private String dateCreated;
    private String dateModified;
    private String createdBy;
    private String modifiedBy;

    @PrePersist
    protected void onCreate() {
        dateCreated = java.time.LocalDateTime.now().toString();
        dateModified = dateCreated;
    }

    @PreUpdate
    protected void onUpdate() {
        dateModified = java.time.LocalDateTime.now().toString();
    }

}
