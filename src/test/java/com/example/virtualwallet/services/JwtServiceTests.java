package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.models.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static com.example.virtualwallet.Helpers.createMockUser;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTests {
    public static final String INVALID_TOKEN = "Invalid token: Please log in again.";
    public static final String TOKEN = "eyJhbGciOiJIUzM4NCJ9";
    public static final String EXPIRED_TOKEN = "expired-token";
    public static final String INVALID_USERNAME = "invalidUser";

    @Mock
    private UserDetails mockUserDetails;

    @InjectMocks
    JwtServiceImpl service;

    @Mock
    User user;

    @BeforeEach
    void setup(){
        user = createMockUser();
    }

    @Test
    void generateToken_ShouldGenerateValidToken_Always() {

        // Act
        String token = service.generateToken(user);

        // Assert
        Assertions.assertNotNull(token);
        Assertions.assertTrue(token.startsWith(TOKEN));
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername_FromToken() {
        // Arrange
        String token = service.generateToken(user);

        // Act
        String extractedUsername = service.extractUsername(token);

        // Assert
        Assertions.assertEquals(user.getUsername(), extractedUsername);
    }

    @Test
    void isValid_ShouldReturnTrue_When_TokenIsValid() {
        // Arrange

        String token = service.generateToken(user);

        when(mockUserDetails.getUsername()).thenReturn(user.getUsername());

        // Act
        boolean isValid = service.isValid(token, mockUserDetails);

        // Assert
        Assertions.assertTrue(isValid);
    }

    @Test
    void isValid_ShouldThrowException_When_TokenIsInvalid() {

        String token = service.generateToken(user);

        when(mockUserDetails.getUsername()).thenReturn(INVALID_USERNAME);

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () -> {
            service.isValid(token, mockUserDetails);
        });

        Assertions.assertEquals(INVALID_TOKEN, exception.getMessage());
    }

    @Test
    void extractClaim_ShouldReturnCorrectClaim_WhenTokenIsValid() {
        // Arrange
        String token = service.generateToken(user);

        // Act
        String extractedSubject = service.extractClaim(token, Claims::getSubject);

        // Assert
        Assertions.assertEquals(user.getUsername(), extractedSubject);
    }

    @Test
    void isTokenExpired_ShouldThrowException_WhenTokenIsExpired() {
        // Arrange
        String fakeToken = EXPIRED_TOKEN;

        JwtServiceImpl service = Mockito.spy(new JwtServiceImpl());

        Claims mockClaims = mock(Claims.class);
        when(mockClaims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() - 10000));

        doReturn(mockClaims).when(service).extractAllClaims(fakeToken);

        // Act + Assert
        assertThrows(UnauthorizedAccessException.class, () -> service.isTokenExpired(fakeToken));
    }

}