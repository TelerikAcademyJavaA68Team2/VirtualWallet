package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EmailConfirmationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.models.ResetPasswordToken;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.auth.NewPasswordResetInput;
import com.example.virtualwallet.models.dtos.auth.PasswordResetInput;
import com.example.virtualwallet.repositories.PasswordResetRepository;
import com.example.virtualwallet.services.contracts.EmailService;
import com.example.virtualwallet.services.contracts.PasswordResetService;
import com.example.virtualwallet.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetRepository resetRepository;
    private final EmailService emailService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void sendResetPasswordEmail(PasswordResetInput input, boolean isRestRequest) throws EntityNotFoundException {

        User user = userService.findUserByUsernameOrEmailOrPhoneNumber(input.getEmail());
        if (resetRepository.checkIfTokenWasAlreadySent(user.getEmail(), LocalDateTime.now())) {
            throw new EmailConfirmationException("The reset password email was already send!");
        }

        UUID tokenId = UUID.randomUUID();
        ResetPasswordToken token = new ResetPasswordToken(tokenId, user);
        resetRepository.save(token);

        emailService.sendResetPasswordEmail(user.getUsername(), user.getEmail(), tokenId.toString(), isRestRequest);
    }

    @Override
    public void processResetPasswordInput(NewPasswordResetInput input, UUID tokenId) {
        if (!input.getPassword().equals(input.getPasswordConfirm())) {
            throw new InvalidUserInputException("Password confirmation failed");
        }
        ResetPasswordToken token = resetRepository.findById(tokenId).orElseThrow(
                () -> new EntityNotFoundException("The token is invalid or"));

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        token.setConfirmedAt(LocalDateTime.now());

        userService.save(user);
        resetRepository.save(token);
    }

    @Override
    public boolean checkIfTokenExists(UUID tokenId) {
        ResetPasswordToken token = resetRepository.findById(tokenId).orElse(null);
        return token != null && token.getConfirmedAt() == null && !token.getExpiresAt().isBefore(LocalDateTime.now());
    }
}
