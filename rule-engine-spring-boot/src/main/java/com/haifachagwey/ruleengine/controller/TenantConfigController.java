package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.model.TenantConfig;
import com.haifachagwey.ruleengine.repository.TenantConfigRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/admin/tenant-configs")
public class TenantConfigController {

    private final TenantConfigRepository tenantConfigRepository;

    public TenantConfigController(TenantConfigRepository tenantConfigRepository) {
        this.tenantConfigRepository = tenantConfigRepository;
    }

    @GetMapping("/{tenantId}")
    public List<TenantConfig> getConfigsByTenant(@PathVariable String tenantId) {
        return tenantConfigRepository.findByTenantId(tenantId);
    }

    @PostMapping
    public TenantConfig saveConfig(@RequestBody TenantConfig config) {
        return tenantConfigRepository.save(config);
    }

    @DeleteMapping("/{id}")
    public void deleteConfig(@PathVariable Integer id) {
        tenantConfigRepository.deleteById(id);
    }
}
