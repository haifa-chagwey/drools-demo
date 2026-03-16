package com.haifachagwey.ruleengine.repository;

import com.haifachagwey.ruleengine.model.TenantConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantConfigRepository extends JpaRepository<TenantConfig, Integer> {
    List<TenantConfig> findByTenantId(String tenantId);
}
