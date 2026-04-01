package com.haifachagwey.ruleengine.repository;

import com.haifachagwey.ruleengine.model.ActionDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActionDefinitionRepository extends JpaRepository<ActionDefinition, Integer> {
    Optional<ActionDefinition> findByKey(String key);
}
