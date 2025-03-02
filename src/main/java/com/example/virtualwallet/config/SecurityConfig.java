package com.example.virtualwallet.config;

import com.example.virtualwallet.auth.CustomAuthenticationFailureHandler;
import com.example.virtualwallet.auth.filters.JwtAuthorizationFilter;
import com.example.virtualwallet.auth.filters.MvcUserValidationFilter;
import com.example.virtualwallet.models.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST_SWAGGER_URL = {"/pesho"};
    private static final String[] PUBLIC_REST_URL_LIST =
            {"/api/home/**", "/api/auth/**", "/error", "/", "/css/**", "/js/**", "/images/**","/api/admin/users"};
    private static final String[] RESTRICTED_REST_URL_LIST = {"/api/admin/**"};

    private static final String[] PUBLIC_MVC_URL_LIST =
            {"/mvc/home/**", "/mvc/auth/**", "/error", "/", "/css/**", "/js/**", "/images/**"};
    private static final String[] RESTRICTED_MVC_URL_LIST = {"/mvc/admin"};

    private final UserDetailsService userDetailsService;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final MvcUserValidationFilter mvcUserValidationFilter;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;


    @Bean
    @Order(1)
    public SecurityFilterChain mvcSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/mvc/**")
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
            response.sendRedirect("/mvc/home");
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
                        .requestMatchers(RESTRICTED_REST_URL_LIST).permitAll()/*hasAnyAuthority("ADMIN")*/
                        .anyRequest().permitAll()/*.authenticated()*/)
                .userDetailsService(userDetailsService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: You need to be logged in to access this page!");
                            // Handle 401 (Unauthorized)
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden: You don't have permission to access this page!");
                            // Handle 403 (Forbidden)
                        }))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}