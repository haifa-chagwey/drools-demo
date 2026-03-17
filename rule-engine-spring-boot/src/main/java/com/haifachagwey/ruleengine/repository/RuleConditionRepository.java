package com.haifachagwey.ruleengine.repository;

import com.haifachagwey.ruleengine.model.RuleCondition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleConditionRepository extends JpaRepository<RuleCondition, Integer> {
}
