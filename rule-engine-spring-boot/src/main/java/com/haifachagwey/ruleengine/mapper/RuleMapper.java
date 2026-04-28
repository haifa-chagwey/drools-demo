package com.haifachagwey.ruleengine.mapper;

import com.haifachagwey.ruleengine.dto.RuleActionDTO;
import com.haifachagwey.ruleengine.dto.RuleConditionDTO;
import com.haifachagwey.ruleengine.dto.RuleDTO;
import com.haifachagwey.ruleengine.model.*;
import com.haifachagwey.ruleengine.repository.AssociatedActionRepository;
import com.haifachagwey.ruleengine.repository.FactAttributeRepository;
import com.haifachagwey.ruleengine.repository.FactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RuleMapper {

    private final FactRepository factRepository;
    private final FactAttributeRepository factAttributeRepository;
    private final AssociatedActionRepository associatedActionRepository;

    public RuleDTO toDTO(Rule rule) {
        if (rule == null) {
            return null;
        }
        return RuleDTO.builder()
                .id(rule.getId())
                .name(rule.getName())
                .description(rule.getDescription())
                .factId(rule.getFact() != null ? rule.getFact().getId().longValue() : null)
                .factName(rule.getFact() != null ? rule.getFact().getName() : null)
                .active(rule.isActive())
                .conditions(rule.getConditions() != null ? rule.getConditions().stream()
                        .map(this::toDTO)
                        .toList() : null)
                .actions(rule.getActions() != null ? rule.getActions().stream()
                        .map(this::toDTO)
                        .toList() : null)
                .build();
    }

    public RuleConditionDTO toDTO(RuleCondition condition) {
        if (condition == null) {
            return null;
        }
        return RuleConditionDTO.builder()
                .id(condition.getId())
                .attributeId(condition.getAttribute() != null ? condition.getAttribute().getId() : null)
                .attributeKey(condition.getAttribute() != null ? condition.getAttribute().getKey() : null)
                .attributeLabel(condition.getAttribute() != null ? condition.getAttribute().getLabel() : null)
                .operator(condition.getOperator())
                .value(condition.getValue())
                .build();
    }

    public RuleActionDTO toDTO(RuleAction action) {
        if (action == null) {
            return null;
        }
        return RuleActionDTO.builder()
                .id(action.getId())
                .actionId(action.getAction() != null ? action.getAction().getId() : null)
                .actionKey(action.getAction() != null ? action.getAction().getKey() : null)
                .actionLabel(action.getAction() != null ? action.getAction().getLabel() : null)
                .value(action.getValue())
                .build();
    }

    public Rule toEntity(RuleDTO dto) {
        if (dto == null) {
            return null;
        }
        Rule rule = new Rule();
        rule.setId(dto.getId());
        rule.setName(dto.getName());
        rule.setDescription(dto.getDescription());
        rule.setActive(dto.isActive());
        if (dto.getFactId() != null) {
            rule.setFact(factRepository.findById(dto.getFactId().intValue()).orElse(null));
        }
        if (dto.getConditions() != null) {
            rule.setConditions(dto.getConditions().stream()
                    .map(c -> {
                        RuleCondition condition = toEntity(c);
                        condition.setRule(rule);
                        return condition;
                    })
                    .toList());
        }
        if (dto.getActions() != null) {
            rule.setActions(dto.getActions().stream()
                    .map(a -> {
                        RuleAction action = toEntity(a);
                        action.setRule(rule);
                        return action;
                    })
                    .toList());
        }
        return rule;
    }

    public RuleCondition toEntity(RuleConditionDTO dto) {
        if (dto == null) {
            return null;
        }
        RuleCondition condition = new RuleCondition();
        condition.setId(dto.getId());
        condition.setOperator(dto.getOperator());
        condition.setValue(dto.getValue());
        if (dto.getAttributeId() != null) {
            condition.setAttribute(factAttributeRepository.findById(dto.getAttributeId()).orElse(null));
        }
        return condition;
    }

    public RuleAction toEntity(RuleActionDTO dto) {
        if (dto == null) {
            return null;
        }
        RuleAction action = new RuleAction();
        action.setId(dto.getId());
        action.setValue(dto.getValue());
        if (dto.getActionId() != null) {
            action.setAction(associatedActionRepository.findById(dto.getActionId()).orElse(null));
        }
        return action;
    }
}
