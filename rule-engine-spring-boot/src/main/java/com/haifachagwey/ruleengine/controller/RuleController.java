package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RuleController {


    private final RuleService ruleService;

    @PostMapping("/evaluate")
    public ResponseEntity<Map<String, Object>> evaluate(@RequestBody Map<String, Object> input) {
        Map<String, Object> discount = ruleService.evaluate(input);
        return new ResponseEntity<>(discount, HttpStatus.OK);
    }

}