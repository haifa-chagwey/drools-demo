package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RuleDrlCompiler {

    public String compile(Rule rule) {
        StringBuilder drl = new StringBuilder();

        drl.append("package rules;\n");
        if (rule.getFact() != null) {
            drl.append("// Domain: ").append(rule.getFact().getName()).append("\n");
        }
        drl.append("import com.haifachagwey.ruleengine.model.Fact;\n");
        drl.append("global java.util.Map outputs;\n");
        drl.append("global java.util.Map configs;\n");
        drl.append("dialect \"mvel\"\n\n");

        drl.append("rule \"").append(rule.getName()).append("_").append(rule.getId()).append("\"\n");

//        Conditions
        drl.append("when\n");

        drl.append("    $f : Fact(");
        drl.append(rule.getFact() != null ? "name==" + "\"" + rule.getFact().getName()+ "\"" : "name==null");
        drl.append("&&");

        if (rule.getConditions() != null && !rule.getConditions().isEmpty()) {
            List<String> parts = rule.getConditions().stream()
                    .map(this::compileCondition)
                    .toList();
            drl.append(String.join(" && ", parts));
        } else {
            drl.append("eval(true)");
        }
        drl.append(")\n");

//        Actions
        drl.append("then\n");
        if (rule.getActions() != null) {
            for (RuleAction action : rule.getActions()) {
                String key = "unknown";
                if (action.getAction() != null) {
                    key = action.getAction().getKey();
                }

//                outputs.put(key, value);
                drl.append("    outputs.put(\"")
                   .append(key)
                   .append("\", ")
                   .append(action.getValue())
                   .append(");\n");
            }
        }

        drl.append("end\n");
        return drl.toString();
    }


    private String compileCondition(RuleCondition condition) {
        if (condition.getAttribute() == null) {
            return "eval(true)";
        }

        String leftHandSide = "attributes[\"" + condition.getAttribute().getKey() + "\"]";
        AttributeType type = condition.getAttribute().getType();

        String operator;
        switch (condition.getOperator()) {
            case "EQUAL" -> operator = "==";
            case "NOT_EQUAL" -> operator = "!=";
            case "GREATER_THAN" -> operator = ">";
            case "LESS_THAN" -> operator = "<";
            case "GREATER_OR_EQUAL" -> operator = ">=";
            case "LESS_OR_EQUAL" -> operator = "<=";
            default -> throw new IllegalArgumentException("Unsupported operator: " + condition.getOperator());
        }

        String rightHandSide = formatConstant(condition.getValue(), type);


        return leftHandSide + " " + operator + " " + rightHandSide;
    }


    private String formatConstant(String value, AttributeType valueType) {
        if (value == null) return "null";

        return switch (valueType) {
            case BOOLEAN -> value.toLowerCase();
            case NUMBER -> value;
            default -> "\"" + value.replace("\"", "\\\"") + "\"";
        };
    }
}
