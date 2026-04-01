package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.ActionDefinition;
import com.haifachagwey.ruleengine.model.ActionAllowedValue;
import com.haifachagwey.ruleengine.repository.ActionDefinitionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActionDefinitionService {

    private final ActionDefinitionRepository actionDefinitionRepository;

    public ActionDefinitionService(ActionDefinitionRepository actionDefinitionRepository) {
        this.actionDefinitionRepository = actionDefinitionRepository;
    }

    @PostConstruct
    public void init() {
        if (actionDefinitionRepository.count() == 0) {
            actionDefinitionRepository.save(ActionDefinition.builder()
                    .key("BOOK_AMOUNT")
                    .label("Book Amount")
                    .description("The action to book an amount")
                    .allowedValues(List.of(
                            ActionAllowedValue.builder().value("DIRECT_PAYMENT").build(),
                            ActionAllowedValue.builder().value("LOST_REVENUE").build(),
                            ActionAllowedValue.builder().value("LATER_PAYMENT").build()
                    ))
                    .build());

            actionDefinitionRepository.save(ActionDefinition.builder()
                    .key("openBarrier")
                    .label("Open Barrier")
                    .description("The action to open the barrier")
                    .allowedValues(List.of(
                            ActionAllowedValue.builder().value("true").build(),
                            ActionAllowedValue.builder().value("false").build()
                    ))
                    .build());
        }
    }

    public List<ActionDefinition> getAllActions() {
        return actionDefinitionRepository.findAll();
    }

    public Optional<ActionDefinition> getActionByKey(String key) {
        return actionDefinitionRepository.findByKey(key);
    }

    public ActionDefinition saveAction(ActionDefinition actionDefinition) {
        return actionDefinitionRepository.save(actionDefinition);
    }

    public void deleteAction(Integer id) {
        actionDefinitionRepository.deleteById(id);
    }
}
