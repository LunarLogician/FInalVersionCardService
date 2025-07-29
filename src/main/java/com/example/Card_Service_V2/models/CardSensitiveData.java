package com.example.Card_Service_V2.models;


import jakarta.persistence.*;

@Entity
@Table(name = "card_sensitive_data")
public class CardSensitiveData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    private String cardCvv;

    @Column(nullable = false)
    private String cardPin;

    @Column(nullable = false)
    private String cardExpiry;

    @Column(nullable = true)
    private String title;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getCardCvv() { return cardCvv; }
    public void setCardCvv(String cardCvv) { this.cardCvv = cardCvv; }
    public String getCardPin() { return cardPin; }
    public void setCardPin(String cardPin) { this.cardPin = cardPin; }
    public String getCardExpiry() { return cardExpiry; }
    public void setCardExpiry(String cardExpiry) { this.cardExpiry = cardExpiry; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
