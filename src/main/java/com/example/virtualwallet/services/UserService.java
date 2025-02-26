package com.example.virtualwallet.services;

import com.example.virtualwallet.models.Dtos.UserOutput;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    User getUserById(UUID id);

    Page<UserOutput> filterUsers(UserFilterOptions userFilterOptions, Pageable pageable);
}

