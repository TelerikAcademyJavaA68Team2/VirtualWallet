package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.pageable.UserPageOutput;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;

import java.util.UUID;

public interface UserRepository {

    void createUser(User user);

    void updateUser(User user);

    User getByUsername(String username);

    User getById(UUID id);

    String findByUsernameOrEmailOrPhoneNumber(String input);

    UserPageOutput filterUsers(UserFilterOptions userFilterOptions);

    boolean checkIfEmailIsTaken(String email);

    boolean checkIfPhoneNumberIsTaken(String phoneNumber);

    boolean checkIfUsernameIsTaken(String username);
}