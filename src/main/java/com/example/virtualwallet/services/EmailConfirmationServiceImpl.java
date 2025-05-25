package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.*;
import com.example.virtualwallet.models.EmailConfirmationToken;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.repositories.EmailConfirmationRepository;
import com.example.virtualwallet.services.contracts.EmailConfirmationService;
import com.example.virtualwallet.services.contracts.EmailService;
import com.example.virtualwallet.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailConfirmationServiceImpl implements EmailConfirmationService {

    public static final String EMAIL_IS_ALREADY_CONFIRMED = "Your email is already confirmed.";
    public static final String CONFIRMATION_WAS_NOT_FOUND = "The confirmation token was";
    public static final String TOKEN_EXPIRED = "Email confirmation has expired please send new confirmation email.";
    public static final String CONFIRMATION_ALREADY_SENT = "Email confirmation was already sent";

    private final EmailConfirmationRepository emailConfirmationRepository;
    private final UserService userService;
    private final EmailService emailService;


    @Override
    public void createAndSendEmailConfirmationToUser(User request, boolean isRestRequest) {
        if (!request.getStatus().equals(AccountStatus.PENDING)) {
            throw new InvalidUserInputException(EMAIL_IS_ALREADY_CONFIRMED);
        }

        if (emailConfirmationRepository.checkIfTokenWasAlreadySent(request.getEmail(), LocalDateTime.now().minusMinutes(15))) {
            throw new DuplicateEntityException(CONFIRMATION_ALREADY_SENT);
        }

        User user = userService.loadUserByUsername(request.getUsername());
        UUID tokenId = UUID.randomUUID();
        EmailConfirmationToken token = new EmailConfirmationToken(tokenId, user);
        emailConfirmationRepository.save(token);

        emailService.sendVerificationEmail(user.getFirstName(), user.getEmail(), tokenId.toString(), isRestRequest);
    }

    public void confirmEmailToken(UUID id) {
        EmailConfirmationToken token = emailConfirmationRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException(CONFIRMATION_WAS_NOT_FOUND));

        if (token.getConfirmedAt() != null) {
            throw new EmailConfirmedException(EMAIL_IS_ALREADY_CONFIRMED);
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new EmailConfirmationException(TOKEN_EXPIRED);
        }
        User user = token.getUser();
        user.setStatus(AccountStatus.ACTIVE);
        token.setConfirmedAt(LocalDateTime.now());
        userService.save(user);
        emailConfirmationRepository.save(token);
    }

}