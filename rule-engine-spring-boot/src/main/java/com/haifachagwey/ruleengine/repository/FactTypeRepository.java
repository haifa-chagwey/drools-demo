package com.haifachagwey.ruleengine.repository;

import com.haifachagwey.ruleengine.model.FactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FactTypeRepository extends JpaRepository<FactType, Integer> {
    Optional<FactType> findByName(String name);
}
