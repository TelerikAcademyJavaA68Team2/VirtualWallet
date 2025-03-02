package com.example.virtualwallet.models.dtos;

import com.example.virtualwallet.helpers.YearMonthDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.YearMonth;

@Data
public class CardInput {

    @NotBlank(message = "error.cardNumberEmpty")
    @Pattern(regexp = "\\d{16}", message = "error.cardNumberDigits")
    private String cardNumber;

    @NotBlank(message = "error.cardHolderEmpty")
    @Size(min = 2, max = 30, message = "error.cardHolderLength")
    private String cardHolder;

    @NotBlank(message = "error.expirationDateEmpty")
    @JsonFormat(pattern = "MM/yy")
    @JsonDeserialize(using = YearMonthDeserializer.class)
    private YearMonth expirationDate;

    @NotBlank(message = "error.cvvEmpty")
    @Pattern(regexp = "\\d{3}", message = "error.cvvDigits")
    private String cvv;

}
