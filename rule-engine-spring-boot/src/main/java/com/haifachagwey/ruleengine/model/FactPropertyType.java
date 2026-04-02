package com.haifachagwey.ruleengine.model;

import java.util.List;

import static com.haifachagwey.ruleengine.model.Operator.*;

public enum FactPropertyType {
    STRING (List.of(EQUALS, NOT_EQUALS)),
    NUMBER (List.of(GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, EQUALS, NOT_EQUALS)),
    BOOLEAN (List.of(EQUALS, NOT_EQUALS)),
    DATE (List.of(GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, EQUALS, NOT_EQUALS)),
    ENUM (List.of(EQUALS, NOT_EQUALS));

    FactPropertyType(List<Operator> operators) {
    }
}
