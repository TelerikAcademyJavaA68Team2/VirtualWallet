package com.example.virtualwallet.models.dtos.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.context.annotation.PropertySource;

import java.time.YearMonth;

@Data
@PropertySource("classpath:messages.properties")
public class CardInput {

    @NotBlank(message = "{error.cardNumberEmpty}")
    @Pattern(regexp = "\\d{16}", message = "{error.cardNumberDigits}")
    private String cardNumber;

    @NotBlank(message = "{error.cardHolderEmpty}")
    @Size(min = 2, max = 30, message = "{error.cardHolderLength}")
    @Pattern(regexp = "^[A-Za-z]+( [A-Za-z]+)+$", message = "{error.cardHolderFormat}")
    private String cardHolder;

    @NotBlank(message = "{error.expirationDateEmpty}")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "{error.expirationDateFormat}")
    private String expirationDate;

    @NotBlank(message = "{error.cvvEmpty}")
    @Pattern(regexp = "\\d{3}", message = "{error.cvvDigits}")
    private String cvv;

    public YearMonth getExpirationDateAsYearMonth() {
        return YearMonth.parse("20" + expirationDate.split("/")[1] + "-" + expirationDate.split("/")[0]);
    }
}
