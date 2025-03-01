/*
package com.example.virtualwallet.services;

import com.example.virtualwallet.Helpers;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.dtos.UserOutput;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import com.example.virtualwallet.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository mockRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void getUserById_ShouldReturnUser_When_UserExists() {
        // Arrange
        User mockUser = Helpers.createMockUser();
        Mockito.when(mockRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        // Act
        User result = service.getUserById(mockUser.getId());

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(mockUser, result);
        Mockito.verify(mockRepository, Mockito.times(1)).findById(mockUser.getId());
    }

    @Test
    void getUserById_ShouldThrowEntityNotFoundExc_When_UserDoesntExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Mockito.when(mockRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.getUserById(userId));
        Assertions.assertEquals("User", exception.getMessage().split(" ")[0].trim());
        Assertions.assertEquals(userId.toString(), exception.getMessage().split(" ")[3].trim());
        Mockito.verify(mockRepository, Mockito.times(1)).findById(userId);
    }

    @Test
    void filterUsers_ShouldReturnUserOutputPage_When_ValidArgs() {
        // Arrange
        UserFilterOptions filterOptions = Helpers.createMockUserFilterOptions();
        Pageable pageable = Helpers.createMockPageable();
        Page<Object[]> mockPage = Helpers.createMockPage();

        Mockito.when(mockRepository.findFilteredUsers(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(mockPage);
        Mockito.when(modelMapper.mapObjectPageToUserOutputPage(mockPage))
                .thenReturn(mockPage.map(row -> Helpers.createMockUserOutput()));

        // Act
        Page<UserOutput> result = service.filterUsers(filterOptions, pageable);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getTotalElements());
        Mockito.verify(mockRepository, Mockito.times(1)).findFilteredUsers(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(modelMapper, Mockito.times(1)).mapObjectPageToUserOutputPage(mockPage);
    }

    @Test
    void loadUserByUsername_ShouldReturnUser_When_UserExists() {
        // Arrange
        User mockUser = Helpers.createMockUser();
        Mockito.when(mockRepository.findByUsername(mockUser.getUsername())).thenReturn(Optional.of(mockUser));

        // Act
        User result = service.loadUserByUsername(mockUser.getUsername());

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(mockUser, result);
        Mockito.verify(mockRepository, Mockito.times(1)).findByUsername(mockUser.getUsername());
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundExc_When_UserDoesntExist() {
        // Arrange
        String username = "nonexistent_user";
        Mockito.when(mockRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = Assertions.assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername(username));
        Assertions.assertEquals(String.format("User with username: %s not found!", username), exception.getMessage());
        Mockito.verify(mockRepository, Mockito.times(1)).findByUsername(username);
    }
}*/
