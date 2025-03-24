package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.auth.AuthenticationService;
import com.example.virtualwallet.exceptions.*;
import com.example.virtualwallet.services.contracts.EmailConfirmationService;
import com.example.virtualwallet.models.dtos.auth.LoginUserInput;
import com.example.virtualwallet.models.dtos.auth.RegisterUserInput;
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

    private final AuthenticationService authenticationService;
    private final EmailConfirmationService emailConfirmationService;


    @GetMapping("/login")
    public String getLoginPage(Model model, HttpSession session,
                               @RequestParam(value = "error", required = false) Boolean error,
                               @RequestParam(value = "deleted", required = false) Boolean deleted) {
        if (Boolean.TRUE.equals(error)) {
            model.addAttribute("errorMessage", "Invalid username or password");
        } else if (Boolean.TRUE.equals(deleted)) {
            model.addAttribute("errorMessage", "Invalid username or password"); // no need to tell the user he is deleted
            // ToDo add restore account option or send to restore account page directly
        }

        Boolean hasActiveUser = (Boolean) session.getAttribute("hasActiveUser");
        if (hasActiveUser != null && hasActiveUser) {
            return "redirect:/mvc/home";
        }

        model.addAttribute("loginDto", new LoginUserInput());
        return "Login-View";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model, HttpSession session) {
        Boolean hasActiveUser = (Boolean) session.getAttribute("hasActiveUser");
        if (hasActiveUser != null && hasActiveUser) {
            return "redirect:/mvc/home";
        }

        model.addAttribute("registerRequest", new RegisterUserInput());
        return "Register-View";
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();

        return "redirect:/mvc/home";
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
        } catch (EntityNotFoundException e){
            return "redirect:/mvc/home";
        }
    }
}