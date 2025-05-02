package com.example.virtualwallet.exceptions.exceptionHandlers;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;

@ControllerAdvice(basePackages = "com.example.virtualwallet.controllers.mvc")
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

//    @ExceptionHandler(ClassCastException.class)
//    public String handleClassCastException(HttpServletRequest request) {
//        String uri = request.getRequestURI();
//        return "redirect:" + uri;
//    }
//
//    @ExceptionHandler(NumberFormatException.class)
//    public String handleNumberFormatException(HttpServletRequest request) {
//        String uri = request.getRequestURI();
//        return "redirect:" + uri;
//    }

    @ExceptionHandler(SQLException.class)
    public String handleSQLException() {

        return "redirect:/mvc/error";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri.startsWith("/mvc/profile/cards")){
            return "redirect:/mvc/profile/cards";
        }else if (uri.startsWith("/mvc/profile")){
            return "redirect:/mvc/profile/wallets";
        }else if (uri.startsWith("/mvc/admin/users")){
            return "redirect:/mvc/admin/users";
        }else if (uri.startsWith("/mvc/admin/transfers")){
            return "redirect:/mvc/admin/transfers";
        }else if (uri.startsWith("/mvc/admin/transactions")){
            return "redirect:/mvc/admin/transactions";
        }else if (uri.startsWith("/mvc/admin/exchanges")){
            return "redirect:/mvc/admin/exchanges";
        }

        return "redirect:/mvc/profile/wallets";
    }

    @ExceptionHandler(Exception.class)
    public String handleGlobalException() {
        return "redirect:/mvc/error";
    }
}
