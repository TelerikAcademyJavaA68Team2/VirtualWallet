package com.example.virtualwallet.helpers;

import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Currency;
import org.springframework.web.multipart.MultipartFile;


public class ValidationHelpers {

    public static final String UNAUTHORIZED_MESSAGE_POST = "Only the card's owner can modify cards!";

    public static boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg")
                || contentType.equals("image/png") || contentType.equals("image/gif"));
    }

    public static void validateUserIsCardOwner(Card card, User user) {
        if (!card.getOwner().equals(user)) {
            throw new UnauthorizedAccessException(UNAUTHORIZED_MESSAGE_POST);
        }
    }

    public static Currency validateAndConvertCurrency(String currencyString) {
        try {
            return Currency.valueOf(currencyString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidUserInputException("Invalid currency: " + currencyString +
                    ". Supported currencies: BGN, EUR, USD.");
        }
    }
}
