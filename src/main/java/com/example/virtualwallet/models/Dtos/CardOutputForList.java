package com.example.virtualwallet.models.Dtos;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class CardOutputForList {

    private UUID cardId;

    private String cardNumber;

    private Date expirationDate;

}
