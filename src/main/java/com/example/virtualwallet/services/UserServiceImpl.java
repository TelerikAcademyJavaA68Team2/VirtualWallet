package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.helpers.UserQueryBuilderHelper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.user.ProfileUpdateInput;
import com.example.virtualwallet.models.dtos.user.UserOutput;
import com.example.virtualwallet.models.dtos.user.UserProfileOutput;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import com.example.virtualwallet.repositories.UserRepository;
import com.example.virtualwallet.services.contracts.UserService;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:messages.properties")
public class UserServiceImpl implements UserService {

    @Value("${error.userNotLoggedIn}")
    public static String LOGIN_FIRST;

    @Value("${error.noAuthenticatedUser}")
    public static String USER_NOT_AUTHENTICATED;

    @PersistenceContext
    private EntityManager entityManager;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


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
/*
    @Override
    public User getUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(
                String.format("User with email: %s not found!", email)));
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new EntityNotFoundException(
                String.format("User with phone number: %s not found!", phoneNumber)));
    }*/

    @Override
    public boolean checkIfPhoneNumberIsTaken(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    @Override
    public boolean checkIfEmailIsTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User getAuthenticatedUser() {
        try {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (user == null) {
                throw new InvalidUserInputException(USER_NOT_AUTHENTICATED);
            }
            return loadUserByUsername(user.getUsername());
        } catch (Exception e) {
            throw new UnauthorizedAccessException(e.getMessage());
        }
    }

    @Override
    public void updateAuthenticatedUser(ProfileUpdateInput input) {
        User user = getAuthenticatedUser();
        if (!new BCryptPasswordEncoder().matches(input.getPassword(), user.getPassword())) {
            throw new InvalidUserInputException("You provided wrong password");
        }
        if (input.getNewPassword() != null) {
            if (input.getNewPasswordConfirm() == null) {
                throw new InvalidUserInputException("You need to confirm your new password");
            } else if (!input.getNewPassword().equals(input.getNewPasswordConfirm())) {
                throw new InvalidUserInputException("Your password confirmation doesn't match your new password");
            }
            user.setPassword(new BCryptPasswordEncoder().encode(input.getNewPassword()));
        }

        if (input.getFirstName() != null) {
            user.setFirstName(input.getFirstName());
        }
        if (input.getLastName() != null) {
            user.setLastName(input.getLastName());
        }
        if (input.getEmail() != null) {
            if (checkIfEmailIsTaken(input.getEmail())) {
                throw new DuplicateEntityException("User", "Email", input.getEmail());
            }
            user.setEmail(input.getEmail());
        }
        if (input.getPhoneNumber() != null) {
            if (checkIfPhoneNumberIsTaken(input.getPhoneNumber())) {
                throw new DuplicateEntityException("User", "PhoneNumber", input.getPhoneNumber());
            }
            user.setPhoneNumber(input.getPhoneNumber());
        }
        save(user);
    }

    @Override
    public void softDeleteAuthenticatedUser() {
        User user = getAuthenticatedUser();
        user.markAsDeleted();
        save(user);
    }

    @Override
    public UserProfileOutput getAuthenticatedUserProfile() {
        User user = getAuthenticatedUser();
        return modelMapper.userProfileFromUser(user);
    }

    @Override
    public List<UserOutput> getAllUsers() {
        return userRepository.findAllUsersWithTotalBalance();
    }

    @Override
    public Page<UserOutput> filterUsers(UserFilterOptions userFilterOptions, Pageable pageable) {
        UserQueryBuilderHelper queryBuilder = new UserQueryBuilderHelper();

        queryBuilder.addUsernameFilter(userFilterOptions.getUsername());
        queryBuilder.addEmailFilter(userFilterOptions.getEmail());
        queryBuilder.addPhoneNumberFilter(userFilterOptions.getPhoneNumber());
        queryBuilder.addRoleFilter(userFilterOptions.getRole());
        queryBuilder.addAccountStatusFilter(userFilterOptions.getAccountStatus());
        queryBuilder.addGroupBy();
        queryBuilder.addMinTotalBalanceFilter(userFilterOptions.getMinTotalBalance());
        queryBuilder.addMaxTotalBalanceFilter(userFilterOptions.getMaxTotalBalance());
        queryBuilder.addSorting(
                userFilterOptions.getSortBy().orElse("u.username"),
                userFilterOptions.getSortOrder().orElse("ASC")
        );

        String queryString = queryBuilder.getQueryString();
        Map<String, Object> parameters = queryBuilder.getParameters();

        TypedQuery<UserOutput> query = entityManager.createQuery(queryString, UserOutput.class);
        parameters.forEach(query::setParameter);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<UserOutput> content = query.getResultList();
        long total = countFilteredUsers(queryBuilder.getCountQuery(), parameters);

        return new PageImpl<>(content, pageable, total);
    }

    private long countFilteredUsers(String countQuery, Map<String, Object> parameters) {
        Query query = entityManager.createQuery(countQuery);
        parameters.forEach(query::setParameter);

        try {
            Number countResult = (Number) query.getSingleResult();
            return countResult.longValue();
        } catch (NoResultException | NullPointerException e) {
            return 0;
        }
    }
}