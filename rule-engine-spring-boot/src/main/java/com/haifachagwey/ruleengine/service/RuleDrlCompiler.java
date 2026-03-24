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

        RuleConditionGroup rootGroup = findRootGroup(rule);
        if (rootGroup != null) {
            drl.append(compileGroup(rootGroup));
        }

        drl.append(")\n");
        drl.append("then\n");

        for (RuleAction action : rule.getActions()) {
            drl.append("    $c.setResult(\"")
               .append(action.getOutputKey())
               .append("\", \"")
               .append(action.getOutputValue())
               .append("\");\n");
        }

        drl.append("end\n");
        return drl.toString();
    }

    private RuleConditionGroup findRootGroup(Rule rule) {
        if (rule.getConditionGroups() == null) return null;
        return rule.getConditionGroups()
                .stream()
                .filter(g -> g.getParentGroup() == null)
                .findFirst()
                .orElse(null);
    }

    private String compileGroup(RuleConditionGroup group) {
        List<String> parts = new ArrayList<>();

        if (group.getConditions() != null) {
            for (RuleCondition condition : group.getConditions()) {
                parts.add(compileCondition(condition));
            }
        }

        if (group.getSubConditionGroups() != null) {
            for (RuleConditionGroup child : group.getSubConditionGroups()) {
                parts.add("(" + compileGroup(child) + ")");
            }
        }

        String joiner = "OR".equalsIgnoreCase(group.getCombinator()) ? " || " : " && ";
        return String.join(joiner, parts);
    }

    private String compileCondition(RuleCondition condition) {
        return "facts[\"" + condition.getLeftOperand() + "\"]"
                + " " + operatorToExpression(condition.getOperator()) + " "
                + operandToExpression(condition.getRightOperandType(), condition.getRightOperandValue());
    }

    private String operandToExpression(OperandType type, String value) {
        return switch (type) {
            case FIELD -> "facts[\"" + value + "\"]";
            case CONFIG -> "tenantConfigs[\"" + value + "\"]";
            case CONSTANT -> formatConstant(value);
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
