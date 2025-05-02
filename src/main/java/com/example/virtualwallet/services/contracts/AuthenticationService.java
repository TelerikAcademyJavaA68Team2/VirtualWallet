package com.example.virtualwallet.services.contracts;


import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.auth.DeleteAccountInput;
import com.example.virtualwallet.models.dtos.auth.LoginUserInput;
import com.example.virtualwallet.models.dtos.auth.RegisterUserInput;
import com.example.virtualwallet.models.dtos.user.PasswordUpdateInput;

public interface AuthenticationService {

    String authenticate(LoginUserInput request);

    String register(RegisterUserInput request);

    void registerForMvc(RegisterUserInput request);

    void updateUserPassword(PasswordUpdateInput request);

    void softDeleteAuthenticatedUser(DeleteAccountInput request);
}