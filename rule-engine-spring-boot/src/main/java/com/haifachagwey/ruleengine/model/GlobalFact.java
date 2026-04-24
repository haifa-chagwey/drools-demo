package com.haifachagwey.ruleengine.model;

import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalFact {
    private String factType;  // just a String: "Device", "Payment", "Contract" ...
    private Map<String, Object> properties;

}
