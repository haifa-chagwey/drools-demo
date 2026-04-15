package com.haifachagwey.ruleengine.model;

import java.util.Map;

public class GlobalFact {

    private String factType;  // just a String: "Device", "Payment", "Contract" ...
    private Map<String, Object> properties;
}
