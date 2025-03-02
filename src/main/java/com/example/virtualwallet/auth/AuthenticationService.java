package com.example.virtualwallet.auth;


import com.example.virtualwallet.models.Dtos.auth.RegisterUserInput;
import com.example.virtualwallet.models.Dtos.auth.LoginUserInput;

public interface AuthenticationService {

    String authenticate(LoginUserInput request);

    void authenticateForMvc(LoginUserInput request);

    String register(RegisterUserInput request);

    void registerForMvc(RegisterUserInput request);
}