package com.example.Card_Service_V2.controllers;

import com.example.Card_Service_V2.models.CardPlan;
import com.example.Card_Service_V2.services.CardPlanService;
import com.example.Card_Service_V2.utils.AuthUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/v1/card-plans")
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
    public ResponseEntity<?> createPlan(@RequestBody CardPlan plan, HttpServletRequest request) {
        if (!AuthUtils.isAdmin(request)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Only ADMIN can unblock cards"));
        }
        return ResponseEntity.ok(cardPlanService.createPlan(plan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlan(@PathVariable int id, HttpServletRequest request) {
        if (!AuthUtils.isAdmin(request)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Only ADMIN can unblock cards"));
        }
        cardPlanService.deletePlan(id);
        return ResponseEntity.ok(Map.of("message", "Plan deleted"));
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assignPlanToUser(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!AuthUtils.isAdmin(request)) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Only ADMIN can unblock cards"));
        }
        if (!body.containsKey("userId") || !body.containsKey("planId")) {
            return ResponseEntity.badRequest().body(Map.of("error", "userId and planId are required"));
        }
        Integer userId = (Integer) body.get("userId");
        Integer planId = (Integer) body.get("planId");
        try {
            cardPlanService.assignPlanToUser(userId, planId);
            return ResponseEntity.ok(Map.of("message", "Plan assigned to user successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
