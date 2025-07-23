package com.example.Card_Service_V2.services.dtos;

public class UpdateCardRequestDTO {
    
    private String cardpin;
    private String cardstatus;

    public UpdateCardRequestDTO() {
    }

    public UpdateCardRequestDTO(String cardpin, String cardstatus) {
        this.cardpin = cardpin;
        this.cardstatus = cardstatus;
    }

    public String getCardpin() {
        return cardpin;
    }

    public void setCardpin(String cardpin) {
        this.cardpin = cardpin;
    }

    public String getCardstatus() {
        return cardstatus;
    }

    public void setCardstatus(String cardstatus) {
        this.cardstatus = cardstatus;
    }

    @Override
    public String toString() {
        return "UpdateCardRequestDTO{" +
                "cardpin='" + (cardpin != null ? "***" : null) + '\'' +
                ", cardstatus='" + cardstatus + '\'' +
                '}';
    }
}
