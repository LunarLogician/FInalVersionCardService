package com.example.Card_Service_V2.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class CardTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer cardId;
    private Integer accountId;
    private Double amount;
    private LocalDateTime timestamp;

    public CardTransaction() {}

    public CardTransaction(Integer cardId, Integer accountId, Double amount, LocalDateTime timestamp) {
        this.cardId = cardId;
        this.accountId = accountId;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCardId() { return cardId; }
    public void setCardId(Integer cardId) { this.cardId = cardId; }
    public Integer getAccountId() { return accountId; }
    public void setAccountId(Integer accountId) { this.accountId = accountId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
