package com.example.virtualwallet.auth.emailVerification;

import com.example.virtualwallet.exceptions.EmailConfirmationException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.models.enums.AccountStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EmailConfirmationServiceImpl implements EmailConfirmationService{

    private final EmailConfirmationRepository emailConfirmationRepository;

    public void save(EmailConfirmationToken token) {
        emailConfirmationRepository.save(token);
    }

    public void confirmEmailToken(UUID id) {
        EmailConfirmationToken token = emailConfirmationRepository.findById(id).orElseThrow(() -> new InvalidUserInputException("You provided wrong confirmation token"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new EmailConfirmationException("Email confirmation has expired please try to login again to receive new confirmation email.");
        }
        token.getUser().setStatus(AccountStatus.ACTIVE);
        token.setConfirmedAt(LocalDateTime.now());
        emailConfirmationRepository.save(token);
    }

}
