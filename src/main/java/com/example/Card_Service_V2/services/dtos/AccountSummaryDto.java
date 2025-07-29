package com.example.Card_Service_V2.services.dtos;

public class AccountSummaryDto {
    private Long accountId;
    private Long userId;
    private String currency; // Use String if you don't have the Currency enum
    private String accountNumber;
    private String status;   // Use String if you don't have the AccountStatus enum

    // Getters and setters
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}