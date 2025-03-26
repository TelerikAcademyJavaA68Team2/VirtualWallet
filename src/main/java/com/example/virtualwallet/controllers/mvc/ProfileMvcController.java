package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.auth.AuthenticationService;
import com.example.virtualwallet.exceptions.*;
import com.example.virtualwallet.models.dtos.auth.DeleteAccountInput;
import com.example.virtualwallet.models.dtos.user.PasswordUpdateInput;
import com.example.virtualwallet.models.dtos.user.ProfileUpdateInput;
import com.example.virtualwallet.models.dtos.user.UserProfileOutput;
import com.example.virtualwallet.services.contracts.EmailConfirmationService;
import com.example.virtualwallet.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.virtualwallet.helpers.ModelMapper.userOutputToUserUpdateInput;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mvc/profile")
public class ProfileMvcController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final EmailConfirmationService emailConfirmationService;

    @GetMapping
    public String getProfile(@RequestParam(value = "emailError", required = false, defaultValue = "false") boolean emailError
            , @RequestParam(value = "emailTokenExpired", required = false, defaultValue = "false") boolean emailTokenExpired
            , Model model) {


        UserProfileOutput profile = userService.getAuthenticatedUserProfile();
        if (profile.getAccountStatus().equals("Pending")) {
            model.addAttribute("emailError", emailError);
            model.addAttribute("emailTokenExpired", emailTokenExpired);
        } else {
            model.addAttribute("emailError", false);
            model.addAttribute("emailTokenExpired", false);
        }

        model.addAttribute("user", profile);
        return "Profile-View";
    }

    @GetMapping("/update")
    public String showEditProfileForm(Model model) {

        UserProfileOutput user = userService.getAuthenticatedUserProfile();
        ProfileUpdateInput request = userOutputToUserUpdateInput(user);

        model.addAttribute("updateRequest", request);
        return "Update-Profile-View";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("updateRequest") ProfileUpdateInput updateProfileRequest, BindingResult errors,
                                @RequestParam(value = "profilePicture", required = false) MultipartFile profileImage,
                                @RequestParam(value = "removePicture", required = false, defaultValue = "false") boolean removePicture){
        if (errors.hasErrors()) {
            return "Update-Profile-View";
        }
        try {
            userService.updateAuthenticatedUser(updateProfileRequest, profileImage, removePicture);
        } catch (DuplicateEntityException e) {
            if (e.getMessage().contains("Email")) {
                errors.rejectValue("email", "EmailError", e.getMessage());
            } else if (e.getMessage().contains("Phone number")) {
                errors.rejectValue("phoneNumber", "phoneNumberError", e.getMessage());
            }
            return "Update-Profile-View";
        } catch (InvalidFileException | InvalidFileSizeException e){
            errors.rejectValue("profilePicture", "profilePicture.error", e.getMessage());
            return "Update-Profile-View";
        }
        return "redirect:/mvc/profile";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        PasswordUpdateInput request = new PasswordUpdateInput();
        model.addAttribute("passwordUpdateInput", request);
        return "Profile-Change-Password-View";
    }

    @PostMapping("/change-password")
    public String executeChangePasswordForm(@Valid @ModelAttribute("passwordUpdateInput") PasswordUpdateInput request, BindingResult errors, Model model) {
        if (errors.hasErrors()) {
            return "Profile-Change-Password-View";
        }
        try {
            authenticationService.updateUserPassword(request);
            return "redirect:/mvc/profile";
        } catch (InvalidUserInputException e) {
            errors.rejectValue("password", "invalid password", e.getMessage());
            return "Profile-Change-Password-View";
        } catch (PasswordMismatchException e) {
            errors.rejectValue("newPassword", "invalid password confirmation", e.getMessage());
            return "Profile-Change-Password-View";
        }
    }

    @GetMapping("/delete")
    public String getDeleteAccountPage(Model model) {
        model.addAttribute("deleteRequest", new DeleteAccountInput());
        return "Delete-Account-View";
    }

    @PostMapping("/delete")
    public String executeDeleteAccountRequest(@Valid @ModelAttribute("deleteRequest") DeleteAccountInput request, BindingResult errors, HttpSession session) {
        if (errors.hasErrors()) {
            return "Delete-Account-View";
        }
        try {
            authenticationService.softDeleteAuthenticatedUser(request);
            SecurityContextHolder.clearContext();
            session.invalidate();
            return "redirect:/mvc/auth/logout";
        } catch (PasswordMismatchException e) {
            errors.rejectValue("password", "password.mismatch", "Wrong Password");
            return "Delete-Account-View";
        } catch (CaptchaMismatchException e) {
            errors.rejectValue("captcha", "captcha.mismatch", "Wrong Captcha");
            return "Delete-Account-View";
        }
    }

    @GetMapping("/resend-email")
    public String resendEmail() {
        try {
            emailConfirmationService.createAndSendEmailConfirmationToUser
                    (userService.getAuthenticatedUser(), false);
        } catch (InvalidUserInputException ignored) {
        } catch (DuplicateEntityException e) {
            return "redirect:/mvc/profile?emailError=true";
        }

        return "redirect:/mvc/profile";
    }

    @ModelAttribute("user")
    public UserProfileOutput getCurrentUser() {
        return userService.getAuthenticatedUserProfile();
    }
}