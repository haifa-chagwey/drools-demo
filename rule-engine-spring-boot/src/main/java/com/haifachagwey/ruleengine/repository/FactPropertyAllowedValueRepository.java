package com.haifachagwey.ruleengine.repository;

import com.haifachagwey.ruleengine.model.FactPropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactPropertyAllowedValueRepository extends JpaRepository<FactPropertyValue, Integer> {
    FactPropertyValue findByValue(String value);
}
