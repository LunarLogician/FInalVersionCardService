package com.example.Card_Service_V2.services.dtos;

public class InternalCardVerificationRequestDTO {
    private String cardNumber;
    private String cardPin;
    private Integer accountId; 
    public InternalCardVerificationRequestDTO() {}

    public InternalCardVerificationRequestDTO(String cardNumber, String cardPin) {
        this.cardNumber = cardNumber;
        this.cardPin = cardPin;
    }

    public InternalCardVerificationRequestDTO(String cardNumber, String cardPin, Integer accountId) {
        this.cardNumber = cardNumber;
        this.cardPin = cardPin;
        this.accountId = accountId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardPin() {
        return cardPin;
    }

    public void setCardPin(String cardPin) {
        this.cardPin = cardPin;
    }

    public Integer getAccountId() {
        return accountId;
    }
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    private Double amount;
public Double getAmount() { return amount; }
public void setAmount(Double amount) { this.amount = amount; }
    private String cardCvv;

    public String getCardCvv() {
        return cardCvv;
    }

    public void setCardCvv(String cardCvv) {
        this.cardCvv = cardCvv;
    }
    
}
