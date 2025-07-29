package com.example.Card_Service_V2.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardListDTO {
    private int cardId;
    private String cardNumber; 
    private String cardexpiry;
    private int accountid;
    private String type;
    private String cardstatus;
    private Integer userId; 

}
