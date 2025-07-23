package com.example.Card_Service_V2.services;

import com.example.Card_Service_V2.models.CardPlan;
import com.example.Card_Service_V2.repositories.CardPlanRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CardPlanService {
    @Autowired
    private CardPlanRepository cardPlanRepository;

    @PostConstruct
    public void initDefaultPlans() {
        if (cardPlanRepository.count() == 0) {
            cardPlanRepository.save(new CardPlan(
                "Silver", 50000, false, "Silver plan: 50,000 limit, no international transactions"
            ));
            cardPlanRepository.save(new CardPlan(
                "Gold", 200000, true, "Gold plan: 200,000 limit, international transactions enabled"
            ));
            cardPlanRepository.save(new CardPlan(
                "Platinum", 1000000, true, "Platinum plan: 1,000,000 limit, international transactions enabled"
            ));
        }
    }

    public List<CardPlan> getAllPlans() {
        return cardPlanRepository.findAll();
    }

    public Optional<CardPlan> getPlanById(int id) {
        return cardPlanRepository.findById(id);
    }

    public CardPlan createPlan(CardPlan plan) {
        return cardPlanRepository.save(plan);
    }

    public void deletePlan(int id) {
        cardPlanRepository.deleteById(id);
    }
}
