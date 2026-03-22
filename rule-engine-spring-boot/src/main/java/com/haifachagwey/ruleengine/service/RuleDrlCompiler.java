package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        if (group.getChildGroups() != null) {
            for (RuleConditionGroup child : group.getChildGroups()) {
                parts.add("(" + compileGroup(child) + ")");
            }
        }

        String joiner = "OR".equalsIgnoreCase(group.getCombinator()) ? " || " : " && ";
        return String.join(joiner, parts);
    }

    private String compileCondition(RuleCondition condition) {
        return operandToExpression(condition.getLeftType(), condition.getLeftValue())
                + " " + condition.getOperator() + " "
                + operandToExpression(condition.getRightType(), condition.getRightValue());
    }

    private String operandToExpression(OperandType type, String value) {
        return switch (type) {
            case FIELD -> "facts[\"" + value + "\"]";
            case CONFIG -> "tenantConfigs[\"" + value + "\"]";
            case CONSTANT -> formatConstant(value);
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
