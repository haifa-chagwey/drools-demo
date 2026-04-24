package com.haifachagwey.ruleengine.repository;

import com.haifachagwey.ruleengine.model.FactAssociatedAction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FactActionRepository extends JpaRepository<FactAssociatedAction, Integer> {
    Optional<FactAssociatedAction> findByKey(String key);
    @Override
    @EntityGraph(attributePaths = {"allowedValues"})
    List<FactAssociatedAction> findAll();

    @EntityGraph(attributePaths = {"allowedValues"})
    List<FactAssociatedAction> findByFactId(Integer factId);
}
