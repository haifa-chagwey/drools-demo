package com.haifachagwey.ruleengine.repository;

import com.haifachagwey.ruleengine.model.Fact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FactDefinitionRepository extends JpaRepository<Fact, Integer> {
    Optional<Fact> findByKey(String key);
}
