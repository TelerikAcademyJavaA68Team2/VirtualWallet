package com.example.virtualwallet.exceptions.exceptionHandlers;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandlerMVC {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("status", HttpStatus.NOT_FOUND);
        return "error";
    }

    // Handle 404 errors (No mapping found)
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", HttpStatus.NOT_FOUND);
        return "error";
    }

    // Handle 405 errors (Wrong method used - method is not supported)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, Model model) {
        model.addAttribute("error", "This action is not allowed!");
        model.addAttribute("status", HttpStatus.METHOD_NOT_ALLOWED);
        return "error";
    }

    // Handle 401 errors (Unauthorized)
    @ExceptionHandler(UnauthorizedAccessException.class)
    public String handleUnauthorizedAccessException(UnauthorizedAccessException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        redirectAttributes.addFlashAttribute("status", HttpStatus.UNAUTHORIZED);
        return "redirect:/mvc/auth/login";
    }

    @ExceptionHandler(ClassCastException.class)
    public String handleClassCastException(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return "redirect:" + uri;
    }

    @ExceptionHandler(NumberFormatException.class)
    public String handleNumberFormatException(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return "redirect:" + uri;
    }

    // Handle all other exceptions
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public String handleGlobalException(Exception ex, Model model) {
//        model.addAttribute("error", "Something went wrong. Please try again later.");
//        model.addAttribute("status", HttpStatus.NOT_FOUND);
//        return "error";
//    }
}
