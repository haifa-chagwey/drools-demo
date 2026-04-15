package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.ActionProperty;
import com.haifachagwey.ruleengine.model.ActionPropertyAllowedValue;
import com.haifachagwey.ruleengine.model.ActionType;
import com.haifachagwey.ruleengine.model.FactPropertyType;
import com.haifachagwey.ruleengine.repository.ActionDefinitionRepository;
import com.haifachagwey.ruleengine.repository.ActionTypeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActionDefinitionService {

    private final ActionDefinitionRepository actionDefinitionRepository;
    private final ActionTypeRepository actionTypeRepository;

    public ActionDefinitionService(ActionDefinitionRepository actionDefinitionRepository, ActionTypeRepository actionTypeRepository) {
        this.actionDefinitionRepository = actionDefinitionRepository;
        this.actionTypeRepository = actionTypeRepository;
    }

    @PostConstruct
    public void init() {
        if (actionTypeRepository.count() == 0) {
            ActionType financial = ActionType.builder()
                    .name("Financial")
                    .description("Financial related actions")
                    .build();
            ActionType hardware = ActionType.builder()
                    .name("Hardware")
                    .description("Hardware related actions")
                    .build();
            actionTypeRepository.saveAll(List.of(financial, hardware));
        }

        if (actionDefinitionRepository.count() == 0) {
            ActionType financial = actionTypeRepository.findByName("Financial").orElse(null);
            ActionType hardware = actionTypeRepository.findByName("Hardware").orElse(null);

            ActionProperty bookAmount = ActionProperty.builder()
                    .key("BOOK_AMOUNT")
                    .label("Book Amount")
                    .type(FactPropertyType.ENUM)
                    .description("The action to book an amount")
                    .actionType(financial)
                    .build();

            ActionPropertyAllowedValue v1 = ActionPropertyAllowedValue.builder()
                    .value("DIRECT_PAYMENT")
                    .actionProperty(bookAmount) // 🔥 REQUIRED
                    .build();

            ActionPropertyAllowedValue v2 = ActionPropertyAllowedValue.builder()
                    .value("LOST_REVENUE")
                    .actionProperty(bookAmount)
                    .build();

            ActionPropertyAllowedValue v3 = ActionPropertyAllowedValue.builder()
                    .value("LATER_PAYMENT")
                    .actionProperty(bookAmount)
                    .build();
            bookAmount.setAllowedValues(List.of(v1, v2, v3));
            actionDefinitionRepository.save(bookAmount);

            ActionProperty openBarrier = ActionProperty.builder()
                    .key("openBarrier")
                    .label("Open Barrier")
                    .type(FactPropertyType.BOOLEAN)
                    .description("The action to open the barrier")
                    .actionType(hardware)
                    .build();

            ActionPropertyAllowedValue b1 = ActionPropertyAllowedValue.builder()
                    .value("true")
                    .actionProperty(openBarrier)
                    .build();

            ActionPropertyAllowedValue b2 = ActionPropertyAllowedValue.builder()
                    .value("false")
                    .actionProperty(openBarrier)
                    .build();

            openBarrier.setAllowedValues(List.of(b1, b2));

            actionDefinitionRepository.save(openBarrier);
        }
    }

    public List<ActionProperty> getAllActions() {
        return actionDefinitionRepository.findAll();
    }

    public Optional<ActionProperty> getActionByKey(String key) {
        return actionDefinitionRepository.findByKey(key);
    }

    public ActionProperty saveAction(ActionProperty actionProperty) {
        return actionDefinitionRepository.save(actionProperty);
    }

    public List<ActionType> getAllActionTypes() {
        return actionTypeRepository.findAll();
    }

    public void deleteAction(Integer id) {
        actionDefinitionRepository.deleteById(id);
    }
}
