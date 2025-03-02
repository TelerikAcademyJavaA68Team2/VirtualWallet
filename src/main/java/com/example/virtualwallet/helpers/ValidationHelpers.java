package com.example.virtualwallet.helpers;

import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.multipart.MultipartFile;

@PropertySource("classpath:messages.properties")
public class ValidationHelpers {

    @Value("${error.unauthorizedCardModification}")
    public static String UNAUTHORIZED_MESSAGE_POST;

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
}
