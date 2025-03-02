package com.example.virtualwallet.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.util.Date;

@Data
public class CardInput {

    @NotBlank(message = "error.cardNumberEmpty")
    @Size(min = 16, max = 64, message = "Card Number should be between 16 and 64 symbols!")
    private String cardNumber;

    @NotBlank(message = "Content can't be empty")
    @Size(min = 32, max = 8192, message = "Content should be between 32 and 8192 symbols")
    private String cardHolder;

    @NotBlank(message = "Content can't be empty")
    @Size(min = 32, max = 8192, message = "Content should be between 32 and 8192 symbols")
    private Date expirationDate;

    private Date cvv;

}
