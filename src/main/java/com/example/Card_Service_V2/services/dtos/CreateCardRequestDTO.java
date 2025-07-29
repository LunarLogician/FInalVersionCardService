package com.example.Card_Service_V2.services.dtos;


public class CreateCardRequestDTO {
    
    private Integer userId; 
    private Integer accountId;
    private String type;
    private String network;
    private String cardPin;
    private Integer planId;
    private String currency;
    private String title;
    private String status; // New field for card status

    public CreateCardRequestDTO() {
    }

    public CreateCardRequestDTO(Integer userId, Integer accountId, String type, String network, String cardpin, Integer planId, String currency, String title, String status) {
        this.userId = userId;
        this.accountId = accountId;
        this.type = type;
        this.network = network;
        this.cardPin = cardpin;
        this.planId = planId;
        this.currency = currency;
        this.title = title;
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getCardPin() {
        return cardPin;
    }

    public void setCardPin(String cardPin) {
        this.cardPin = cardPin;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CreateCardRequestDTO{" +
                "userId=" + userId + " (set from JWT)" +
                ", accountId=" + accountId +
                ", type='" + type + '\'' +
                ", network='" + network + '\'' +
                ", cardPin='***'" +
                ", planId=" + planId +
                ", currency='" + currency + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
