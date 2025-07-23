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

    public CardPlan() {}
    public CardPlan(String name, double limitAmount, boolean internationalEnabled, String description) {
        this.name = name;
        this.limitAmount = limitAmount;
        this.description = description;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
