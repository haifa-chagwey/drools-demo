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

        RuleDefinition definition = rule.getDefinition();
        if (definition != null && definition.getConditions() != null && !definition.getConditions().isEmpty()) {
            List<String> parts = new ArrayList<>();
            for (RuleCondition condition : definition.getConditions()) {
                parts.add(compileCondition(condition));
            }
            String joiner = "OR".equalsIgnoreCase(definition.getCombinator()) ? " || " : " && ";
            drl.append(String.join(joiner, parts));
        } else {
            drl.append("eval(true)");
        }

        drl.append(")\n");
        drl.append("then\n");

        if (definition != null && definition.getActions() != null) {
            for (RuleAction action : definition.getActions()) {
                drl.append("    $c.setResult(\"")
                   .append(action.getOutputKey())
                   .append("\", \"")
                   .append(action.getOutputValue())
                   .append("\");\n");
            }
        }

        drl.append("end\n");
        return drl.toString();
    }


    private String compileCondition(RuleCondition condition) {
        return "facts[\"" + condition.getTargetField() + "\"]"
                + " " + operatorToExpression(condition.getOperator()) + " "
                + operandToExpression(condition.getValueType(), condition.getValue());
    }

    private String operandToExpression(OperandType type, String value) {
        return switch (type) {
            case CONSTANT -> formatConstant(value);
            case CONFIG -> "tenantConfigs[\"" + value + "\"]";
            case FIELD -> "facts[\"" + value + "\"]";
        };
    }

    private String operatorToExpression(OperatorType type) {
        return switch (type) {
            case GREATER_THAN -> ">";
            case LESS_THAN -> "<";
            case GREATER_THAN_OR_EQUAL -> ">=";
            case LESS_THAN_OR_EQUAL -> "<=";
            case EQUALS -> "==";
            case NOT_EQUALS -> "!=";
        };
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
