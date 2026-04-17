package com.haifachagwey.ruleengine.service;

import com.haifachagwey.ruleengine.model.*;
import com.haifachagwey.ruleengine.repository.ActionRepository;
import com.haifachagwey.ruleengine.repository.FactTypeRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActionService {

    private final ActionRepository actionRepository;

    public ActionService(ActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    @Transactional
    public List<Action> getAllActions() {
        return actionRepository.findAll();
    }

    @Transactional
    public List<Action> getActionsByFactTypeId(Integer factTypeId) {
        return actionRepository.findByFactTypeId(factTypeId);
    }

    public Optional<Action> getActionByKey(String key) {
        return actionRepository.findByKey(key);
    }

    public Action saveAction(Action actionProperty) {
        return actionRepository.save(actionProperty);
    }

    public void deleteAction(Integer id) {
        actionRepository.deleteById(id);
    }
}
