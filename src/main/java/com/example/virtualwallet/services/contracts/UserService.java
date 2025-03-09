package com.example.virtualwallet.services.contracts;


import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.pageable.UserPageOutput;
import com.example.virtualwallet.models.dtos.user.ProfileUpdateInput;
import com.example.virtualwallet.models.dtos.user.UserProfileOutput;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserService extends UserDetailsService {

    void createUser(User user);

    String findByUsernameOrEmailOrPhoneNumber(String input);

    UserProfileOutput getAuthenticatedUserProfile();

    UserProfileOutput getUserProfileById(UUID userId);

    void softDeleteAuthenticatedUser();

    void updateAuthenticatedUser(ProfileUpdateInput input);

    User getAuthenticatedUser();

    User loadUserByUsername(String username);

    UserPageOutput filterUsers(UserFilterOptions userFilterOptions);

    boolean checkIfEmailIsTaken(String email);

    boolean checkIfPhoneNumberIsTaken(String phoneNumber);

    boolean checkIfUsernameIsTaken(String username);

    void promoteToAdmin(UUID id);

    void demoteToUser(UUID id);

    void blockUser(UUID id);

    void unblockUser(UUID id);
}