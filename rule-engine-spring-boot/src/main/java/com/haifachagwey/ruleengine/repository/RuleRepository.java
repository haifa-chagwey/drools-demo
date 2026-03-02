package com.haifachagwey.ruleengine.repository;
import com.haifachagwey.ruleengine.model.RuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends JpaRepository<RuleEntity, Integer> {
}
