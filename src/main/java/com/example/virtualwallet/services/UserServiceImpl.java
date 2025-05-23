package com.example.virtualwallet.services;

import com.example.virtualwallet.auth.filters.CustomOAuth2UserImpl;
import com.example.virtualwallet.exceptions.*;
import com.example.virtualwallet.helpers.CloudinaryHelper;
import com.example.virtualwallet.helpers.ModelMapper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.pageable.UserPageOutput;
import com.example.virtualwallet.models.dtos.user.PasswordUpdateInput;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.virtualwallet.helpers.ModelMapper.convertToSort;
import static com.example.virtualwallet.helpers.ValidationHelpers.isValidImageFile;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String LOG_IN_FIRST = "Please log in first!";
    public static final String PASSWORD_DONT_MATCH = "Your password confirmation doesn't match your new password";
    public static final String PASSWORD_CONFIRM = "You need to confirm your new password";
    public static final String WRONG_PASSWORD = "You provided wrong password";
    public static final String USER_ALREADY_ADMIN = "The user already has role Admin.";
    public static final String USER_ALREADY_USER = "The user already has role User.";
    public static final String USER_ALREADY_BLOCKED = "The user is already Blocked.";
    public static final String USER_NOT_BLOCKED = "The user is not Blocked.";
    public static final String INVALID_IMAGE = "Invalid image file. Only JPG, PNG, or GIF files are allowed.";
    public static final String FILE_TOO_LARGE = "File is too large! Max size is 5MB.";
    public static final String PHONE_NUMBER_TAKEN = "Phone number '%s' is already associated with another account! Please, " +
            "provide a different phone number.";
    public static final String EMAIL_TAKEN = "Email '%s' is already associated with another account! Please, " +
            "provide a different email.";
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    public static final String PENDING_USERS_CAN_T_BE_BLOCKED = "Pending users can't be blocked";
    public static final String NO_CHANGES_WERE_MADE = "No changes were made";
    public static final String DEFAULT_PROFILE_PIC_PNG = "/images/default-profile-pic.png";
    public static final String USER = "User";
    public static final String USER_NOT_FOUND_MESSAGE = "User with username %s not found!";

    private final UserRepository userRepository;
    private final CloudinaryHelper cloudinaryHelper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User processOAuthLogin(CustomOAuth2UserImpl oAuth2User){
        try{
            return findUserByUsernameOrEmailOrPhoneNumber(oAuth2User.getEmail());
        } catch (EntityNotFoundException e) {
            User newUser = new User();
            String email = oAuth2User.getEmail();
            String fullName = oAuth2User.getName();
            String firstName = fullName.split(" ")[0];
            newUser.setFirstName(firstName);
            if (fullName.contains(" ")){
                String lastName = fullName.split(" ")[1];
                newUser.setLastName(lastName);
            } else {
                newUser.setLastName(firstName);
            }
            newUser.setPhoneNumber(UUID.randomUUID().toString().substring(0, 10));
            newUser.setPhoto(oAuth2User.getPicture());
            newUser.setRole(Role.USER);
            newUser.setStatus(AccountStatus.ACTIVE);
            newUser.setUsername(email.substring(0, email.indexOf("@")));
            newUser.setEmail(email);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            newUser.setWallets(new HashSet<>());
            newUser.setCards(new HashSet<>());
            save(newUser);
            return newUser;
        }
    }

    @Override
    public String findByUsernameOrEmailOrPhoneNumber(String usernameOrEmailOrPhoneNumber) {
        return userRepository.findUsernameByUsernameOrEmailOrPhoneNumber(usernameOrEmailOrPhoneNumber)
                .orElseThrow(() -> new EntityNotFoundException(USER));
    }

    @Override
    public User getUserByID(UUID uuid) {
        return userRepository.getUserById(uuid, AccountStatus.DELETED).orElseThrow(() -> new EntityNotFoundException(USER));
    }

    @Override
    public User findUserByUsernameOrEmailOrPhoneNumber(String usernameOrEmailOrPhoneNumber) {
        return userRepository.findUserByUsernameOrEmailOrPhoneNumber(usernameOrEmailOrPhoneNumber)
                .orElseThrow(() -> new EntityNotFoundException(USER));
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(format(USER_NOT_FOUND_MESSAGE, username)));
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
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(USER, id));
        if (user.getRole().equals(Role.ADMIN)) {
            throw new InvalidUserInputException(USER_ALREADY_ADMIN);
        }
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    @Override
    public void demoteToUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(USER, id));
        if (user.getRole().equals(Role.USER)) {
            throw new InvalidUserInputException(USER_ALREADY_USER);
        }
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    @Override
    public void blockUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(USER, id));
        if (user.getStatus().equals(AccountStatus.BLOCKED)
                || user.getStatus().equals(AccountStatus.BLOCKED_AND_DELETED)) {
            throw new InvalidUserInputException(USER_ALREADY_BLOCKED);
        } else if (user.getStatus().equals(AccountStatus.ACTIVE)) {
            user.setStatus(AccountStatus.BLOCKED);
        } else if (user.getStatus().equals(AccountStatus.DELETED)) {
            user.setStatus(AccountStatus.BLOCKED_AND_DELETED);
        } else if (user.getStatus().equals(AccountStatus.PENDING)) {
            throw new InvalidUserInputException(PENDING_USERS_CAN_T_BE_BLOCKED);
        }
        userRepository.save(user);
    }

    @Override
    public void unblockUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(USER, id));
        if (user.getStatus().equals(AccountStatus.ACTIVE) || user.getStatus().equals(AccountStatus.DELETED)) {
            throw new InvalidUserInputException(USER_NOT_BLOCKED);
        } else if (user.getStatus().equals(AccountStatus.BLOCKED)) {
            user.setStatus(AccountStatus.ACTIVE);
        } else if (user.getStatus().equals(AccountStatus.BLOCKED_AND_DELETED)) {
            user.setStatus(AccountStatus.DELETED);
        } else if (user.getStatus().equals(AccountStatus.PENDING)) {
            throw new InvalidUserInputException(PENDING_USERS_CAN_T_BE_BLOCKED);
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
    public void changePasswordOfAuthenticatedUser(PasswordUpdateInput input) {

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
            userRepository.save(user);
        } else {
            throw new InvalidUserInputException(NO_CHANGES_WERE_MADE);
        }
    }

    @Override
    public void updateAuthenticatedUser(ProfileUpdateInput input, MultipartFile profileImage,
                                        boolean removePicture) {
        User user = getAuthenticatedUser();

        if (input.getFirstName() != null) {
            user.setFirstName(input.getFirstName());
        }
        if (input.getLastName() != null) {
            user.setLastName(input.getLastName());
        }
        if (input.getEmail() != null && !user.getEmail().equals(input.getEmail())) {
            if (checkIfEmailIsTaken(input.getEmail())) {
                throw new DuplicateEntityException(format(EMAIL_TAKEN, input.getEmail()));
            }
            user.setEmail(input.getEmail());
        }
        if (input.getPhoneNumber() != null && !user.getPhoneNumber().equals(input.getPhoneNumber())) {
            if (checkIfPhoneNumberIsTaken(input.getPhoneNumber())) {
                throw new DuplicateEntityException(format(PHONE_NUMBER_TAKEN, input.getPhoneNumber()));
            }
            user.setPhoneNumber(input.getPhoneNumber());
        }
        if (removePicture)
        {
            user.setPhoto(DEFAULT_PROFILE_PIC_PNG);
        }
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                if (!isValidImageFile(profileImage)) {
                    throw new InvalidFileException(INVALID_IMAGE);
                }
                if (profileImage.getSize() > MAX_FILE_SIZE) {
                    throw new InvalidFileSizeException(FILE_TOO_LARGE);
                }
                String imageUrl = cloudinaryHelper.uploadUserProfilePhoto(profileImage);
                user.setPhoto(imageUrl);
            } catch (IOException | InvalidUserInputException e) {
                throw new InvalidFileException(INVALID_IMAGE);
            }
        }
        userRepository.save(user);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public UserProfileOutput getAuthenticatedUserProfile() {
        User user = getAuthenticatedUser();
        return ModelMapper.userProfileFromUser(user);
    }

    @Override
    public UserProfileOutput getUserProfileById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(USER, id));
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

        output.setTotalElements(pageResult.getTotalElements());
        output.setCurrentPage(filterOptions.getPage());
        output.setTotalPages(pageResult.getTotalPages());
        output.setPageSize(filterOptions.getSize());
        output.setHasNextPage(pageResult.hasNext());
        output.setHasPreviousPage(pageResult.hasPrevious());
        output.setContent(pageResult
                .stream()
                .map(ModelMapper::userOutputFromUser)
                .toList());
        return output;
    }

}