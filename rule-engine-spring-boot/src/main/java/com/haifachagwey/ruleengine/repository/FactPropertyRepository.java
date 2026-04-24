package com.haifachagwey.ruleengine.repository;

import com.haifachagwey.ruleengine.model.FactProperty;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FactPropertyRepository extends JpaRepository<FactProperty, Integer> {
    Optional<FactProperty> findByKey(String key);
    @Override
    @EntityGraph(attributePaths = {"allowedValues"})
    List<FactProperty> findAll();

    @EntityGraph(attributePaths = {"allowedValues"})
    List<FactProperty> findByFactId(Integer factId);
}
