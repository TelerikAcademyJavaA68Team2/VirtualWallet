package com.example.virtualwallet;

import com.example.virtualwallet.models.Dtos.UserOutput;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.MainCurrency;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.UUID;

public class Helpers {

    public static final String MOCK_USER_FIRST_NAME = "MockFirstNameUser";
    public static final String MOCK_ADMIN_FIRST_NAME = "MockFirstNameAdmin";
    public static final String MOCK_USER_LAST_NAME = "MockLastNameUser";
    public static final String MOCK_ADMIN_LAST_NAME = "MockLastNameAdmin";
    public static final String MOCK_USER_USERNAME = "MockUsername";
    public static final String MOCK_ADMIN_USERNAME = "MockAdminName";
    public static final String MOCK_PASSWORD = "MockPassword";
    public static final String MOCK_PHONE_NUMBER = "1231231234";
    public static final String MOCK_USER_EMAIL = "mock@user.com";
    public static final String MOCK_ADMIN_EMAIL = "mock@admin.com";
    public static final String MOCK_ACCOUNT_STATUS = "ACTIVE";
    public static final String MOCK_USER_ORDER_BY = "username";


    public static User createMockUser() {
        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setFirstName(MOCK_USER_FIRST_NAME);
        mockUser.setLastName(MOCK_USER_LAST_NAME);
        mockUser.setEmail(MOCK_USER_EMAIL);
        mockUser.setUsername(MOCK_USER_USERNAME);
        mockUser.setPassword(MOCK_PASSWORD);
        mockUser.setRole(Role.USER);
        mockUser.setMainCurrency(MainCurrency.BGN);
        return mockUser;
    }

    public static UserOutput createMockUserOutput() {
        return new UserOutput(MOCK_USER_USERNAME,
                MOCK_USER_EMAIL,
                MOCK_PHONE_NUMBER,
                Role.USER.name(),
                true,
                1L
        );
    }

    public static User createMockAdmin() {
        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setFirstName(MOCK_ADMIN_FIRST_NAME);
        mockUser.setLastName(MOCK_ADMIN_LAST_NAME);
        mockUser.setUsername(MOCK_ADMIN_USERNAME);
        mockUser.setPassword(MOCK_PASSWORD);
        mockUser.setEmail(MOCK_ADMIN_EMAIL);
        mockUser.setPhoneNumber(MOCK_PHONE_NUMBER);
        mockUser.setRole(Role.ADMIN);
        mockUser.setMainCurrency(MainCurrency.BGN);
        return mockUser;
    }

    public static UserFilterOptions createMockUserFilterOptions() {
        return new UserFilterOptions(
                MOCK_PHONE_NUMBER,
                MOCK_USER_USERNAME,
                MOCK_USER_EMAIL,
                Role.USER.name(),
                MOCK_ACCOUNT_STATUS,
                0,
                10,
                MOCK_USER_ORDER_BY
        );
    }

    public static Pageable createMockPageable() {
        return PageRequest.of(0, 20, Sort.unsorted());
    }

    public static Page<Object[]> createMockPage() {
        return new PageImpl<>(Collections.singletonList(new Object[]{
                MOCK_USER_USERNAME,
                MOCK_USER_EMAIL,
                MOCK_PHONE_NUMBER,
                Role.USER.name(),
                true,
                5L
        }));
    }
}