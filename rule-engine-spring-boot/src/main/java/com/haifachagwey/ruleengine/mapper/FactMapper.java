package com.haifachagwey.ruleengine.mapper;

import com.haifachagwey.ruleengine.dto.*;
import com.haifachagwey.ruleengine.model.*;
import org.springframework.stereotype.Component;

@Component
public class FactMapper {

    /*
    * Entity To DTO
    * */

    public FactDTO toDTO(Fact fact) {
        if (fact == null) {
            return null;
        }
        return FactDTO.builder()
                .id(fact.getId())
                .name(fact.getName())
                .description(fact.getDescription())
                .attributes(fact.getAttributes() != null ? fact.getAttributes().stream()
                        .map(this::toDTO)
                        .toList() : null)
                .associatedActions(fact.getAssociatedActions() != null ? fact.getAssociatedActions().stream()
                        .map(this::toDTO)
                        .toList() : null)
                .build();
    }

    public FactAttributeDTO toDTO(FactAttribute attribute) {
        if (attribute == null) {
            return null;
        }
        return FactAttributeDTO.builder()
                .id(attribute.getId())
                .key(attribute.getKey())
                .label(attribute.getLabel())
                .type(attribute.getType())
                .options(attribute.getOptions() != null ? attribute.getOptions().stream()
                                                          .map(this::toDTO)
                                                          .toList() : null)
                .build();
    }

    public AssociatedActionDTO toDTO(AssociatedAction action) {
        if (action == null) {
            return null;
        }
        return AssociatedActionDTO.builder()
                .id(action.getId())
                .key(action.getKey())
                .label(action.getLabel())
                .options(action.getOptions() != null ? action.getOptions().stream()
                                                       .map(this::toDTO)
                                                       .toList() : null)
                .build();
    }
    public AttributeOptionDTO toDTO(AttributeOption option) {
        if (option == null) {
            return null;
        }
        return AttributeOptionDTO.builder()
                .id(option.getId())
                .value(option.getValue())
                .label(option.getLabel())
                .build();
    }


    public ActionOptionDTO toDTO(ActionOption option) {
        if (option == null) {
            return null;
        }
        return ActionOptionDTO.builder()
                .id(option.getId())
                .value(option.getValue())
                .label(option.getLabel())
                .build();
    }

    /*
    * DTO To Entity
    * */

    public Fact toEntity(FactDTO dto) {
        if (dto == null) {
            return null;
        }
        Fact fact = Fact.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .attributes(dto.getAttributes() != null ? dto.getAttributes().stream()
                        .map(this::toEntity)
                        .toList() : null)
                .associatedActions(dto.getAssociatedActions() != null ? dto.getAssociatedActions().stream()
                        .map(this::toEntity)
                        .toList() : null)
                .build();
        // Link children back to parent for correct persistence if needed
        if (fact.getAttributes() != null) {
            fact.getAttributes().forEach(attr -> attr.setFact(fact));
        }
        if (fact.getAssociatedActions() != null) {
            fact.getAssociatedActions().forEach(action -> action.setFact(fact));
        }
        return fact;
    }


    public FactAttribute toEntity(FactAttributeDTO dto) {
        if (dto == null) {
            return null;
        }
        FactAttribute attribute = FactAttribute.builder()
                .id(dto.getId())
                .key(dto.getKey())
                .label(dto.getLabel())
                .type(dto.getType())
                .options(dto.getOptions() != null ? dto.getOptions().stream()
                        .map(this::toEntity)
                        .toList() : null)
                .build();
        
        if (attribute.getOptions() != null) {
            attribute.getOptions().forEach(opt -> opt.setAttribute(attribute));
        }
        return attribute;
    }



    public AssociatedAction toEntity(AssociatedActionDTO dto) {
        if (dto == null) {
            return null;
        }
        AssociatedAction action = AssociatedAction.builder()
                .id(dto.getId())
                .key(dto.getKey())
                .label(dto.getLabel())
                .options(dto.getOptions() != null ? dto.getOptions().stream()
                        .map(this::toEntity)
                        .toList() : null)
                .build();
        if (action.getOptions() != null) {
            action.getOptions().forEach(opt -> opt.setAction(action));
        }
        return action;
    }



    public AttributeOption toEntity(AttributeOptionDTO dto) {
        if (dto == null) {
            return null;
        }
        return AttributeOption.builder()
                .id(dto.getId())
                .value(dto.getValue())
                .label(dto.getLabel())
                .build();
    }


    public ActionOption toEntity(ActionOptionDTO dto) {
        if (dto == null) {
            return null;
        }
        return ActionOption.builder()
                .id(dto.getId())
                .value(dto.getValue())
                .label(dto.getLabel())
                .build();
    }
}
