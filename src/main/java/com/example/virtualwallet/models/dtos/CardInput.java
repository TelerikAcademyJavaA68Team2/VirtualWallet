package com.example.virtualwallet.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.YearMonth;

@Data
public class CardInput {

    @NotBlank(message = "{error.cardNumberEmpty}")
    @Pattern(regexp = "\\d{16}", message = "{error.cardNumberDigits}")
    private String cardNumber;

    @NotBlank(message = "{error.cardHolderEmpty}")
    @Size(min = 2, max = 30, message = "{error.cardHolderLength}")
    @Pattern(regexp = "^[A-Za-z]+( [A-Za-z]+)+$", message = "{error.cardHolderFormat}")
    private String cardHolder;


    @NotBlank(message = "{error.expirationDateEmpty}")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/([2-9][5-9]|[3-9][0-9])$", message = "{error.expirationDateFormat}")
    private String expirationDate;

    @NotBlank(message = "{error.cvvEmpty}")
    @Pattern(regexp = "\\d{3}", message = "{error.cvvDigits}")
    private String cvv;

    public YearMonth getExpirationDateAsYearMonth() {
        return YearMonth.parse("20" + expirationDate.split("/")[1] + "-" + expirationDate.split("/")[0]);
    }
}
