package com.haifachagwey.ruleengine.controller;

import com.haifachagwey.ruleengine.model.OrderDiscount;
import com.haifachagwey.ruleengine.model.OrderRequest;
import com.haifachagwey.ruleengine.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {


    private final OrderService orderService;

    @PostMapping("/evaluate")
    public ResponseEntity<OrderDiscount> evaluate(@RequestBody OrderRequest orderRequest) {
        OrderDiscount discount = orderService.evaluate(orderRequest);
        return new ResponseEntity<>(discount, HttpStatus.OK);
    }

}