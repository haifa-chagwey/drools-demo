package com.haifachagwey.ruleengine.repository;

import com.haifachagwey.ruleengine.model.FactPropertyAllowedValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactPropertyAllowedValueRepository extends JpaRepository<FactPropertyAllowedValue, Integer> {
    FactPropertyAllowedValue findByValue(String value);
}
