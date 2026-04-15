package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.model.ActionProperty;
import com.haifachagwey.ruleengine.model.ActionType;
import com.haifachagwey.ruleengine.service.ActionDefinitionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/admin/actions")
public class ActionDefinitionController {

    private final ActionDefinitionService actionDefinitionService;

    public ActionDefinitionController(ActionDefinitionService actionDefinitionService) {
        this.actionDefinitionService = actionDefinitionService;
    }

    @GetMapping
    public List<ActionProperty> getAllActions() {
        return actionDefinitionService.getAllActions();
    }

    @GetMapping("/types")
    public List<ActionType> getAllActionTypes() {
        return actionDefinitionService.getAllActionTypes();
    }

    @PostMapping
    public ActionProperty addAction(@RequestBody ActionProperty actionProperty) {
        return actionDefinitionService.saveAction(actionProperty);
    }

    @DeleteMapping("/{id}")
    public void deleteAction(@PathVariable Integer id) {
        actionDefinitionService.deleteAction(id);
    }
}
