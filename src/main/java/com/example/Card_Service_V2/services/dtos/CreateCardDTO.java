package com.example.Card_Service_V2.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCardDTO {
    private int cardId;
    private String maskedCardNumber;
    private String cardExpiry;
    private String type;
    private String cardStatus;
    private LocalDateTime createdAt;
    private String title;
    

}
