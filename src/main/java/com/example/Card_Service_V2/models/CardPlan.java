package com.example.Card_Service_V2.models;

import jakarta.persistence.*;

@Entity
public class CardPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private double limitAmount;
    private String description;
    private double dailyLimit;

    public CardPlan() {}
    public CardPlan(String name, double limitAmount, boolean internationalEnabled, String description, double dailyLimit) {
        this.name = name;
        this.limitAmount = limitAmount;
        this.description = description;
        this.dailyLimit = dailyLimit;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(double dailyLimit) { this.dailyLimit = dailyLimit; }
}
