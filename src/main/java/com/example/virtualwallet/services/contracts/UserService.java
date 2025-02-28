package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Dtos.UserOutput;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserService extends UserDetailsService {

    User getUserById(UUID id);

/*
    Page<UserOutput> filterUsers(UserFilterOptions userFilterOptions, Pageable pageable);
*/

    User loadUserByUsername(String username);
}

