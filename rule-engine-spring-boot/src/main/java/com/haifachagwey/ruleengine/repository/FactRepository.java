package com.haifachagwey.ruleengine.repository;

import com.haifachagwey.ruleengine.model.Fact;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FactRepository extends JpaRepository<Fact, Integer> {
    Optional<Fact> findByName(String name);

    @Override
//    @EntityGraph(attributePaths = {"properties", "properties.allowedValues", "associatedActions", "associatedActions.allowedValues"})
    List<Fact> findAll();

    @Override
//    @EntityGraph(attributePaths = {"properties", "properties.allowedValues", "associatedActions", "associatedActions.allowedValues"})
    Optional<Fact> findById(Integer id);
}
