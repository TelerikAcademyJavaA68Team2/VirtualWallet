package com.example.virtualwallet.controllers.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class PublicMvcController {

    @GetMapping
    public String redirectToHomepage() {
        return "redirect:/mvc/home";
    }

    @GetMapping("/mvc/home")
    public String homePage() {
        return "index";
    }

    @GetMapping("/mvc/about")
    public String showAboutPage() {
        return "About-View";
    }

    @GetMapping("/mvc/privacy")
    public String showPrivacyPolicyPage() {
        return "Privacy-Policy-View";
    }

    @GetMapping("/mvc/terms")
    public String showTermsAndConditionsPage() {
        return "Terms-of-Service-View";
    }

    @GetMapping("/mvc/faq")
    public String showFAQPage() {
        return "FAQ-View";
    }

    @GetMapping("/mvc/error")
    public String showErrorPage() {
        return "error";
    }
}