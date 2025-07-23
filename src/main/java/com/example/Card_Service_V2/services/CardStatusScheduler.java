package com.example.Card_Service_V2.services;

import com.example.Card_Service_V2.models.CardModel;
import com.example.Card_Service_V2.repositories.CardRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class CardStatusScheduler {

    @Autowired
    private CardRepo cardRepo;

    @Scheduled(fixedRate = 60000) // runs every 1 minute
    public void activatePendingCardsAfter24Hours() {
        List<CardModel> pendingCards = cardRepo.findByCardstatus("PENDING");

        LocalDateTime now = LocalDateTime.now();

        for (CardModel card : pendingCards) {
            if (card.getCreatedAt() != null &&
                    Duration.between(card.getCreatedAt(), now).toHours() >= 24) {

                card.setCardstatus("ACTIVE");
                cardRepo.save(card);
            }
        }
    }
}
