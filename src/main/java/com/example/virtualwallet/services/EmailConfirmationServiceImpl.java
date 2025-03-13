package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.*;
import com.example.virtualwallet.models.EmailConfirmationToken;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.repositories.EmailConfirmationRepository;
import com.example.virtualwallet.services.contracts.EmailConfirmationService;
import com.example.virtualwallet.services.contracts.EmailService;
import com.example.virtualwallet.services.contracts.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EmailConfirmationServiceImpl implements EmailConfirmationService {

    private final EmailConfirmationRepository emailConfirmationRepository;
    private final UserService userService;
    private final EmailService emailService;


    @Override
    public void createAndSendEmailConfirmationToUser(User request, boolean isRestRequest) {
        if (!request.getStatus().equals(AccountStatus.PENDING)) {
            throw new InvalidUserInputException("Email is already confirmed");
        }

        if (emailConfirmationRepository.checkIfTokenWasAlreadySent(request.getId(), LocalDateTime.now().minusMinutes(15))) {
            throw new DuplicateEntityException("Email confirmation was already sent");
        }

        User user = userService.loadUserByUsername(request.getUsername());
        UUID tokenId = UUID.randomUUID();
        EmailConfirmationToken token = new EmailConfirmationToken(tokenId, user);
        emailConfirmationRepository.save(token);

        emailService.sendVerificationEmail(user.getFirstName(), user.getEmail(), tokenId.toString(), isRestRequest);
    }

    public void confirmEmailToken(UUID id) {
        EmailConfirmationToken token = emailConfirmationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("The confirmation token was not found!"));

        if (token.getConfirmedAt() != null) {
            throw new EmailConfirmedException("Your email is already confirmed.");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new EmailConfirmationException("Email confirmation has expired please send new confirmation email.");
        }
        User user = token.getUser();
        user.setStatus(AccountStatus.ACTIVE);
        token.setConfirmedAt(LocalDateTime.now());
        userService.save(user);
        emailConfirmationRepository.save(token);
    }

}