package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.model.Action;
import com.haifachagwey.ruleengine.service.ActionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/admin/actions")
public class ActionController {

    private final ActionService actionService;

    public ActionController(ActionService actionService) {
        this.actionService = actionService;
    }

    @GetMapping
    public List<Action> getAllActions() {
        return actionService.getAllActions();
    }

    @GetMapping("/by-fact/{factTypeId}")
    public List<Action> getActionsByFactTypeId(@PathVariable Integer factTypeId) {
        return actionService.getActionsByFactTypeId(factTypeId);
    }

    @PostMapping
    public Action addAction(@RequestBody Action actionProperty) {
        return actionService.saveAction(actionProperty);
    }

    @DeleteMapping("/{id}")
    public void deleteAction(@PathVariable Integer id) {
        actionService.deleteAction(id);
    }
}
