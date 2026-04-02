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
        drl.append("import com.haifachagwey.ruleengine.model.RuleContext;\n");
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
            // For simplicity, using AND as default combinator since it's not in the XML
            drl.append(String.join(" && ", parts));
        } else {
            drl.append("eval(true)");
        }

        drl.append(")\n");
        drl.append("then\n");

        if (rule.getActions() != null) {
            for (RuleAction action : rule.getActions()) {
                if (action.isEnabled()) {
                    drl.append("    $c.setResult(\"")
                       .append(action.getActionDefinition() != null ? action.getActionDefinition().getKey() : "unknown")
                       .append("\", \"")
                       .append(action.getOutputValue())
                       .append("\");\n");
                }
            }
        }

        drl.append("end\n");
        return drl.toString();
    }


    private String compileCondition(RuleCondition condition) {
        String leftHandSide = "facts[\"" + (condition.getFactDefinition() != null ? condition.getFactDefinition().getKey() : "unknown") + "\"]";
        String operator = condition.getOperator();
        String rightHandSide = operandToExpression(condition.getRightOperandType(), condition.getConstantValue(), condition.getConfigKey());
        
        return leftHandSide + " " + operator + " " + rightHandSide;
    }

    private String operandToExpression(String type, String constantValue, String configKey) {
        if ("CONSTANT".equalsIgnoreCase(type)) {
            return formatConstant(constantValue);
        } else if ("CONFIG".equalsIgnoreCase(type)) {
            return "tenantConfigs[\"" + configKey + "\"]";
        } else if ("FIELD".equalsIgnoreCase(type)) {
             return "facts[\"" + constantValue + "\"]";
        }
        return formatConstant(constantValue);
    }

    private String formatConstant(String value) {
        if (value == null) return "null";
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return value.toLowerCase();
        }
        try {
            Double.parseDouble(value);
            return value;
        } catch (Exception e) {
            return "\"" + value.replace("\"", "\\\"") + "\"";
        }
    }
}
