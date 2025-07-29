package com.example.Card_Service_V2.services.dtos;

public class InternalCardVerificationResponseDTO {
    private boolean isValid;
    private String message;
    private Integer cardId;
    private Integer accountId;
    private String cardStatus;
    private String cardType;
    private String maskedCardNumber;

    public InternalCardVerificationResponseDTO() {}

    public InternalCardVerificationResponseDTO(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }

    public InternalCardVerificationResponseDTO(boolean isValid, String message, Integer cardId, 
                                             Integer accountId, String cardStatus, String cardType, String maskedCardNumber) {
        this.isValid = isValid;
        this.message = message;
        this.cardId = cardId;
        this.accountId = accountId;
        this.cardStatus = cardStatus;
        this.cardType = cardType;
        this.maskedCardNumber = maskedCardNumber;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(String cardStatus) {
        this.cardStatus = cardStatus;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }
}
