package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.services.contracts.AuthenticationService;
import com.example.virtualwallet.exceptions.*;
import com.example.virtualwallet.models.dtos.auth.NewPasswordResetInput;
import com.example.virtualwallet.models.dtos.auth.PasswordResetInput;
import com.example.virtualwallet.services.contracts.EmailConfirmationService;
import com.example.virtualwallet.models.dtos.auth.LoginUserInput;
import com.example.virtualwallet.models.dtos.auth.RegisterUserInput;
import com.example.virtualwallet.services.contracts.PasswordResetService;
import com.example.virtualwallet.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mvc/auth")
public class AuthenticationMvcController {

    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String HAS_ACTIVE_USER = "hasActiveUser";
    public static final String REDIRECT_HOME = "redirect:/mvc/home";

    private final AuthenticationService authenticationService;
    private final EmailConfirmationService emailConfirmationService;
    private final PasswordResetService passwordResetService;
    private final UserService userService;


    @GetMapping("/login")
    public String getLoginPage(Model model, HttpSession session,
                               @RequestParam(value = "error", required = false) Boolean error,
                               @RequestParam(value = "deleted", required = false) Boolean deleted) {
        try {
            if (userService.getAuthenticatedUser() != null) {
                return "redirect:/mvc/profile/wallets";
            }
        } catch (UnauthorizedAccessException e) {


            if (Boolean.TRUE.equals(error)) {
                model.addAttribute(ERROR_MESSAGE, INVALID_USERNAME_OR_PASSWORD);
            } else if (Boolean.TRUE.equals(deleted)) {
                model.addAttribute(ERROR_MESSAGE, INVALID_USERNAME_OR_PASSWORD);
            }

            Boolean hasActiveUser = (Boolean) session.getAttribute(HAS_ACTIVE_USER);
            if (hasActiveUser != null && hasActiveUser) {
                return REDIRECT_HOME;
            }

            model.addAttribute("loginDto", new LoginUserInput());
            return "Login-View";
        }
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute(ERROR_MESSAGE, INVALID_USERNAME_OR_PASSWORD);
        } else if (Boolean.TRUE.equals(deleted)) {
            model.addAttribute(ERROR_MESSAGE, INVALID_USERNAME_OR_PASSWORD);

        }

        Boolean hasActiveUser = (Boolean) session.getAttribute(HAS_ACTIVE_USER);
        if (hasActiveUser != null && hasActiveUser) {
            return REDIRECT_HOME;
        }

        model.addAttribute("loginDto", new LoginUserInput());
        return "Login-View";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model, HttpSession session) {
        try {
            if (userService.getAuthenticatedUser() != null) {
                return "redirect:/mvc/profile/wallets";
            }
        } catch (UnauthorizedAccessException e){

        Boolean hasActiveUser = (Boolean) session.getAttribute(HAS_ACTIVE_USER);
        if (hasActiveUser != null && hasActiveUser) {
            return REDIRECT_HOME;
        }

        model.addAttribute("registerRequest", new RegisterUserInput());
        return "Register-View";
        }

        Boolean hasActiveUser = (Boolean) session.getAttribute(HAS_ACTIVE_USER);
        if (hasActiveUser != null && hasActiveUser) {
            return REDIRECT_HOME;
        }

        model.addAttribute("registerRequest", new RegisterUserInput());
        return "Register-View";
    }


    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();

        return REDIRECT_HOME;
    }

    @PostMapping(("/register"))
    public String executeRegisterRequest(@Valid @ModelAttribute("registerRequest") RegisterUserInput registerRequest,
                                         BindingResult errors, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return "Register-View";
        }
        try {
            authenticationService.registerForMvc(registerRequest);
            redirectAttributes.addFlashAttribute("emailConfirmationMessage", "message");
            return "redirect:/mvc/auth/login";
        } catch (DuplicateEntityException e) {

            String field = "";
            String errorCode = "";
            String defaultMsg = "";

            if (e.getMessage().contains("email")) {
                field = "email";
                errorCode = "email.mismatch";
                defaultMsg = e.getMessage();
            }

            if (e.getMessage().contains("username")) {
                field = "username";
                errorCode = "username.mismatch";
                defaultMsg = e.getMessage();
            }

            if (e.getMessage().startsWith("phone number")) {
                field = "phoneNumber";
                errorCode = "phoneNumber.mismatch";
                defaultMsg = e.getMessage();
            }

            errors.rejectValue(field, errorCode, defaultMsg);
            return "Register-View";
        } catch (InvalidUserInputException e) {
            errors.rejectValue("passwordConfirm", "password.mismatch", e.getMessage());
            return "Register-View";
        }
    }

    @GetMapping("/email-confirm")
    public String confirmEmail(@RequestParam UUID token) {
        try {
            emailConfirmationService.confirmEmailToken(token);
            return "Account-Verified-View";
        } catch (EmailConfirmationException e) {
            return "redirect:/mvc/profile?emailTokenExpired=true";
        } catch (EmailConfirmedException e) {
            return "redirect:/mvc/profile";
        } catch (EntityNotFoundException e) {
            return REDIRECT_HOME;
        }
    }

    @GetMapping("/password-reset")
    public String passwordReset(Model model) {
        model.addAttribute("passwordResetInput", new PasswordResetInput());
        return "Forgotten-Password-View";
    }

    @PostMapping("/password-reset")
    public String processPasswordResetInput(@Valid @ModelAttribute("passwordResetInput") PasswordResetInput input, BindingResult errors, Model model) {
        if (errors.hasErrors()) {
            return "Forgotten-Password-View";
        }
        try {
            passwordResetService.sendResetPasswordEmail(input, false);
            return "redirect:/mvc/auth/login";
        } catch (EntityNotFoundException e) {
            errors.rejectValue("email", "email.not.found", e.getMessage());
            model.addAttribute("passwordResetInput", input);
            return "Forgotten-Password-View";
        }catch (EmailConfirmationException e) {
            errors.rejectValue("email", "email.abuse", e.getMessage());
            model.addAttribute("passwordResetInput", input);
            return "Forgotten-Password-View";
        }

    }

    @GetMapping("/password-reset/{id}")
    public String passwordResetWindow(@PathVariable UUID id, Model model) {
        if (!passwordResetService.checkIfTokenExists(id)) {
            return "redirect:/mvc/auth/login";
        }

        model.addAttribute("tokenId", id);
        model.addAttribute("newPasswordInput", new NewPasswordResetInput());
        return "Request-New-Password-View";
    }

    @PostMapping("/password-reset/{id}")
    public String processPasswordResetInput(@PathVariable UUID id, @Valid @ModelAttribute("newPasswordInput") NewPasswordResetInput input, BindingResult errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("tokenId", id);
            model.addAttribute("newPasswordInput", input);
            return "Request-New-Password-View";
        }

        try {
            passwordResetService.processResetPasswordInput(input, id);
        } catch (InvalidUserInputException e) {
            errors.rejectValue("password", "password.mismatch", e.getMessage());
            model.addAttribute("tokenId", id);
            model.addAttribute("newPasswordInput", input);
            return "Request-New-Password-View";
        } catch (EntityNotFoundException e) {
            errors.rejectValue("password", "input.mismatch", e.getMessage());
            model.addAttribute("tokenId", id);
            model.addAttribute("newPasswordInput", input);
            return "Request-New-Password-View";
        }

        return "redirect:/mvc/auth/login";
    }
}