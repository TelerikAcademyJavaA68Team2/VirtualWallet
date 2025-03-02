package com.example.virtualwallet.models.dtos;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class CardOutputForList {

    private UUID cardId;

    private String cardNumber;

    private Date expirationDate;

}
