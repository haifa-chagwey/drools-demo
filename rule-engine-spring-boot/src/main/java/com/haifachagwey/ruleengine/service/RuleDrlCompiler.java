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
        if (rule.getFact() != null) {
            drl.append("// Domain: ").append(rule.getFact().getName()).append("\n");
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
                    String key = "unknown";
                    AttributeType valueType = AttributeType.STRING;
                    if (action.getAction() != null) {
                        key = action.getAction().getKey();
                        valueType = action.getAction().getType();
                    }
//                    else if (action.getOutputKey() != null) {
//                        key = action.getOutputKey();
//                    }
                    drl.append("    outputs.put(\"")
                       .append(key)
                       .append("\", ")
                       .append(formatConstant(action.getValue(), valueType))
                       .append(");\n");

            }
        }

        drl.append("end\n");
        return drl.toString();
    }


    private String compileCondition(RuleCondition condition) {
        String leftHandSide;
        AttributeType type = AttributeType.STRING;
        if (condition.getAttribute() != null) {
            leftHandSide = "properties[\"" + condition.getAttribute().getKey() + "\"]";
            type = condition.getAttribute().getType();
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


    private String formatConstant(String value, AttributeType valueType) {
        if (value == null) return "null";
        if (valueType == AttributeType.BOOLEAN) {
            return value.toLowerCase();
        }
        if (valueType == AttributeType.NUMBER) {
            return value;
        }
        return "\"" + value.replace("\"", "\\\"") + "\"";
    }
}
