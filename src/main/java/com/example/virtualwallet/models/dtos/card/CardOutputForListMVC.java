package com.example.virtualwallet.models.dtos.card;

import com.example.virtualwallet.helpers.YearMonthSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.YearMonth;
import java.util.UUID;

@Data
public class CardOutputForListMVC {

    private UUID cardId;

    private String cardHolder;

    private String cardNumber;

    private String cvv;

    @JsonFormat(pattern = "MM/yy")
    @JsonSerialize(using = YearMonthSerializer.class)
    private YearMonth expirationDate;
}
