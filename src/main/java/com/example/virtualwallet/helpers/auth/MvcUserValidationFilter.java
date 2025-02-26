package com.example.virtualwallet.helpers.auth;


import com.example.virtualwallet.models.User;
import com.example.virtualwallet.services.contracts.UserService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MvcUserValidationFilter extends OncePerRequestFilter {

    private final AccountStatusUserDetailsChecker accountStatusChecker = new AccountStatusUserDetailsChecker();
    private final UserService userService;

    public MvcUserValidationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain) throws ServletException, IOException {

        if (isPublicRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            try {
                User user = userService.loadUserByUsername(userDetails.getUsername());
                accountStatusChecker.check(user);

                if (request.getRequestURI().startsWith("/mvc/admin") && !user.getRole().name().equals("ADMIN")) {
                    response.sendRedirect("/mvc/home");
                    return;
                }
            } catch (DisabledException e) {
                request.getSession().invalidate();
                SecurityContextHolder.clearContext();
                response.sendRedirect("/mvc/auth/logout");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPublicRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return requestUri.startsWith("/mvc/auth") ||
                requestUri.startsWith("/mvc/home") ||
                requestUri.startsWith("/css/") ||
                requestUri.startsWith("/js/") ||
                requestUri.startsWith("/images/") ||
                requestUri.startsWith("/static") ||
                requestUri.equals("/error");
    }
}