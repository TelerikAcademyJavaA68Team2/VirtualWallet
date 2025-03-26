package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.services.contracts.UserService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.example.virtualwallet.controllers.mvc")
public class GlobalModelAttributeConfig {

    private final UserService userService;

    public GlobalModelAttributeConfig(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("currentUser")
    public User addUserToModel() {
        try {
            return userService.getAuthenticatedUser();
        } catch (UnauthorizedAccessException e) {
            return new User();
        }
    }
}
