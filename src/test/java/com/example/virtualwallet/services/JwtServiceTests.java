package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.models.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTests {
/*
    private static final String SECRET_KEY = "9bfb230cbadfdf8af34f02be46188a222b105d14a1dd1f183e8934de0d78c354";
*/

    @Mock
    private UserDetails mockUserDetails;

    @InjectMocks
    JwtServiceImpl service;


    @Test
    void generateToken_ShouldGenerateValidToken_Always() {
        // Arrange
        User mockUser = new User();
        mockUser.setUsername("TestUser");
        mockUser.setPassword("password");

        // Act
        String token = service.generateToken(mockUser);

        // Assert
        Assertions.assertNotNull(token);
        Assertions.assertTrue(token.startsWith("eyJhbGciOiJIUzM4NCJ9"));
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername_FromToken() {
        // Arrange
        String mockUsername = "testUser";
        User mockUser = new User();
        mockUser.setUsername(mockUsername);
        mockUser.setPassword("password");
        String token = service.generateToken(mockUser);

        // Act
        String extractedUsername = service.extractUsername(token);

        // Assert
        Assertions.assertEquals(mockUsername, extractedUsername);
    }

    @Test
    void isValid_ShouldReturnTrue_When_TokenIsValid() {
        // Arrange
        String mockUsername = "testUser";
        User mockUser = new User();
        mockUser.setUsername(mockUsername);
        mockUser.setPassword("password");
        String token = service.generateToken(mockUser);

        Mockito.when(mockUserDetails.getUsername()).thenReturn(mockUsername);

        // Act
        boolean isValid = service.isValid(token, mockUserDetails);

        // Assert
        Assertions.assertTrue(isValid);
    }

    @Test
    void isValid_ShouldThrowException_When_TokenIsInvalid() {
        // Arrange
        String invalidUsername = "invalidUser";
        User mockUser = new User();
        mockUser.setUsername("User");
        mockUser.setPassword("password");

        String token = service.generateToken(mockUser);

        Mockito.when(mockUserDetails.getUsername()).thenReturn(invalidUsername);

        // Act & Assert
        UnauthorizedAccessException exception = Assertions.assertThrows(UnauthorizedAccessException.class, () -> {
            service.isValid(token, mockUserDetails);
        });

        Assertions.assertEquals("Invalid token: Please log in again.", exception.getMessage());
    }

    @Test
    void extractClaim_ShouldReturnCorrectClaim_WhenTokenIsValid() {
        // Arrange
        String mockUsername = "testUser";
        User mockUser = new User();
        mockUser.setUsername(mockUsername);
        mockUser.setPassword("password");
        String token = service.generateToken(mockUser);

        // Act
        String extractedSubject = service.extractClaim(token, Claims::getSubject);

        // Assert
        Assertions.assertEquals(mockUsername, extractedSubject);
    }
}