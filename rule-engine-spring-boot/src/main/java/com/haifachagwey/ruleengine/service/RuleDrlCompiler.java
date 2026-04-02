package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RuleDrlCompiler {

    public String compile(Rule rule) {
        if (rule.getDrl() != null && !rule.getDrl().isEmpty()) {
            return rule.getDrl();
        }
        StringBuilder drl = new StringBuilder();

        drl.append("package rules;\n");
        drl.append("import com.haifachagwey.ruleengine.model.RuleContext;\n");
        drl.append("global java.util.Map output;\n");
        drl.append("global java.util.Map tenantConfigs;\n");
        drl.append("dialect \"mvel\"\n\n");

        drl.append("rule \"").append(rule.getName()).append("\"\n");
        drl.append("when\n");
        drl.append("    $c : RuleContext(");

        if (rule.getConditions() != null && !rule.getConditions().isEmpty()) {
            List<String> parts = new ArrayList<>();
            for (RuleCondition condition : rule.getConditions()) {
                if (condition.isEnabled()) {
                    parts.add(compileCondition(condition));
                }
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
                    if (action.getActionDefinition() != null) {
                        key = action.getActionDefinition().getKey();
                    } else if (action.getOutputKey() != null) {
                        key = action.getOutputKey();
                    }
                    drl.append("    $c.setResult(\"")
                       .append(key)
                       .append("\", ")
                       .append(formatConstant(action.getOutputValue(), action.getValueType()))
                       .append(");\n");
                }
            }
        }

        drl.append("end\n");
        return drl.toString();
    }


    private String compileCondition(RuleCondition condition) {
        String leftHandSide;
        if (condition.getFactPropertyDefinition() != null) {
            leftHandSide = "facts[\"" + condition.getFactPropertyDefinition().getKey() + "\"]";
        } else if (condition.getFieldName() != null) {
            leftHandSide = "facts[\"" + condition.getFieldName() + "\"]";
        } else {
            leftHandSide = "eval(true)"; // Or some default
        }

        String operator = condition.getOperator();
        String rightHandSide = operandToExpression(condition);
        
        return leftHandSide + " " + (operator != null ? operator : "==") + " " + rightHandSide;
    }

    private String operandToExpression(RuleCondition condition) {
        String type = condition.getRightOperandType();
        if ("CONSTANT".equalsIgnoreCase(type)) {
            return formatConstant(condition.getConstantValue(), null);
        } else if ("CONFIG".equalsIgnoreCase(type)) {
            return "tenantConfigs[\"" + condition.getConfigKey() + "\"]";
        } else if ("FIELD".equalsIgnoreCase(type)) {
             return "facts[\"" + condition.getConstantValue() + "\"]";
        } else if (condition.getThresholdKey() != null) {
            return "tenantConfigs[\"" + condition.getThresholdKey() + "\"]";
        }
        return formatConstant(condition.getConstantValue(), null);
    }

    private String formatConstant(String value, String valueType) {
        if (value == null) return "null";
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return "\"" + value.toLowerCase() + "\"";
        }
        if ("Integer".equalsIgnoreCase(valueType) || "Double".equalsIgnoreCase(valueType) || "Long".equalsIgnoreCase(valueType)) {
            return value;
        }
        try {
            Double.parseDouble(value);
            return value;
        } catch (NumberFormatException e) {
            return "\"" + value.replace("\"", "\\\"") + "\"";
        }
    }
}
