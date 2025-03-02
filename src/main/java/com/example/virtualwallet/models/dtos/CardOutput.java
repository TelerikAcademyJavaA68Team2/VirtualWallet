package com.example.virtualwallet.models.dtos;

import lombok.Data;

import java.time.YearMonth;
import java.util.UUID;

@Data
public class CardOutput {

    private UUID cardId;

    private String cardNumber;

    private String cardHolder;

    private YearMonth expirationDate;

    private String ccv;

}
