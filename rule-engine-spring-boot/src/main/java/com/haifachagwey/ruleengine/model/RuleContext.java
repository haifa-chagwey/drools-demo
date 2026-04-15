package com.haifachagwey.ruleengine.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

// Fact

@Getter
@Setter
public class RuleContext {

//    input from decision caller
    private Map<String, Object> inputs;
    private Map<String, Object> outputs;
    private Map<String, Object> configs;

    public RuleContext() {}

    public RuleContext(Map<String, Object> inputs, Map<String, Object> outputs, Map<String, Object> configs) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.configs = configs;
    }


    public Object getInput(String key) { return inputs.get(key); }
    public void setInput(String key, Object value) { inputs.put(key, value); }

    public Object getOutput(String key) { return outputs.get(key); }
    public void setOutput(String key, Object value) { outputs.put(key, value); }


    public Object getConfig(String key) { return configs.get(key); }
    public void setConfig(String key, Object value) { configs.put(key, value); }


}
