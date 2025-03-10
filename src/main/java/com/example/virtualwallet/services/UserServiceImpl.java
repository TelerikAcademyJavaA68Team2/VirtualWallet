package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InvalidUserInputException;
import com.example.virtualwallet.exceptions.UnauthorizedAccessException;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.pageable.UserPageOutput;
import com.example.virtualwallet.models.dtos.user.ProfileUpdateInput;
import com.example.virtualwallet.models.dtos.user.UserProfileOutput;
import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import com.example.virtualwallet.repositories.UserRepository;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.specifications.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.example.virtualwallet.helpers.ModelMapper.convertToSort;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String LOG_IN_FIRST = "Please log in first!";
    public static final String PASSWORD_DONT_MATCH = "Your password confirmation doesn't match your new password";
    public static final String PASSWORD_CONFIRM = "You need to confirm your new password";
    public static final String WRONG_PASSWORD = "You provided wrong password";

    private final UserRepository userRepository;


    @Override
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Override
    public String findByUsernameOrEmailOrPhoneNumber(String usernameOrEmailOrPhoneNumber) {
        return userRepository.findUsernameByUsernameOrEmailOrPhoneNumber(usernameOrEmailOrPhoneNumber)
                .orElseThrow(() -> new EntityNotFoundException("User"));
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username"));
    }

    @Override
    public boolean checkIfPhoneNumberIsTaken(String phoneNumber) {
        return userRepository.checkIfPhoneNumberIsTaken(phoneNumber);
    }

    @Override
    public boolean checkIfUsernameIsTaken(String username) {
        return userRepository.checkIfUsernameIsTaken(username);
    }

    @Override
    public boolean checkIfEmailIsTaken(String email) {
        return userRepository.checkIfEmailIsTaken(email);
    }

    @Override
    public void promoteToAdmin(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        if (user.getRole().equals(Role.ADMIN)) {
            throw new InvalidUserInputException("The user already has role Admin.");
        }
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    @Override
    public void demoteToUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        if (user.getRole().equals(Role.USER)) {
            throw new InvalidUserInputException("The user already has role User.");
        }
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    @Override
    public void blockUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        if (user.getStatus().equals(AccountStatus.BLOCKED)) {
            throw new InvalidUserInputException("The user is already Blocked.");
        } else if (user.getStatus().equals(AccountStatus.BLOCKED_AND_DELETED)) {
            throw new InvalidUserInputException("The user is already Blocked.");
        } else if (user.getStatus().equals(AccountStatus.ACTIVE)) {
            user.setStatus(AccountStatus.BLOCKED);
        } else if (user.getStatus().equals(AccountStatus.DELETED)) {
            user.setStatus(AccountStatus.BLOCKED_AND_DELETED);
        }
        userRepository.save(user);
    }

    @Override
    public void unblockUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        if (user.getStatus().equals(AccountStatus.ACTIVE) || user.getStatus().equals(AccountStatus.DELETED)) {
            throw new InvalidUserInputException("The user is not Blocked.");
        } else if (user.getStatus().equals(AccountStatus.BLOCKED)) {
            user.setStatus(AccountStatus.ACTIVE);
        } else if (user.getStatus().equals(AccountStatus.BLOCKED_AND_DELETED)) {
            user.setStatus(AccountStatus.DELETED);
        }
        userRepository.save(user);
    }


    @Override
    public User getAuthenticatedUser() {
        try {
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (user == null) {
                throw new InvalidUserInputException(LOG_IN_FIRST);
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
            throw new InvalidUserInputException(WRONG_PASSWORD);
        }
        if (input.getNewPassword() != null) {
            if (input.getNewPasswordConfirm() == null) {
                throw new InvalidUserInputException(PASSWORD_CONFIRM);
            } else if (!input.getNewPassword().equals(input.getNewPasswordConfirm())) {
                throw new InvalidUserInputException(PASSWORD_DONT_MATCH);
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
        userRepository.save(user);
    }

    @Override
    public UserProfileOutput getAuthenticatedUserProfile() {
        User user = getAuthenticatedUser();
        return ModelMapper.userProfileFromUser(user);
    }

    @Override
    public UserProfileOutput getUserProfileById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        return ModelMapper.userProfileFromUser(user);
    }

    @Override
    public void softDeleteAuthenticatedUser() {
        User user = getAuthenticatedUser();
        user.markAsDeleted();
        userRepository.save(user);
    }

    @Override
    public UserPageOutput filterUsers(UserFilterOptions filterOptions) {
        Specification<User> spec = UserSpecification.buildUserSpecification(filterOptions);

        Sort sort = convertToSort(filterOptions.getSortBy(), filterOptions.getSortOrder());
        Pageable pageable = PageRequest.of(filterOptions.getPage(), filterOptions.getSize(), sort);

        Page<User> pageResult = userRepository.findAll(spec, pageable);
        UserPageOutput output = new UserPageOutput();
        output.setTotalResults(pageResult.getTotalElements());
        output.setNumberOfPages(pageResult.getTotalPages());
        output.setContent(pageResult
                .stream()
                .map(ModelMapper::userOutputFromUser)
                .toList());
        return output;
    }

}