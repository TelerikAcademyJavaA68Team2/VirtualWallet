package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.UserOutput;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import com.example.virtualwallet.repositories.UserRepository;
import com.example.virtualwallet.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:messages.properties")
public class UserServiceImpl implements UserService {
    @Value("${error.userNotLoggedIn}")
    public static String LOGIN_FIRST;

    @Value("${error.userNotFound}")
    public static String USER_NOT_FOUND;

    private final UserRepository userRepository;


    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User with username: %s not found!", username)));
    }

    @Override
    public User getUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(
                String.format("User with email: %s not found!", email)));
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new EntityNotFoundException(
                String.format("User with phone number: %s not found!", phoneNumber)));
    }

    public User getAuthenticatedUser() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (user == null) {
                throw new EntityNotFoundException(USER_NOT_FOUND);
            }
            return loadUserByUsername(user.getUsername());
        } catch (Exception e) {
            throw new UnauthorizedAccessException(LOGIN_FIRST);
        }
    }

    @Override
    public List<UserOutput> getAllUsers() {
        return null;
        /*return userRepository.findAllUsersWithTotalBalance();*/
    }

    @Override
    public Page<UserOutput> filterUsers(UserFilterOptions userFilterOptions, Pageable pageable) {

        String username = userFilterOptions.getUsername().orElse(null);
        String email = userFilterOptions.getEmail().orElse(null);

        String phoneNumber = userFilterOptions.getPhoneNumber()
                .map(number -> "%" + number + "%")
                .orElse(null);

        Role role = userFilterOptions.getRole().map(Role::valueOf).orElse(null);
        String accountStatus = userFilterOptions.getAccountStatus().orElse(null); // Pass as string
        BigDecimal minTotalBalance = userFilterOptions.getMinTotalBalance().orElse(null);
        BigDecimal maxTotalBalance = userFilterOptions.getMaxTotalBalance().orElse(null);

/*        return userRepository.findUsersWithTotalBalance(
                username,
                email,
                phoneNumber, // Pass the constructed pattern
                role,
                accountStatus,
                minTotalBalance,
                maxTotalBalance,
                pageable
        );*/
        return null;
    }
}