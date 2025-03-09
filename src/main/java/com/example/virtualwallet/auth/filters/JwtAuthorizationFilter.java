package com.example.virtualwallet.auth.filters;

import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.auth.jwt.JwtService;
import com.example.virtualwallet.services.contracts.UserService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public JwtAuthorizationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain) throws ServletException, IOException {

        if (isPublicRestRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);
        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid token please login" + e.getMessage());
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userService.loadUserByUsername(username);

                if (!userDetails.isEnabled() && isRestrictedToBlockedUsersUri(request)) {
                    throw new UnauthorizedAccessException("Your account is blocked!");
                }
                if (!userDetails.isAccountNonLocked()) {
                    throw new UnauthorizedAccessException("Your account is not confirmed yet!");
                }
                if (!userDetails.isCredentialsNonExpired()) { // todo -> maybe add email for restoring account sending
                    //         when trying to login with deleted account
                    throw new UnauthorizedAccessException("Your account was deleted!");
                }

                if (jwtService.isValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    throw new UnauthorizedAccessException("No valid token provided! Try login again.");
                }
            } catch (UnauthorizedAccessException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: " + e.getMessage());
                return;
            } catch (UsernameNotFoundException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Your token is not valid!");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPublicRestRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return requestUri.startsWith("/api/auth");
    }

    private boolean isRestrictedToBlockedUsersUri(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return requestUri.startsWith("/api/profile/transfer/new") ||
                requestUri.startsWith("/api/profile/transaction/new") ||
                requestUri.startsWith("/api/profile/exchange/new");
    }

}