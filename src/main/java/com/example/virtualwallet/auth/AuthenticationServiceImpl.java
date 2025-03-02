package com.example.virtualwallet.auth;

import com.example.virtualwallet.auth.jwt.JwtService;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.models.Dtos.auth.RegisterUserInput;
import com.example.virtualwallet.models.Dtos.auth.LoginUserInput;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    public static final String WRONG_CREDENTIALS = "Wrong username or password!";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Override
    public String authenticate(LoginUserInput request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, WRONG_CREDENTIALS);
        }

        User user = userService.loadUserByUsername(request.getUsername());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        return jwtService.generateToken(user);
    }

    @Override
    public void authenticateForMvc(LoginUserInput request) {

    }

    @Override
    public String register(RegisterUserInput request) {
        if (!request.getPasswordConfirm().equals(request.getPassword())) {
            throw new InvalidUserInputException("Password Confirmation failed");
        }
        User user = createUserFromRequest(request);
        userService.save(user);
        return jwtService.generateToken(user);
    }

    @Override
    public void registerForMvc(RegisterUserInput request) {

    }

    private User createUserFromRequest(RegisterUserInput request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoto("/images/default-profile-pic.png");
        return user;
    }

}