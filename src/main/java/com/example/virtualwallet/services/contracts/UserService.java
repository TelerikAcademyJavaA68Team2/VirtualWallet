package com.example.virtualwallet.services.contracts;


import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.UserPageOutput;
import com.example.virtualwallet.models.dtos.user.ProfileUpdateInput;
import com.example.virtualwallet.models.dtos.user.UserProfileOutput;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    void createUser(User user);

    String findByUsernameOrEmailOrPhoneNumber(String input);

    UserProfileOutput getAuthenticatedUserProfile();

    void softDeleteAuthenticatedUser();

    void updateAuthenticatedUser(ProfileUpdateInput input);

    User getAuthenticatedUser();

    User loadUserByUsername(String username);

    UserPageOutput filterUsers(UserFilterOptions userFilterOptions);

    boolean checkIfEmailIsTaken(String email);

    boolean checkIfPhoneNumberIsTaken(String phoneNumber);
}