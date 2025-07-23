package com.example.Card_Service_V2.services.dtos;

public class CardVerificationResponseDTO {
    
    private boolean verified;
    private String message;
    private int userId;
    private String maskedCardNumber;

    public CardVerificationResponseDTO() {
    }

    public CardVerificationResponseDTO(boolean verified, String message, int userId, String maskedCardNumber) {
        this.verified = verified;
        this.message = message;
        this.userId = userId;
        this.maskedCardNumber = maskedCardNumber;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }

    @Override
    public String toString() {
        return "CardVerificationResponseDTO{" +
                "verified=" + verified +
                ", message='" + message + '\'' +
                ", userId=" + userId +
                ", maskedCardNumber='" + maskedCardNumber + '\'' +
                '}';
    }
}
