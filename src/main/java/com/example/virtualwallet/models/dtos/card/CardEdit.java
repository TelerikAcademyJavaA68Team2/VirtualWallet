package com.example.virtualwallet.models.dtos.card;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.context.annotation.PropertySource;

import java.time.YearMonth;

@Data
@PropertySource("classpath:messages.properties")
public class CardEdit {

    @Pattern(regexp = "^\\d{16}$", message = "{error.cardNumberDigits}")
    private String cardNumber;

    @Size(min = 2, max = 30, message = "{error.cardHolderLength}")
    @Pattern(regexp = "^[A-Za-z]+( [A-Za-z]+)+$", message = "{error.cardHolderFormat}")
    private String cardHolder;

    @Pattern(regexp = "^(0[1-9]|1[0-2])/(\\d{2})$", message = "{error.expirationDateFormat}")
    private String expirationDate;

    @Pattern(regexp = "^\\d{3}$", message = "{error.cvvDigits}")
    private String cvv;

    public YearMonth getExpirationDateAsYearMonth() {
        if (expirationDate == null || expirationDate.isEmpty()) {
            return null;
        }
        String[] parts = expirationDate.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid expiration date format: " + expirationDate);
        }
        return YearMonth.of(2000 + Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
    }

}
