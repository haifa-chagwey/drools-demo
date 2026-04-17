package com.haifachagwey.ruleengine.repository;

import com.haifachagwey.ruleengine.model.Action;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActionRepository extends JpaRepository<Action, Integer> {
    Optional<Action> findByKey(String key);
    @Override
    @EntityGraph(attributePaths = {"allowedValues"})
    List<Action> findAll();

    @EntityGraph(attributePaths = {"allowedValues"})
    List<Action> findByFactTypeId(Integer factTypeId);
}
