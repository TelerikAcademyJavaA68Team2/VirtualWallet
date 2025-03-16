package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.services.contracts.EmailConfirmationService;
import com.example.virtualwallet.exceptions.EmailConfirmationException;
import com.example.virtualwallet.exceptions.EmailConfirmedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/mvc/email")
@RequiredArgsConstructor
public class EmailMvcController {

    private final EmailConfirmationService emailConfirmationService;


    @GetMapping("/confirm")
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
}