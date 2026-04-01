package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.model.ActionDefinition;
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
    public List<ActionDefinition> getAllActions() {
        return actionDefinitionService.getAllActions();
    }

    @PostMapping
    public ActionDefinition addAction(@RequestBody ActionDefinition actionDefinition) {
        return actionDefinitionService.saveAction(actionDefinition);
    }

    @DeleteMapping("/{id}")
    public void deleteAction(@PathVariable Integer id) {
        actionDefinitionService.deleteAction(id);
    }
}
