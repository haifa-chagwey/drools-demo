package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RuleDrlCompiler {

    public String compile(Rule rule) {
        StringBuilder drl = new StringBuilder();

        drl.append("package rules;\n");
        if (rule.getFactType() != null) {
            drl.append("// Domain: ").append(rule.getFactType().getName()).append("\n");
        }
        drl.append("import com.haifachagwey.ruleengine.model.GlobalFact;\n");
        drl.append("global java.util.Map outputs;\n");
        drl.append("global java.util.Map configs;\n");
        drl.append("dialect \"mvel\"\n\n");

        drl.append("rule \"").append(rule.getName()).append("\"\n");
        drl.append("when\n");
        drl.append("    $f : GlobalFact(");

        if (rule.getConditions() != null && !rule.getConditions().isEmpty()) {
            List<String> parts = new ArrayList<>();
            for (RuleCondition condition : rule.getConditions()) {
                    parts.add(compileCondition(condition));
            }
            if (parts.isEmpty()) {
                drl.append("eval(true)");
            } else {
                drl.append(String.join(" && ", parts));
            }
        } else {
            drl.append("eval(true)");
        }

        drl.append(")\n");
        drl.append("then\n");

        if (rule.getActions() != null) {
            for (RuleAction action : rule.getActions()) {
                if (action.isEnabled()) {
                    String key = "unknown";
                    FactPropertyType valueType = FactPropertyType.STRING;
                    if (action.getActionProperty() != null) {
                        key = action.getActionProperty().getKey();
                        valueType = action.getActionProperty().getType();
                    } else if (action.getOutputKey() != null) {
                        key = action.getOutputKey();
                    }
                    drl.append("    outputs.put(\"")
                       .append(key)
                       .append("\", ")
                       .append(formatConstant(action.getOutputValue(), valueType))
                       .append(");\n");
                }
            }
        }

        drl.append("end\n");
        return drl.toString();
    }


    private String compileCondition(RuleCondition condition) {
        String leftHandSide;
        FactPropertyType type = FactPropertyType.STRING;
        if (condition.getFactProperty() != null) {
            leftHandSide = "properties[\"" + condition.getFactProperty().getKey() + "\"]";
            type = condition.getFactProperty().getType();
        } else {
            return "eval(true)";
        }

        String operator = condition.getOperator();
        if (operator == null) operator = "==";

        String rightHandSide;
        if (condition.getValue() != null && condition.getValue().startsWith("config:")) {
            String configKey = condition.getValue().substring("config:".length());
            rightHandSide = "configs[\"" + configKey + "\"]";
        } else {
            rightHandSide = formatConstant(condition.getValue(), type);
        }

        return leftHandSide + " " + operator + " " + rightHandSide;
    }


    private String formatConstant(String value, FactPropertyType valueType) {
        if (value == null) return "null";
        if (valueType == FactPropertyType.BOOLEAN) {
            return value.toLowerCase();
        }
        if (valueType == FactPropertyType.NUMBER) {
            return value;
        }
        return "\"" + value.replace("\"", "\\\"") + "\"";
    }
}
