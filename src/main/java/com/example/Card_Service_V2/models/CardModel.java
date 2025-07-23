package com.example.Card_Service_V2.models;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CardModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_model_generator")
    @SequenceGenerator(name = "card_model_generator", sequenceName = "card_model_new_seq", initialValue = 1, allocationSize = 1)
    int id;

    int userid;

    @Column(name = "accountid")
    int accountId;
    String network;
    String type;
    private LocalDateTime createdAt;
    String cardstatus;
    String currency;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sensitive_data_id", referencedColumnName = "id")
    private CardSensitiveData sensitiveData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private CardPlan plan;

    public void setUserId(int userId) {
        this.userid = userId;
    }

    public CardPlan getPlan() {
        return plan;
    }

    public void setPlan(CardPlan plan) {
        this.plan = plan;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

}