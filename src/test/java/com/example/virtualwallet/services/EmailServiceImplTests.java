package com.example.virtualwallet.services;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static com.example.virtualwallet.services.EmailServiceImpl.CONFIRMATION_FAILED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTests {

    public static final String MAIL_SERVER_ERROR = "Mail server error";
    private static final String MOCK_FIRST_NAME = "John";
    private static final String MOCK_EMAIL = "john.doe@example.com";
    private static final String MOCK_TOKEN_ID = "abc123";
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void sendVerificationEmail_Success() {
        // Arrange
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendVerificationEmail(MOCK_FIRST_NAME, MOCK_EMAIL, MOCK_TOKEN_ID, false);

        // Assert
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
    }

    @Test
    void sendResetPasswordEmail_Success() {
        // Arrange
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendResetPasswordEmail(MOCK_FIRST_NAME, MOCK_EMAIL, MOCK_TOKEN_ID, false);

        // Assert
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        MimeMessage sentMessage = captor.getValue();
        assertNotNull(sentMessage);
    }

    @Test
    void sendVerificationEmail_ExceptionThrown() {
        // Arrange
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException(MAIL_SERVER_ERROR)).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendVerificationEmail(MOCK_FIRST_NAME, MOCK_EMAIL, MOCK_TOKEN_ID, true));
        assertEquals(CONFIRMATION_FAILED, exception.getMessage());
    }

    @Test
    void sendResetPasswordEmail_ExceptionThrown() {
        // Arrange
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        lenient().doThrow(new RuntimeException(MAIL_SERVER_ERROR)).when(mailSender).send(any(MimeMessage.class));
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendResetPasswordEmail(MOCK_FIRST_NAME, MOCK_EMAIL, MOCK_TOKEN_ID, true));
        assertEquals(CONFIRMATION_FAILED, exception.getMessage());
    }
}