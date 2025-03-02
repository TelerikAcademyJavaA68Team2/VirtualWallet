package com.example.virtualwallet.models.Dtos;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class CardOutput {

    private UUID cardId;

    private String cardNumber;

    private String cardHolder;

    private Date expirationDate;

    private String ccv;

}
