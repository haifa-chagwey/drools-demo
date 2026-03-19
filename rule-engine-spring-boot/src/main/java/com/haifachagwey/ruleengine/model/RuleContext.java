package com.haifachagwey.ruleengine.model;

import java.util.HashMap;
import java.util.Map;

public class RuleContext {

    private final Map<String, Object> facts = new HashMap<>();
    private final Map<String, Object> results = new HashMap<>();
    private final Map<String, Object> tenantConfigs = new HashMap<>();

    public Object getFact(String key) { return facts.get(key); }
    public void setFact(String key, Object value) { facts.put(key, value); }
    public void setFacts(Map<String, Object> facts) { this.facts.putAll(facts); }
    public Map<String, Object> getFacts() { return facts; }

    public Object getResult(String key) { return results.get(key); }
    public void setResult(String key, Object value) { results.put(key, value); }
    public Map<String, Object> getResults() { return results; }
    public void setResults(Map<String, Object> results) { this.results.putAll(results); }

    public Object getTenantConfig(String key) { return tenantConfigs.get(key); }
    public void setTenantConfig(String key, Object value) { tenantConfigs.put(key, value); }
    public Map<String, Object> getTenantConfigs() { return tenantConfigs; }
    public void setTenantConfig(Map<String, Object> tenantConfigs) { this.tenantConfigs.putAll(tenantConfigs); }

}
