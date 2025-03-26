package com.example.virtualwallet.services;

import com.example.virtualwallet.Helpers;
import com.example.virtualwallet.exceptions.*;
import com.example.virtualwallet.models.ResetPasswordToken;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.auth.NewPasswordResetInput;
import com.example.virtualwallet.models.dtos.auth.PasswordResetInput;
import com.example.virtualwallet.repositories.PasswordResetRepository;
import com.example.virtualwallet.services.contracts.EmailService;
import com.example.virtualwallet.services.contracts.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.example.virtualwallet.services.PasswordResetServiceImpl.PASSWORD_CONFIRMATION_FAILED;
import static com.example.virtualwallet.services.PasswordResetServiceImpl.PASSWORD_EMAIL_WAS_ALREADY_SEND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceImplTests {

    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String WRONG_PASSWORD = "wrongPassword";
    public static final String TOKEN_IS_INVALID_OR_NOT_FOUND = "The token is invalid or not found.";
    public static final String NEW_PASSWORD = "newPassword";

    @Mock
    private PasswordResetRepository resetRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    private User mockUser;
    private ResetPasswordToken mockToken;
    private PasswordResetInput mockPasswordResetInput;
    private NewPasswordResetInput mockNewPasswordResetInput;

    @BeforeEach
    void setup() {
        mockUser = Helpers.createMockAdmin();

        mockToken = new ResetPasswordToken(UUID.randomUUID(), mockUser);
        mockToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        mockPasswordResetInput = new PasswordResetInput();
        mockPasswordResetInput.setEmail(mockUser.getEmail());

        mockNewPasswordResetInput = new NewPasswordResetInput();
        mockNewPasswordResetInput.setPassword(NEW_PASSWORD);
        mockNewPasswordResetInput.setPasswordConfirm(NEW_PASSWORD);
    }

    @Test
    void sendResetPasswordEmail_Success() {
        // Arrange
        when(userService.findUserByUsernameOrEmailOrPhoneNumber(eq(mockPasswordResetInput.getEmail()))).thenReturn(mockUser);
        when(resetRepository.checkIfTokenWasAlreadySent(eq(mockUser.getEmail()), any(LocalDateTime.class))).thenReturn(false);

        // Act
        passwordResetService.sendResetPasswordEmail(mockPasswordResetInput, false);

        // Assert
        ArgumentCaptor<ResetPasswordToken> tokenCaptor = ArgumentCaptor.forClass(ResetPasswordToken.class);
        verify(resetRepository).save(tokenCaptor.capture());

        ResetPasswordToken savedToken = tokenCaptor.getValue();
        assertNotNull(savedToken);
        assertEquals(mockUser, savedToken.getUser());
        assertNotNull(savedToken.getId());
        assertTrue(savedToken.getExpiresAt().isAfter(LocalDateTime.now()));

        verify(emailService).sendResetPasswordEmail(eq(mockUser.getUsername()), eq(mockUser.getEmail()),
                eq(savedToken.getId().toString()), eq(false));
    }

    @Test
    void sendResetPasswordEmail_TokenAlreadySent_ThrowsException() {
        // Arrange
        when(userService.findUserByUsernameOrEmailOrPhoneNumber(eq(mockPasswordResetInput.getEmail()))).thenReturn(mockUser);
        when(resetRepository.checkIfTokenWasAlreadySent(eq(mockUser.getEmail()), any(LocalDateTime.class))).thenReturn(true);

        // Act & Assert
        EmailConfirmationException exception = assertThrows(EmailConfirmationException.class, () ->
                passwordResetService.sendResetPasswordEmail(mockPasswordResetInput, false));

        assertEquals(PASSWORD_EMAIL_WAS_ALREADY_SEND, exception.getMessage());
        verify(resetRepository, never()).save(any());
        verify(emailService, never()).sendResetPasswordEmail(anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    void processResetPasswordInput_Success() {
        // Arrange
        when(passwordEncoder.encode(mockNewPasswordResetInput.getPassword())).thenReturn(ENCODED_PASSWORD);
        when(resetRepository.findById(mockToken.getId())).thenReturn(Optional.of(mockToken));

        // Act
        passwordResetService.processResetPasswordInput(mockNewPasswordResetInput, mockToken.getId());

        // Assert
        verify(userService).save(mockUser);
        verify(resetRepository).save(mockToken);
        assertEquals(ENCODED_PASSWORD, mockUser.getPassword());
        assertNotNull(mockToken.getConfirmedAt());
    }

    @Test
    void processResetPasswordInput_PasswordsDoNotMatch_ThrowsException() {
        // Arrange
        mockNewPasswordResetInput.setPasswordConfirm(WRONG_PASSWORD);

        // Act & Assert
        InvalidUserInputException exception = assertThrows(InvalidUserInputException.class, () ->
                passwordResetService.processResetPasswordInput(mockNewPasswordResetInput, mockToken.getId()));
        assertEquals(PASSWORD_CONFIRMATION_FAILED, exception.getMessage());
        verify(userService, never()).save(any());
        verify(resetRepository, never()).save(any());
    }

    @Test
    void processResetPasswordInput_TokenNotFound_ThrowsException() {
        // Arrange
        UUID invalidTokenId = UUID.randomUUID();
        when(resetRepository.findById(invalidTokenId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                passwordResetService.processResetPasswordInput(mockNewPasswordResetInput, invalidTokenId));
        assertEquals(TOKEN_IS_INVALID_OR_NOT_FOUND, exception.getMessage());
        verify(userService, never()).save(any());
        verify(resetRepository, never()).save(any());
    }

    @Test
    void checkIfTokenExists_ValidToken_ReturnsTrue() {
        // Arrange
        when(resetRepository.findById(mockToken.getId())).thenReturn(Optional.of(mockToken));

        // Act
        boolean result = passwordResetService.checkIfTokenExists(mockToken.getId());

        // Assert
        assertTrue(result);
    }

    @Test
    void checkIfTokenExists_ExpiredToken_ReturnsFalse() {
        // Arrange
        mockToken.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(resetRepository.findById(mockToken.getId())).thenReturn(Optional.of(mockToken));

        // Act
        boolean result = passwordResetService.checkIfTokenExists(mockToken.getId());

        // Assert
        assertFalse(result);
    }

    @Test
    void checkIfTokenExists_ConfirmedToken_ReturnsFalse() {
        // Arrange
        mockToken.setConfirmedAt(LocalDateTime.now());
        when(resetRepository.findById(mockToken.getId())).thenReturn(Optional.of(mockToken));

        // Act
        boolean result = passwordResetService.checkIfTokenExists(mockToken.getId());

        // Assert
        assertFalse(result);
    }

    @Test
    void checkIfTokenExists_TokenNotFound_ReturnsFalse() {
        // Arrange
        UUID invalidTokenId = UUID.randomUUID();
        when(resetRepository.findById(invalidTokenId)).thenReturn(Optional.empty());

        // Act
        boolean result = passwordResetService.checkIfTokenExists(invalidTokenId);

        // Assert
        assertFalse(result);
    }
}