package com.haifachagwey.ruleengine.repository;

import com.haifachagwey.ruleengine.model.AssociatedAction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssociatedActionRepository extends JpaRepository<AssociatedAction, Integer> {

    @Override
    @EntityGraph(attributePaths = {"allowedValues"})
    List<AssociatedAction> findAll();
}
