package com.example.Card_Service_V2.services.dtos;

public class CardFilterDTO {
    
    private String status;
    private String type;
    private Integer userId;
    private Integer accountId;
    private String network;
    
    public CardFilterDTO() {
    }
    
    public CardFilterDTO(String status, String type, Integer userId, Integer accountId, String network) {
        this.status = status;
        this.type = type;
        this.userId = userId;
        this.accountId = accountId;
        this.network = network;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
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
    
    public String getNetwork() {
        return network;
    }
    
    public void setNetwork(String network) {
        this.network = network;
    }
    
    @Override
    public String toString() {
        return "CardFilterDTO{" +
                "status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", userId=" + userId +
                ", accountId=" + accountId +
                ", network='" + network + '\'' +
                '}';
    }
}
