package com.example.virtualwallet.config;

import com.example.virtualwallet.auth.CustomAuthenticationFailureHandler;
import com.example.virtualwallet.auth.filters.*;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.services.contracts.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST_SWAGGER_URL = {"/api/v1/auth/**", "/v2/api-docs", "/v3/api-docs",
            "/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**", "/configuration/ui",
            "/configuration/security", "/swagger-ui/**", "/webjars/**", "/swagger-ui.html", "/api/auth/**",
            "/api/test/**", "/authenticate"};

    private static final String[] PUBLIC_REST_URL_LIST =
            {"/api/auth/**", "/error", "/", "/css/**", "/js/**", "/images/**", "/api/profile/transfers/withdraw"};

    private static final String[] RESTRICTED_REST_URL_LIST = {"/api/admin/**"};

    private static final String[] PUBLIC_MVC_URL_LIST =
            {"/mvc/auth/**", "/error", "/", "/css/**", "/js/**", "/images/**", "/mvc/home", "/mvc/about", "/mvc/terms",
                    "/mvc/auth/**", "/login/oauth2/**", "/oauth/**", "/oauth2/**", "/error", "/", "/css/**", "/js/**",
                    "/images/**", "/mvc/home", "/mvc/about", "/mvc/terms", "/mvc/privacy", "/mvc/faq", "/mvc/error",
                    "/mvc/auth/password-reset"};

    private static final String[] RESTRICTED_MVC_URL_LIST = {"/mvc/admin**"};

    private final UserDetailsService userDetailsService;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final MvcUserValidationFilter mvcUserValidationFilter;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserService userService;

    @Bean
    @Order(1)
    public SecurityFilterChain mvcSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/mvc/**", "/oauth/**", "/oauth2/**", "/login/oauth2/**")
                .authorizeHttpRequests(request -> request
                        .requestMatchers(PUBLIC_MVC_URL_LIST).permitAll()
                        .requestMatchers(RESTRICTED_MVC_URL_LIST).hasAuthority("ADMIN")
                        .anyRequest().authenticated())
                .userDetailsService(userDetailsService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // Stateful for MVC
                .formLogin(form -> form
                        .loginPage("/mvc/auth/login")
                        .successHandler(customSuccessHandler())
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/mvc/auth/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(customOAuth2SuccessHandler())
                        .failureHandler(customAuthenticationFailureHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/mvc/auth/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessUrl("/mvc/home"))
                .addFilterBefore(mvcUserValidationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect("/mvc/auth/login");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/mvc/access-denied");
                        }))
                        .build();
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            HttpSession session = request.getSession();
            User user = (User) authentication.getPrincipal();
            session.setAttribute("currentUser", user.getUsername());
            response.sendRedirect("/mvc/profile/wallets");
        };
    }

    @Bean
    public AuthenticationSuccessHandler customOAuth2SuccessHandler() {
        return (request, response, authentication) -> {
            CustomOAuth2UserImpl oauthUser = (CustomOAuth2UserImpl) authentication.getPrincipal();
            User user = userService.processOAuthLogin(oauthUser);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            session.setAttribute("currentUser", user.getUsername());
            response.sendRedirect("/mvc/profile/wallets");
        };
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/api/**")
                .authorizeHttpRequests(request -> request
                        .requestMatchers(WHITE_LIST_SWAGGER_URL).permitAll()
                        .requestMatchers(PUBLIC_REST_URL_LIST).permitAll()
                        .requestMatchers(RESTRICTED_REST_URL_LIST).hasAnyAuthority("ADMIN")
                        .anyRequest().authenticated())
                .userDetailsService(userDetailsService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("""
                                        {
                                          "error": "Unauthorized",
                                          "message": "You need to be logged in to access this endpoint."
                                        }
                                    """);// Handle 401 (Unauthorized)
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("""
                                        {
                                          "error": "Forbidden",
                                          "message": "You don't have permission to access this endpoint."
                                        }
                                    """);// Handle 403 (Forbidden)
                        }))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}