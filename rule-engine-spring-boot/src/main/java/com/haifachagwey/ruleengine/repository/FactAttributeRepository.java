package com.haifachagwey.ruleengine.repository;

import com.haifachagwey.ruleengine.model.FactAttribute;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FactAttributeRepository extends JpaRepository<FactAttribute, Integer> {
    Optional<FactAttribute> findByKey(String key);
    @Override
    @EntityGraph(attributePaths = {"allowedValues"})
    List<FactAttribute> findAll();

    @EntityGraph(attributePaths = {"allowedValues"})
    List<FactAttribute> findByFactId(Integer factId);
}
