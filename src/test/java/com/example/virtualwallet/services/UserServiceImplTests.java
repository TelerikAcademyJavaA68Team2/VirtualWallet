package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.*;
import com.example.virtualwallet.helpers.CloudinaryHelper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.pageable.UserPageOutput;
import com.example.virtualwallet.models.dtos.user.PasswordUpdateInput;
import com.example.virtualwallet.models.dtos.user.ProfileUpdateInput;
import com.example.virtualwallet.models.dtos.user.UserProfileOutput;
import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import com.example.virtualwallet.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.virtualwallet.Helpers.*;
import static com.example.virtualwallet.services.UserServiceImpl.*;
import static com.example.virtualwallet.services.UserServiceImpl.DEFAULT_PROFILE_PIC_PNG;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {

    public static final String FAILED = "something failed";
    public static final String TEST_USER = "testUser";
    public static final String MAIL = "test@email.com";
    public static final String MOCK_USER = "mockUser";
    public static final String PHONE_NUMBER = "123456789";
    public static final String EMAIL_OR_PHONE_NUMBER = "mock@user.com";
    public static final String PASSWORD = "ValidPass123!";
    public static final String DIFFERENT_PASS = "DifferentPass456!";
    public static final String NEW_PASSWORD = "NewPass456!";
    public static final String NEW_PASSWORD_CONFIRM = "NewPass123!";
    public static final String ACTUAL_PASSWORD = "ActualPassword123!";
    public static final String WRONG_PASSWORD = "WrongPassword";
    public static final String OLD_PASS = "OldPass123!";
    public static final String MOCK_PHONE_NUMBER = "1234567890";
    public static final String VALID_PHONE_NUMBER = "0987654321";
    public static final String TAKEN_EMAIL = "taken@example.com";
    public static final String NEW_EMAIL = "old@example.com";
    public static final String NEW_EMAIL_2 = "new@example.com";
    public static final String LAST_NAME = "Doe";
    public static final String FIRST_NAME = "John";
    public static final String USERNAME = "john";
    public static final String VALID_CONTENT_TYPE = "image/jpeg";
    public static final String INVALID_CONTENT_TYPE = "application/pdf";
    public static final String VALID_IMAGE_URL = "https://image.com/photo.jpg";
    public static final String CLOUDINARY_IMAGE_URL = "https://cloudinary/image.jpg";
    public static final String SORT_BY_USERNAME = "username";
    public static final String ASC = "asc";
    public static final String UPLOAD_FAILED = "upload failed";
    public static final String NONEXISTENT_USER = "nonexistent_user";

    @Mock
    private UserRepository userRepository;

    @Mock
    private CloudinaryHelper cloudinaryHelper;

    @Spy
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private User mockUser;

    @Mock
    private UUID userId;

    @BeforeEach
    void setup(){
        mockUser = createMockUserWithoutCardsAndWallets();
        userId = UUID.randomUUID();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUserById_ShouldReturnUser_When_UserExists() {

        when(userRepository.getUserById(mockUser.getId(), AccountStatus.DELETED)).thenReturn(Optional.of(mockUser));

        User result = userService.getUserByID(mockUser.getId());

        Assertions.assertNotNull(result);
        assertEquals(mockUser, result);
        verify(userRepository, Mockito.times(1)).getUserById(mockUser.getId(), AccountStatus.DELETED);
    }

    @Test
    void getUserById_ShouldThrowEntityNotFoundExc_When_UserDoesntExist() {

        when(userRepository.getUserById(userId, AccountStatus.DELETED)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserByID(userId));
        verify(userRepository, Mockito.times(1)).getUserById(userId, AccountStatus.DELETED);
    }

    @Test
    void loadUserByUsername_ShouldReturnUser_When_UserExists() {

        when(userRepository.findUserByUsername(mockUser.getUsername())).thenReturn(Optional.of(mockUser));

        User result = userService.loadUserByUsername(mockUser.getUsername());

        Assertions.assertNotNull(result);
        assertEquals(mockUser, result);
        verify(userRepository, Mockito.times(1)).findUserByUsername(mockUser.getUsername());
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundExc_When_UserDoesntExist() {
        String username = NONEXISTENT_USER;
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(username));
        assertEquals(String.format("User with username %s not found!", username), exception.getMessage());
        verify(userRepository, Mockito.times(1)).findUserByUsername(username);
    }

    @Test
    void findByUsernameOrEmailOrPhoneNumber_UserFound_ReturnsUsername() {
        when(userRepository.findUsernameByUsernameOrEmailOrPhoneNumber(EMAIL_OR_PHONE_NUMBER))
                .thenReturn(Optional.of(MOCK_USER_USERNAME));

        String result = userService.findByUsernameOrEmailOrPhoneNumber(EMAIL_OR_PHONE_NUMBER);

        assertEquals(MOCK_USER_USERNAME, result);
    }

    @Test
    void findByUsernameOrEmailOrPhoneNumber_UserNotFound_ThrowsException() {
        when(userRepository.findUsernameByUsernameOrEmailOrPhoneNumber(EMAIL_OR_PHONE_NUMBER))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.findByUsernameOrEmailOrPhoneNumber(EMAIL_OR_PHONE_NUMBER));
    }

    @Test
    void findUserByUsernameOrEmailOrPhoneNumber_UserFound_ReturnsUser() {
        when(userRepository.findUserByUsernameOrEmailOrPhoneNumber(EMAIL_OR_PHONE_NUMBER))
                .thenReturn(Optional.of(mockUser));

        User result = userService.findUserByUsernameOrEmailOrPhoneNumber(EMAIL_OR_PHONE_NUMBER);

        assertEquals(mockUser, result);
    }

    @Test
    void findUserByUsernameOrEmailOrPhoneNumber_UserNotFound_ThrowsException() {
        when(userRepository.findUserByUsernameOrEmailOrPhoneNumber(EMAIL_OR_PHONE_NUMBER))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.findUserByUsernameOrEmailOrPhoneNumber(EMAIL_OR_PHONE_NUMBER));
    }

    @Test
    void checkIfPhoneNumberIsTaken_WhenTaken_ReturnsTrue() {
        when(userRepository.checkIfPhoneNumberIsTaken(PHONE_NUMBER)).thenReturn(true);
        assertTrue(userService.checkIfPhoneNumberIsTaken(PHONE_NUMBER));
    }

    @Test
    void checkIfUsernameIsTaken_WhenNotTaken_ReturnsFalse() {
        when(userRepository.checkIfUsernameIsTaken(MOCK_USER)).thenReturn(false);
        assertFalse(userService.checkIfUsernameIsTaken(MOCK_USER));
    }

    @Test
    void checkIfEmailIsTaken_WhenTaken_ReturnsTrue() {
        when(userRepository.checkIfEmailIsTaken(MAIL)).thenReturn(true);
        assertTrue(userService.checkIfEmailIsTaken(MAIL));
    }

    @Test
    void promoteToAdmin_WhenUserIsNotAdmin_UpdatesRoleToAdmin() {
        mockUser.setRole(Role.USER);
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        userService.promoteToAdmin(mockUser.getId());

        assertEquals(Role.ADMIN, mockUser.getRole());
        verify(userRepository).save(mockUser);
    }

    @Test
    void promoteToAdmin_WhenAlreadyAdmin_ThrowsException() {
        mockUser.setRole(Role.ADMIN);
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        assertThrows(InvalidUserInputException.class, () -> userService.promoteToAdmin(mockUser.getId()));
    }

    @Test
    void demoteToUser_WhenUserIsAdmin_UpdatesRoleToUser() {
        mockUser.setRole(Role.ADMIN);
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        userService.demoteToUser(mockUser.getId());

        assertEquals(Role.USER, mockUser.getRole());
        verify(userRepository).save(mockUser);
    }

    @Test
    void demoteToUser_WhenAlreadyUser_ThrowsException() {
        mockUser.setRole(Role.USER);
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        assertThrows(InvalidUserInputException.class, () -> userService.demoteToUser(mockUser.getId()));
    }

    @Test
    void createUser_Should_CreateUserWhenValidParameters(){
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        userService.createUser(mockUser);

        verify(userRepository).save(mockUser);
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void shouldBlockActiveUser() {
        User user = new User();
        user.setId(userId);
        user.setStatus(AccountStatus.ACTIVE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.blockUser(userId);

        assertEquals(AccountStatus.BLOCKED, user.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    void shouldBlockDeletedUserAsBlockedAndDeleted() {
        User user = new User();
        user.setId(userId);
        user.setStatus(AccountStatus.DELETED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.blockUser(userId);

        assertEquals(AccountStatus.BLOCKED_AND_DELETED, user.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowIfUserIsAlreadyBlocked() {
        User user = new User();
        user.setId(userId);
        user.setStatus(AccountStatus.BLOCKED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(InvalidUserInputException.class, () -> userService.blockUser(userId));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowIfUserIsBlockedAndDeleted() {
        User user = new User();
        user.setId(userId);
        user.setStatus(AccountStatus.BLOCKED_AND_DELETED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(InvalidUserInputException.class, () -> userService.blockUser(userId));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowIfUserIsPending() {
        User user = new User();
        user.setId(userId);
        user.setStatus(AccountStatus.PENDING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(InvalidUserInputException.class, () -> userService.blockUser(userId));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowIfUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.blockUser(userId));
    }


    @Test
    void shouldUnblockBlockedUser() {
        User user = new User();
        user.setId(userId);
        user.setStatus(AccountStatus.BLOCKED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.unblockUser(userId);

        assertEquals(AccountStatus.ACTIVE, user.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    void shouldUnblockBlockedAndDeletedUserToDeleted() {
        User user = new User();
        user.setId(userId);
        user.setStatus(AccountStatus.BLOCKED_AND_DELETED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.unblockUser(userId);

        assertEquals(AccountStatus.DELETED, user.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowIfUserIsAlreadyActive() {
        User user = new User();
        user.setId(userId);
        user.setStatus(AccountStatus.ACTIVE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(InvalidUserInputException.class, () -> userService.unblockUser(userId));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowIfUserIsAlreadyDeleted() {
        User user = new User();
        user.setId(userId);
        user.setStatus(AccountStatus.DELETED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(InvalidUserInputException.class, () -> userService.unblockUser(userId));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowIfUserNotFoundUnblock() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.unblockUser(userId));
    }

    @Test
    void shouldThrowWhenUnblockingPendingUser() {
        User user = new User();
        user.setId(userId);
        user.setStatus(AccountStatus.PENDING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class,
                () -> userService.unblockUser(userId));


        assertEquals(PENDING_USERS_CAN_T_BE_BLOCKED, ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldReturnAuthenticatedUser() {

        UserDetails mockPrincipal = mock(UserDetails.class);
        when(mockPrincipal.getUsername()).thenReturn(TEST_USER);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockPrincipal);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);


        SecurityContextHolder.setContext(securityContext);


        UserServiceImpl spyService = Mockito.spy(userService);
        User expectedUser = new User();
        expectedUser.setUsername(TEST_USER);
        doReturn(expectedUser).when(spyService).loadUserByUsername(TEST_USER);

        // Act
        User result = spyService.getAuthenticatedUser();

        // Assert
        assertEquals(TEST_USER, result.getUsername());
    }


    @Test
    void shouldThrowWhenPrincipalIsNull() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(null);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        UnauthorizedAccessException ex = assertThrows(UnauthorizedAccessException.class,
                () -> userService.getAuthenticatedUser());

        assertEquals(LOG_IN_FIRST, ex.getMessage());

    }


    @Test
    void shouldThrowUnauthorizedWhenUnexpectedError() {

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenThrow(new RuntimeException(FAILED));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);


        SecurityContextHolder.setContext(securityContext);


        UnauthorizedAccessException ex = assertThrows(UnauthorizedAccessException.class,
                () -> userService.getAuthenticatedUser());

        assertEquals(FAILED, ex.getMessage());

    }

    @Test
    void shouldChangePasswordSuccessfully() {
        PasswordUpdateInput input = new PasswordUpdateInput();
        input.setPassword(OLD_PASS);
        input.setNewPassword(NEW_PASSWORD);
        input.setNewPasswordConfirm(NEW_PASSWORD);


        mockUser.setPassword(passwordEncoder.encode(OLD_PASS));

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();

        spyService.changePasswordOfAuthenticatedUser(input);

        verify(userRepository).save(mockUser);
        assertTrue(passwordEncoder.matches(NEW_PASSWORD, mockUser.getPassword()));
    }


    @Test
    void shouldSaveUser() {
        userService.save(mockUser);
        verify(userRepository).save(mockUser);
    }

    @Test
    void shouldThrowIfCurrentPasswordIsWrong() {
        PasswordUpdateInput input = new PasswordUpdateInput();
        input.setPassword(WRONG_PASSWORD);
        input.setNewPassword(NEW_PASSWORD_CONFIRM);
        input.setNewPasswordConfirm(NEW_PASSWORD_CONFIRM);

        mockUser.setPassword(passwordEncoder.encode(ACTUAL_PASSWORD));

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();

        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class,
                () -> spyService.changePasswordOfAuthenticatedUser(input));

        assertEquals(UserServiceImpl.WRONG_PASSWORD, ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowIfNewPasswordIsNull() {
        PasswordUpdateInput input = new PasswordUpdateInput();
        input.setPassword(PASSWORD);
        input.setNewPassword(null);

        mockUser.setPassword(passwordEncoder.encode(PASSWORD));

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();

        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class,
                () -> spyService.changePasswordOfAuthenticatedUser(input));

        assertEquals(NO_CHANGES_WERE_MADE, ex.getMessage());
        verify(userRepository, never()).save(any());
    }


    @Test
    void shouldThrowIfPasswordConfirmIsNull() {
        PasswordUpdateInput input = new PasswordUpdateInput();
        input.setPassword(PASSWORD);
        input.setNewPassword(NEW_PASSWORD);
        input.setNewPasswordConfirm(null);

        mockUser.setPassword(passwordEncoder.encode(PASSWORD));

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();

        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class,
                () -> spyService.changePasswordOfAuthenticatedUser(input));

        assertEquals(PASSWORD_CONFIRM, ex.getMessage());
    }

    @Test
    void shouldThrowIfNewPasswordsDoNotMatch() {
        PasswordUpdateInput input = new PasswordUpdateInput();
        input.setPassword(PASSWORD);
        input.setNewPassword(NEW_PASSWORD);
        input.setNewPasswordConfirm(DIFFERENT_PASS);

        mockUser.setPassword(passwordEncoder.encode(PASSWORD));

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();

        InvalidUserInputException ex = assertThrows(InvalidUserInputException.class,
                () -> spyService.changePasswordOfAuthenticatedUser(input));

        assertEquals(PASSWORD_DONT_MATCH, ex.getMessage());
    }

    @Test
    void shouldUpdateFirstAndLastName() {
        ProfileUpdateInput input = new ProfileUpdateInput();
        input.setFirstName(FIRST_NAME);
        input.setLastName(LAST_NAME);

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();

        spyService.updateAuthenticatedUser(input, null, false);

        assertEquals(FIRST_NAME, mockUser.getFirstName());
        assertEquals(LAST_NAME, mockUser.getLastName());
        verify(userRepository).save(mockUser);
    }

    @Test
    void shouldUpdateEmailIfNotTaken() {
        ProfileUpdateInput input = new ProfileUpdateInput();
        input.setEmail(NEW_EMAIL_2);

        mockUser.setEmail(NEW_EMAIL);

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();
        doReturn(false).when(spyService).checkIfEmailIsTaken(NEW_EMAIL_2);

        spyService.updateAuthenticatedUser(input, null, false);

        assertEquals(NEW_EMAIL_2, mockUser.getEmail());
        verify(userRepository).save(mockUser);
    }

    @Test
    void shouldThrowIfEmailIsTaken() {
        ProfileUpdateInput input = new ProfileUpdateInput();
        input.setEmail(TAKEN_EMAIL);

        mockUser.setEmail(NEW_EMAIL);

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();
        doReturn(true).when(spyService).checkIfEmailIsTaken(TAKEN_EMAIL);

        DuplicateEntityException ex = assertThrows(DuplicateEntityException.class,
                () -> spyService.updateAuthenticatedUser(input, null, false));

        assertTrue(ex.getMessage().contains(TAKEN_EMAIL));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldUpdatePhoneIfNotTaken() {
        ProfileUpdateInput input = new ProfileUpdateInput();
        input.setPhoneNumber(MOCK_PHONE_NUMBER);

        mockUser.setPhoneNumber(VALID_PHONE_NUMBER);

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();
        doReturn(false).when(spyService).checkIfPhoneNumberIsTaken(MOCK_PHONE_NUMBER);

        spyService.updateAuthenticatedUser(input, null, false);

        assertEquals(MOCK_PHONE_NUMBER, mockUser.getPhoneNumber());
        verify(userRepository).save(mockUser);
    }

    @Test
    void shouldThrowIfPhoneIsTaken() {
        ProfileUpdateInput input = new ProfileUpdateInput();
        input.setPhoneNumber(MOCK_PHONE_NUMBER);

        mockUser.setPhoneNumber(VALID_PHONE_NUMBER);

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();
        doReturn(true).when(spyService).checkIfPhoneNumberIsTaken(MOCK_PHONE_NUMBER);

        DuplicateEntityException ex = assertThrows(DuplicateEntityException.class,
                () -> spyService.updateAuthenticatedUser(input, null, false));

        assertTrue(ex.getMessage().contains(MOCK_PHONE_NUMBER));
    }

    @Test
    void shouldRemoveProfilePicture() {

        mockUser.setPhoto(CLOUDINARY_IMAGE_URL);

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();

        spyService.updateAuthenticatedUser(new ProfileUpdateInput(), null, true);

        assertEquals(DEFAULT_PROFILE_PIC_PNG, mockUser.getPhoto());
        verify(userRepository).save(mockUser);
    }

    @Test
    void shouldUploadAndSetNewProfileImage() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn(VALID_CONTENT_TYPE);

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();
        when(cloudinaryHelper.uploadUserProfilePhoto(file)).thenReturn(VALID_IMAGE_URL);

        spyService.updateAuthenticatedUser(new ProfileUpdateInput(), file, false);

        assertEquals(VALID_IMAGE_URL, mockUser.getPhoto());
    }

    @Test
    void shouldThrowIfImageFileIsInvalid() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn(INVALID_CONTENT_TYPE);

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();

        assertThrows(InvalidFileException.class,
                () -> spyService.updateAuthenticatedUser(new ProfileUpdateInput(), file, false));
    }

    @Test
    void shouldThrowIfImageTooLarge() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn(VALID_CONTENT_TYPE);
        when(file.getSize()).thenReturn(UserServiceImpl.MAX_FILE_SIZE + 1);

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();


        assertThrows(InvalidFileSizeException.class,
                () -> spyService.updateAuthenticatedUser(new ProfileUpdateInput(), file, false));
    }

    @Test
    void shouldReturnMappedProfileOfAuthenticatedUser() {

        mockUser.setUsername(USERNAME);

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();

        UserProfileOutput output = spyService.getAuthenticatedUserProfile();

        assertEquals(USERNAME, output.getUsername());
    }

    @Test
    void shouldReturnUserProfileById() {

        mockUser.setUsername(USERNAME);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        UserProfileOutput output = userService.getUserProfileById(userId);

        assertEquals(USERNAME, output.getUsername());
    }

    @Test
    void shouldThrowWhenUserProfileByIdNotFound() {

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserProfileById(userId));
    }

    @Test
    void shouldSoftDeleteAuthenticatedUser() {
        mockUser = Mockito.mock(User.class);

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(mockUser).when(spyService).getAuthenticatedUser();

        spyService.softDeleteAuthenticatedUser();

        verify(mockUser).markAsDeleted();
        verify(userRepository).save(mockUser);
    }

    @Test
    void shouldReturnFilteredUserPage() {
        UserFilterOptions filterOptions = new UserFilterOptions();
        filterOptions.setPage(0);
        filterOptions.setSize(2);
        filterOptions.setSortBy(SORT_BY_USERNAME);
        filterOptions.setSortOrder(ASC);

        User user2 = new User();
        user2.setUsername(USERNAME);

        List<User> userList = List.of(mockUser, user2);
        Page<User> pageResult = new PageImpl<>(userList);

        when(userRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(pageResult);

        UserPageOutput output = userService.filterUsers(filterOptions);

        assertEquals(2, output.getContent().size());
        assertEquals(mockUser.getUsername(), output.getContent().get(0).getUsername());
        assertEquals(USERNAME, output.getContent().get(1).getUsername());
        assertEquals(0, output.getCurrentPage());
        assertEquals(2, output.getPageSize());
    }

    @Test
    void shouldThrowInvalidFileExceptionWhenCloudinaryUploadFails() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn(VALID_CONTENT_TYPE);

        User user = new User();

        UserServiceImpl spyService = Mockito.spy(userService);
        doReturn(user).when(spyService).getAuthenticatedUser();


        when(cloudinaryHelper.uploadUserProfilePhoto(file)).thenThrow(new IOException(UPLOAD_FAILED));

        assertThrows(InvalidFileException.class,
                () -> spyService.updateAuthenticatedUser(new ProfileUpdateInput(), file, false));
    }


}
