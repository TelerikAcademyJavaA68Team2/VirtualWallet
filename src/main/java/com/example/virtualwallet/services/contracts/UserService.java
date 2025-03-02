package com.example.virtualwallet.services.contracts;


import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.UserOutput;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;


import java.util.List;
import java.util.UUID;

public interface UserService extends UserDetailsService {

    void save(User user);

    User getUserById(UUID id);

    User getAuthenticatedUser();

    User loadUserByUsername(String username);

    User getUserByEmail(String email);

    User getUserByPhoneNumber(String phoneNumber);

    List<UserOutput> getAllUsers();

    Page<UserOutput> filterUsers(UserFilterOptions userFilterOptions, Pageable pageable);
}

