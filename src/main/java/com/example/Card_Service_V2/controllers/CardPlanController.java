package com.example.Card_Service_V2.controllers;

import com.example.Card_Service_V2.models.CardPlan;
import com.example.Card_Service_V2.services.CardPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/card-plans")
public class CardPlanController {
    @Autowired
    private CardPlanService cardPlanService;

    @GetMapping
    public List<CardPlan> getAllPlans() {
        return cardPlanService.getAllPlans();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlanById(@PathVariable int id) {
        Optional<CardPlan> plan = cardPlanService.getPlanById(id);
        if (plan.isPresent()) {
            return ResponseEntity.ok(plan.get());
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Plan not found"));
        }
    }

    @PostMapping
    public CardPlan createPlan(@RequestBody CardPlan plan) {
        return cardPlanService.createPlan(plan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlan(@PathVariable int id) {
        cardPlanService.deletePlan(id);
        return ResponseEntity.ok(Map.of("message", "Plan deleted"));
    }
}
