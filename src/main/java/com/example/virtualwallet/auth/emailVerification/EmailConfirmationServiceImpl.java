package com.example.virtualwallet.auth.emailVerification;

import com.example.virtualwallet.exceptions.EmailConfirmationException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EmailConfirmationServiceImpl implements EmailConfirmationService {

    private final EmailConfirmationRepository emailConfirmationRepository;
    private final UserRepository userRepository;

    public void save(EmailConfirmationToken token) {
        emailConfirmationRepository.save(token);
    }

    public void confirmEmailToken(UUID id) {
        EmailConfirmationToken token = emailConfirmationRepository.findById(id).orElseThrow(() -> new InvalidUserInputException("You provided wrong confirmation token"));

        if (token.getConfirmedAt()!=null){
            throw new EmailConfirmationException("Your email is already confirmed try to login.");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new EmailConfirmationException("Email confirmation has expired please try to login again to receive new confirmation email.");
        }
        User user = token.getUser();
        user.setStatus(AccountStatus.ACTIVE);
        token.setConfirmedAt(LocalDateTime.now());
        userRepository.save(user);
        emailConfirmationRepository.save(token);
    }

}
