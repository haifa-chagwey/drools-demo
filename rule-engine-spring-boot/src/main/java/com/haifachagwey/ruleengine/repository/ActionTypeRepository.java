package com.haifachagwey.ruleengine.repository;

import com.haifachagwey.ruleengine.model.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActionTypeRepository extends JpaRepository<ActionType, Integer> {
    Optional<ActionType> findByName(String name);
}
