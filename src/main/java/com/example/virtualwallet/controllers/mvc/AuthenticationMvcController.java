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
            model.addAttribute("errorMessage", "Your account is deleted");
            // todo add restore account option or send to restore account page directly
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
    public String executeRegisterRequest(@Valid @ModelAttribute("registerRequest") RegisterUserInput registerRequest, BindingResult errors) {
        if (errors.hasErrors()) {
            return "Register-View";
        }
        try {
            authenticationService.registerForMvc(registerRequest);
            return "redirect:/mvc/auth/login";
        } catch (DuplicateEntityException e) {

            String field = "";
            String errorCode = "";
            String defaultMsg = "";

            if (e.getMessage().startsWith("Email")) {
                field = "email";
                errorCode = "email.mismatch";
                defaultMsg = "Email is already taken!";
            }

            if (e.getMessage().startsWith("Username")) {
                field = "username";
                errorCode = "username.mismatch";
                defaultMsg = "Username is already taken!";
            }

            if (e.getMessage().startsWith("Phone")) {
                field = "phoneNumber";
                errorCode = "phoneNumber.mismatch";
                defaultMsg = "Phone number is already associated with an account!";
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
            return "redirect:/mvc/profile";
        } catch (EmailConfirmationException e) {
            return "redirect:/mvc/profile?emailTokenExpired=true";
        } catch (EmailConfirmedException e) {
            return "redirect:/mvc/profile";
        }catch (EntityNotFoundException e){
            return "redirect:/mvc/home";
        }
    }

    @GetMapping("/email-confirm/resend")
    public String resendEmail() {
        /*try {
            emailConfirmationService.confirmEmailToken(token);
            return "redirect:/mvc/profile";
        } catch (EmailConfirmationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (EmailConfirmedException e) {
            return "redirect:/mvc/profile";
        }*/
        return null;
    }

}