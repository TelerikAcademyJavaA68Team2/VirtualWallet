package com.example.virtualwallet.repositories;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.pageable.UserPageOutput;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;

public interface UserRepository {

    void createUser(User user);

    void updateUser(User user);

    User getByUsername(String username);

    String findByUsernameOrEmailOrPhoneNumber(String input);

    UserPageOutput filterUsers(UserFilterOptions userFilterOptions);

    boolean checkIfEmailIsTaken(String email);

    boolean checkIfPhoneNumberIsTaken(String phoneNumber);

    boolean checkIfUsernameIsTaken(String username);
}