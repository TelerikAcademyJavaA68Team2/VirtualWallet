package com.example.virtualwallet.auth;

import com.example.virtualwallet.services.contracts.EmailConfirmationService;
import com.example.virtualwallet.services.contracts.EmailService;
import com.example.virtualwallet.auth.jwt.JwtService;
import com.example.virtualwallet.exceptions.CaptchaMismatchException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.PasswordMismatchException;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.auth.DeleteAccountInput;
import com.example.virtualwallet.models.dtos.auth.LoginUserInput;
import com.example.virtualwallet.models.dtos.auth.RegisterUserInput;
import com.example.virtualwallet.models.dtos.user.PasswordUpdateInput;
import com.example.virtualwallet.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    public static final String WRONG_CREDENTIALS = "Wrong username or password!";
    public static final String DELETE_ACCOUNT_CAPTCHA = "Delete my account";
    public static final String WRONG_CAPTCHA = "Wrong captcha";
    public static final String WRONG_PASSWORD = "Wrong password";
    public static final String CONFIRMATION_FAILED = "Password Confirmation failed";
    public static final String CONFIRM_YOUR_EMAIL = "Thanks for registering please confirm your email";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final EmailConfirmationService emailConfirmationService;


    @Override
    public String authenticate(LoginUserInput request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, WRONG_CREDENTIALS);
        }

        User user = userService.loadUserByUsername(request.getUsername());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        return jwtService.generateToken(user);
    }

    @Override
    public String register(RegisterUserInput request) {
        validateUserRequest(request);
        User newUser = createUserFromRequest(request);
        userService.createUser(newUser);

        User user = userService.loadUserByUsername(request.getUsername());
        emailConfirmationService.createAndSendEmailConfirmationToUser(user, true);
        return CONFIRM_YOUR_EMAIL;
    }

    @Override
    public void registerForMvc(RegisterUserInput request) {
        validateUserRequest(request);
        User newUser = createUserFromRequest(request);
        userService.createUser(newUser);

        User user = userService.loadUserByUsername(request.getUsername());
        emailConfirmationService.createAndSendEmailConfirmationToUser(user, false);
    }

    @Override
    public void updateUserPassword(PasswordUpdateInput request) {
        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new PasswordMismatchException(CONFIRMATION_FAILED);
        }
        User user = userService.getAuthenticatedUser();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidUserInputException(WRONG_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(user);
    }

    @Override
    public void softDeleteAuthenticatedUser(DeleteAccountInput request) {
        if (!request.getCaptcha().equalsIgnoreCase(DELETE_ACCOUNT_CAPTCHA)) {
            throw new CaptchaMismatchException(WRONG_CAPTCHA);
        }
        if (!passwordEncoder.matches(request.getPassword(), userService.getAuthenticatedUser().getPassword())) {
            throw new PasswordMismatchException(WRONG_PASSWORD);
        }
        userService.softDeleteAuthenticatedUser();
    }

    private void validateUserRequest(RegisterUserInput request) {
        if (!request.getPasswordConfirm().equals(request.getPassword())) {
            throw new InvalidUserInputException(CONFIRMATION_FAILED);
        }
        if (userService.checkIfUsernameIsTaken(request.getUsername())) {
            throw new DuplicateEntityException("User", "username", request.getUsername());
        }
        if (userService.checkIfEmailIsTaken(request.getEmail())) {
            throw new DuplicateEntityException("User", "email", request.getEmail());
        }
        if (userService.checkIfPhoneNumberIsTaken(request.getPhoneNumber())) {
            throw new DuplicateEntityException("User", "phone number", request.getPhoneNumber());
        }
    }

    private User createUserFromRequest(RegisterUserInput request) {
        return new User(
                request.getFirstName(),
                request.getLastName(),
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                request.getPhoneNumber());
    }

}