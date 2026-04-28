package com.haifachagwey.ruleengine.model;

public enum Operator {
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN_OR_EQUAL,
    EQUALS,
    NOT_EQUALS;

    public String getOperator() {
        return this.name();
    }

}
