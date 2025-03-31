package com.example.virtualwallet.services;

import com.example.virtualwallet.Helpers;
import com.example.virtualwallet.services.contracts.JwtService;
import com.example.virtualwallet.exceptions.CaptchaMismatchException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.PasswordMismatchException;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.auth.DeleteAccountInput;
import com.example.virtualwallet.models.dtos.auth.LoginUserInput;
import com.example.virtualwallet.models.dtos.auth.RegisterUserInput;
import com.example.virtualwallet.models.dtos.user.PasswordUpdateInput;
import com.example.virtualwallet.services.contracts.EmailConfirmationService;
import com.example.virtualwallet.services.contracts.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTests {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailConfirmationService emailConfirmationService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void register_ShouldReturnToken_WhenUserIsRegistered() {
        // Arrange
        RegisterUserInput registrationRequest = Helpers.createMockAUserRegistrationDto();
        User user = Helpers.createMockAdmin();
        user.setId(null);

        Mockito.when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword123");
        Mockito.doNothing().when(emailConfirmationService).createAndSendEmailConfirmationToUser(user, true); // Mock email service

        Mockito.when(userService.loadUserByUsername(registrationRequest.getUsername())).thenReturn(user);

        // Act
        String result = authenticationService.register(registrationRequest);

        // Assert
        Assertions.assertEquals("Thanks for registering please confirm your email", result);
    }

    @Test
    void register_ShouldThrowExc_WhenPasswordConfirmInvalid() {
        // Arrange
        RegisterUserInput registrationRequest = Helpers.createMockAUserRegistrationDto();
        registrationRequest.setPasswordConfirm("invalid");
        User user = Helpers.createMockUser();
        user.setId(null);

        // Act, Assert
        Assertions.assertThrows(InvalidUserInputException.class, () -> authenticationService.register(registrationRequest));
    }

    @Test
    void registerMvc_ShouldCreateUser_WhenUserIsRegistered() {
        // Arrange
        RegisterUserInput registrationRequest = Helpers.createMockAUserRegistrationDto();
        User user = Helpers.createMockUser();
        user.setId(null);

        Mockito.when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword123");
        Mockito.doNothing().when(emailConfirmationService).createAndSendEmailConfirmationToUser(user, false); // Mock email service

        Mockito.when(userService.loadUserByUsername(registrationRequest.getUsername())).thenReturn(user);

        // Act, Assert
        Assertions.assertDoesNotThrow(() -> authenticationService.registerForMvc(registrationRequest));
    }

    @Test
    void registerMvc_ShouldThrowExc_WhenPasswordConfirmInvalid() {
        // Arrange
        RegisterUserInput registrationRequest = Helpers.createMockAUserRegistrationDto();
        registrationRequest.setPasswordConfirm("invalid");

        // Act, Assert
        Assertions.assertThrows(InvalidUserInputException.class, () -> authenticationService.registerForMvc(registrationRequest));
    }

    @Test
    void authenticate_ShouldReturnToken_WhenCredentialsAreValid() {
        // Arrange
        User user = Helpers.createMockUser();
        LoginUserInput loginRequest = Helpers.createMockALoginDto();
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        Mockito.when(userService.loadUserByUsername(loginRequest.getUsername())).thenReturn(user);
        Mockito.when(jwtService.generateToken(user)).thenReturn("generatedToken");

        // Act
        String token = authenticationService.authenticate(loginRequest);

        // Assert
        Assertions.assertEquals("generatedToken", token);
    }

    @Test
    void authenticate_ShouldThrowException_When_CredentialsAreNotValid() {
        // Arrange
        LoginUserInput loginRequest = Helpers.createMockALoginDto();
        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenThrow(BadCredentialsException.class);

        // Act & Assert
        Assertions.assertThrows(ResponseStatusException.class, () ->
                authenticationService.authenticate(loginRequest));
    }

    @Test
    void updateUserPassword_ShouldUpdatePassword_WhenInputsAreValid() {
        // Arrange
        PasswordUpdateInput request = Helpers.createMockPasswordUpdateDto();
        User user = Helpers.createMockUser();
        Mockito.when(userService.getAuthenticatedUser()).thenReturn(user);
        Mockito.when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        // Act
        Assertions.assertDoesNotThrow(() -> authenticationService.updateUserPassword(request));

        // Assert
        Mockito.verify(userService).save(user);
        Mockito.verify(passwordEncoder).encode(request.getNewPassword());
    }

    @Test
    void updateUserPassword_ShouldThrowException_WhenNewPasswordsDoNotMatch() {
        // Arrange
        PasswordUpdateInput request = Helpers.createMockPasswordUpdateDto();
        request.setNewPasswordConfirm("wrongConfirm");

        // Act & Assert
        Assertions.assertThrows(PasswordMismatchException.class, () -> authenticationService.updateUserPassword(request));
    }

    @Test
    void updateUserPassword_ShouldThrowException_WhenOldPasswordIsIncorrect() {
        // Arrange
        PasswordUpdateInput request = Helpers.createMockPasswordUpdateDto();
        User user = Helpers.createMockUser();
        Mockito.when(userService.getAuthenticatedUser()).thenReturn(user);
        Mockito.when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        // Act & Assert
        Assertions.assertThrows(InvalidUserInputException.class, () -> authenticationService.updateUserPassword(request));
    }

    @Test
    void softDeleteAuthenticatedUser_ShouldDeleteUser_WhenCaptchaAndPasswordAreCorrect() {
        // Arrange
        DeleteAccountInput request = Helpers.createMockDeleteAccountDto();
        User user = Helpers.createMockUser();
        Mockito.when(userService.getAuthenticatedUser()).thenReturn(user);
        Mockito.when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        // Act
        Assertions.assertDoesNotThrow(() -> authenticationService.softDeleteAuthenticatedUser(request));

        // Assert
        Mockito.verify(userService).softDeleteAuthenticatedUser();
    }

    @Test
    void softDeleteAuthenticatedUser_ShouldThrowException_WhenCaptchaIsIncorrect() {
        // Arrange
        DeleteAccountInput request = Helpers.createMockDeleteAccountDto();
        request.setCaptcha("wrongCaptcha");

        // Act & Assert
        Assertions.assertThrows(CaptchaMismatchException.class, () -> authenticationService.softDeleteAuthenticatedUser(request));
    }

    @Test
    void softDeleteAuthenticatedUser_ShouldThrowException_WhenPasswordIsIncorrect() {
        // Arrange
        DeleteAccountInput request = Helpers.createMockDeleteAccountDto();
        User user = Helpers.createMockUser();
        Mockito.when(userService.getAuthenticatedUser()).thenReturn(user);
        Mockito.when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        // Act & Assert
        Assertions.assertThrows(PasswordMismatchException.class, () -> authenticationService.softDeleteAuthenticatedUser(request));
    }

    @Test
    void register_ShouldThrowException_WhenUsernameIsTaken() {
        // Arrange
        RegisterUserInput registrationRequest = Helpers.createMockAUserRegistrationDto();
        Mockito.when(userService.checkIfUsernameIsTaken(registrationRequest.getUsername())).thenReturn(true);

        // Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> authenticationService.register(registrationRequest));
    }

    @Test
    void register_ShouldThrowException_WhenEmailIsTaken() {
        // Arrange
        RegisterUserInput registrationRequest = Helpers.createMockAUserRegistrationDto();
        Mockito.when(userService.checkIfUsernameIsTaken(registrationRequest.getUsername())).thenReturn(false);
        Mockito.when(userService.checkIfEmailIsTaken(registrationRequest.getEmail())).thenReturn(true);

        // Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> authenticationService.register(registrationRequest));
    }

    @Test
    void register_ShouldThrowException_WhenPhoneNumberIsTaken() {
        // Arrange
        RegisterUserInput registrationRequest = Helpers.createMockAUserRegistrationDto();
        Mockito.when(userService.checkIfUsernameIsTaken(registrationRequest.getUsername())).thenReturn(false);
        Mockito.when(userService.checkIfEmailIsTaken(registrationRequest.getEmail())).thenReturn(false);
        Mockito.when(userService.checkIfPhoneNumberIsTaken(registrationRequest.getPhoneNumber())).thenReturn(true);

        // Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> authenticationService.register(registrationRequest));
    }

    @Test
    void register_ShouldPassValidation_WhenAllInputsAreValid() {
        // Arrange
        RegisterUserInput registrationRequest = Helpers.createMockAUserRegistrationDto();
        User user = Helpers.createMockAdmin();
        user.setId(null);
        Mockito.when(userService.checkIfUsernameIsTaken(registrationRequest.getUsername())).thenReturn(false);
        Mockito.when(userService.checkIfEmailIsTaken(registrationRequest.getEmail())).thenReturn(false);
        Mockito.when(userService.checkIfPhoneNumberIsTaken(registrationRequest.getPhoneNumber())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword123");
        Mockito.doNothing().when(emailConfirmationService).createAndSendEmailConfirmationToUser(user, true);
        Mockito.when(userService.loadUserByUsername(registrationRequest.getUsername())).thenReturn(user);

        // Act
        String result = authenticationService.register(registrationRequest);

        // Assert
        Assertions.assertEquals("Thanks for registering please confirm your email", result);
    }

    @Test
    void registerForMvc_ShouldThrowException_WhenUsernameIsTaken() {
        // Arrange
        RegisterUserInput registrationRequest = Helpers.createMockAUserRegistrationDto();
        Mockito.when(userService.checkIfUsernameIsTaken(registrationRequest.getUsername())).thenReturn(true);

        // Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> authenticationService.registerForMvc(registrationRequest));
    }

    @Test
    void registerForMvc_ShouldThrowException_WhenEmailIsTaken() {
        // Arrange
        RegisterUserInput registrationRequest = Helpers.createMockAUserRegistrationDto();
        Mockito.when(userService.checkIfUsernameIsTaken(registrationRequest.getUsername())).thenReturn(false);
        Mockito.when(userService.checkIfEmailIsTaken(registrationRequest.getEmail())).thenReturn(true);

        // Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> authenticationService.registerForMvc(registrationRequest));
    }

    @Test
    void registerForMvc_ShouldThrowException_WhenPhoneNumberIsTaken() {
        // Arrange
        RegisterUserInput registrationRequest = Helpers.createMockAUserRegistrationDto();
        Mockito.when(userService.checkIfUsernameIsTaken(registrationRequest.getUsername())).thenReturn(false);
        Mockito.when(userService.checkIfEmailIsTaken(registrationRequest.getEmail())).thenReturn(false);
        Mockito.when(userService.checkIfPhoneNumberIsTaken(registrationRequest.getPhoneNumber())).thenReturn(true);

        // Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> authenticationService.registerForMvc(registrationRequest));
    }

    @Test
    void registerForMvc_ShouldPassValidation_WhenAllInputsAreValid() {
        // Arrange
        RegisterUserInput registrationRequest = Helpers.createMockAUserRegistrationDto();
        User user = Helpers.createMockUser();
        user.setId(null);
        Mockito.when(userService.checkIfUsernameIsTaken(registrationRequest.getUsername())).thenReturn(false);
        Mockito.when(userService.checkIfEmailIsTaken(registrationRequest.getEmail())).thenReturn(false);
        Mockito.when(userService.checkIfPhoneNumberIsTaken(registrationRequest.getPhoneNumber())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword123");
        Mockito.doNothing().when(emailConfirmationService).createAndSendEmailConfirmationToUser(user, false);
        Mockito.when(userService.loadUserByUsername(registrationRequest.getUsername())).thenReturn(user);

        // Act & Assert
        Assertions.assertDoesNotThrow(() -> authenticationService.registerForMvc(registrationRequest));
    }
}