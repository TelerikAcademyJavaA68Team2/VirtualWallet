package com.example.virtualwallet.models.dtos;

import lombok.Data;

import java.time.YearMonth;
import java.util.UUID;

@Data
public class CardOutputForList {

    private UUID cardId;

    private String cardNumber;

    private YearMonth expirationDate;

}
