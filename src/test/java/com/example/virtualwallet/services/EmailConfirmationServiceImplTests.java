package com.example.virtualwallet.services;

import com.example.virtualwallet.Helpers;
import com.example.virtualwallet.exceptions.*;
import com.example.virtualwallet.models.EmailConfirmationToken;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.repositories.EmailConfirmationRepository;
import com.example.virtualwallet.services.contracts.EmailService;
import com.example.virtualwallet.services.contracts.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.example.virtualwallet.services.EmailConfirmationServiceImpl.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailConfirmationServiceImplTests {

    public static final String TOKEN_WAS_NOT_FOUND = "The confirmation token was not found.";
    @Mock
    private EmailConfirmationRepository emailConfirmationRepository;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailConfirmationServiceImpl emailConfirmationService;

    private User mockUser;
    private EmailConfirmationToken mockToken;

    @BeforeEach
    void setup() {
        mockUser = Helpers.createMockAdmin();
        mockUser.setStatus(AccountStatus.PENDING);

        mockToken = new EmailConfirmationToken(UUID.randomUUID(), mockUser);
        mockToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
    }

    @Test
    void createAndSendEmailConfirmationToUser_Success() {
        // Arrange
        when(userService.loadUserByUsername(eq(mockUser.getUsername()))).thenReturn(mockUser);
        when(emailConfirmationRepository.checkIfTokenWasAlreadySent(eq(mockUser.getEmail()), any(LocalDateTime.class)))
                .thenReturn(false);

        // Act
        emailConfirmationService.createAndSendEmailConfirmationToUser(mockUser, false);

        // Assert
        ArgumentCaptor<EmailConfirmationToken> tokenCaptor = ArgumentCaptor.forClass(EmailConfirmationToken.class);
        verify(emailConfirmationRepository).save(tokenCaptor.capture());
        EmailConfirmationToken savedToken = tokenCaptor.getValue();
        assertNotNull(savedToken);
        assertEquals(mockUser, savedToken.getUser());
        assertNotNull(savedToken.getId());
        assertTrue(savedToken.getExpiresAt().isAfter(LocalDateTime.now()));
        verify(emailService).sendVerificationEmail(eq(mockUser.getFirstName()), eq(mockUser.getEmail()),
                eq(savedToken.getId().toString()), eq(false));
    }

    @Test
    void createAndSendEmailConfirmationToUser_EmailAlreadyConfirmed_ThrowsException() {
        // Arrange
        mockUser.setStatus(AccountStatus.ACTIVE);

        // Act & Assert
        InvalidUserInputException exception = assertThrows(InvalidUserInputException.class, () ->
                emailConfirmationService.createAndSendEmailConfirmationToUser(mockUser, false));
        assertEquals(EMAIL_IS_ALREADY_CONFIRMED, exception.getMessage());
        verify(emailConfirmationRepository, never()).save(any());
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    void createAndSendEmailConfirmationToUser_TokenAlreadySent_ThrowsException() {
        // Arrange
        when(emailConfirmationRepository.checkIfTokenWasAlreadySent(eq(mockUser.getEmail()), any(LocalDateTime.class)))
                .thenReturn(true);

        // Act & Assert
        DuplicateEntityException exception = assertThrows(DuplicateEntityException.class, () ->
                emailConfirmationService.createAndSendEmailConfirmationToUser(mockUser, false));
        assertEquals(CONFIRMATION_ALREADY_SENT, exception.getMessage());
        verify(emailConfirmationRepository, never()).save(any());
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    void confirmEmailToken_Success() {
        // Arrange
        when(emailConfirmationRepository.findById(mockToken.getId())).thenReturn(Optional.of(mockToken));

        // Act
        emailConfirmationService.confirmEmailToken(mockToken.getId());

        // Assert
        verify(userService).save(mockUser);
        verify(emailConfirmationRepository).save(mockToken);
        assertEquals(AccountStatus.ACTIVE, mockUser.getStatus());
        assertNotNull(mockToken.getConfirmedAt());
    }

    @Test
    void confirmEmailToken_TokenNotFound_ThrowsException() {
        // Arrange
        UUID invalidTokenId = UUID.randomUUID();
        when(emailConfirmationRepository.findById(invalidTokenId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                emailConfirmationService.confirmEmailToken(invalidTokenId));
        assertEquals(TOKEN_WAS_NOT_FOUND, exception.getMessage());
        verify(userService, never()).save(any());
        verify(emailConfirmationRepository, never()).save(any());
    }

    @Test
    void confirmEmailToken_EmailAlreadyConfirmed_ThrowsException() {
        // Arrange
        mockToken.setConfirmedAt(LocalDateTime.now());
        when(emailConfirmationRepository.findById(mockToken.getId())).thenReturn(Optional.of(mockToken));

        // Act & Assert
        EmailConfirmedException exception = assertThrows(EmailConfirmedException.class, () ->
                emailConfirmationService.confirmEmailToken(mockToken.getId()));
        assertEquals(EMAIL_IS_ALREADY_CONFIRMED, exception.getMessage());
        verify(userService, never()).save(any());
        verify(emailConfirmationRepository, never()).save(any());
    }

    @Test
    void confirmEmailToken_TokenExpired_ThrowsException() {
        // Arrange
        mockToken.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(emailConfirmationRepository.findById(mockToken.getId())).thenReturn(Optional.of(mockToken));

        // Act & Assert
        EmailConfirmationException exception = assertThrows(EmailConfirmationException.class, () ->
                emailConfirmationService.confirmEmailToken(mockToken.getId()));
        assertEquals(TOKEN_EXPIRED, exception.getMessage());
        verify(userService, never()).save(any());
        verify(emailConfirmationRepository, never()).save(any());
    }
}