package com.example.virtualwallet.services.specifications;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.AccountStatus;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserSpecificationTests {
    private CriteriaBuilder cb;
    private Root<User> root;
    private CriteriaQuery<?> query;

    @BeforeEach
    void setup() {
        cb = mock(CriteriaBuilder.class);
        root = mock(Root.class);
        query = mock(CriteriaQuery.class);

        when(root.get("username")).thenReturn(mock());
        when(root.get("email")).thenReturn(mock());
        when(root.get("phoneNumber")).thenReturn(mock());
        when(root.get("role")).thenReturn(mock());
        when(root.get("status")).thenReturn(mock());
        when(root.get("createdAt")).thenReturn(mock());
    }

    @Test
    void buildUserSpecification_WithNoFilters_ReturnsEmptyPredicate() {
        // Arrange
        UserFilterOptions filterOptions = new UserFilterOptions(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "createdAt",
                "asc",
                0,
                10
        );

        // Act
        Specification<User> specification = UserSpecification.buildUserSpecification(filterOptions);
        Predicate predicate = specification.toPredicate(root, query, cb);

        // Assert
        assertNull(predicate);
        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb, times(1)).and(captor.capture());
        Predicate[] capturedPredicates = captor.getValue();
        assertEquals(0, capturedPredicates.length);
    }

    @Test
    void buildUserSpecification_WithUsernameFilter_GeneratesLikePredicate() {
        // Arrange
        String username = "john.doe";
        UserFilterOptions filterOptions = new UserFilterOptions(
                username,
                null,
                null,
                null,
                null,
                null,
                null,
                "createdAt",
                "asc",
                0,
                10
        );

        when(cb.like(root.get("username"), "%" + username + "%")).thenReturn(mock(Predicate.class));

        // Act
        Specification<User> specification = UserSpecification.buildUserSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).like(root.get("username"), "%" + username + "%");
    }

    @Test
    void buildUserSpecification_WithEmailFilter_GeneratesLikePredicate() {
        // Arrange
        String email = "john.doe@example.com";
        UserFilterOptions filterOptions = new UserFilterOptions(
                null,
                email,
                null,
                null,
                null,
                null,
                null,
                "createdAt",
                "asc",
                0,
                10
        );

        when(cb.like(root.get("email"), "%" + email + "%")).thenReturn(mock(Predicate.class));

        // Act
        Specification<User> specification = UserSpecification.buildUserSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).like(root.get("email"), "%" + email + "%");
    }

    @Test
    void buildUserSpecification_WithPhoneNumberFilter_GeneratesLikePredicate() {
        // Arrange
        String phoneNumber = "1234567890";
        UserFilterOptions filterOptions = new UserFilterOptions(
                null,
                null,
                phoneNumber,
                null,
                null,
                null,
                null,
                "createdAt",
                "asc",
                0,
                10
        );

        when(cb.like(root.get("phoneNumber"), "%" + phoneNumber + "%")).thenReturn(mock(Predicate.class));

        // Act
        Specification<User> specification = UserSpecification.buildUserSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).like(root.get("phoneNumber"), "%" + phoneNumber + "%");
    }

    @Test
    void buildUserSpecification_WithRoleFilter_GeneratesEqualPredicate() {
        // Arrange
        String role = "ADMIN";
        UserFilterOptions filterOptions = new UserFilterOptions(
                null,
                null,
                null,
                role,
                null,
                null,
                null,
                "createdAt",
                "asc",
                0,
                10
        );

        when(cb.equal(root.get("role"), Role.valueOf(role))).thenReturn(mock(Predicate.class));

        // Act
        Specification<User> specification = UserSpecification.buildUserSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).equal(root.get("role"), Role.valueOf(role));
    }

    @Test
    void buildUserSpecification_WithBlockedStatusFilter_GeneratesOrPredicate() {
        // Arrange
        String status = "BLOCKED";
        UserFilterOptions filterOptions = new UserFilterOptions(
                null,
                null,
                null,
                null,
                status,
                null,
                null,
                "createdAt",
                "asc",
                0,
                10
        );

        Predicate blockedPredicate = mock(Predicate.class);
        Predicate blockedAndDeletedPredicate = mock(Predicate.class);
        when(cb.equal(root.get("status"), AccountStatus.BLOCKED)).thenReturn(blockedPredicate);
        when(cb.equal(root.get("status"), AccountStatus.BLOCKED_AND_DELETED)).thenReturn(blockedAndDeletedPredicate);

        // Mock the behavior of cb.or
        Predicate orPredicate = mock(Predicate.class);
        when(cb.or(blockedPredicate, blockedAndDeletedPredicate)).thenReturn(orPredicate);

        // Act
        Specification<User> specification = UserSpecification.buildUserSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).equal(root.get("status"), AccountStatus.BLOCKED);
        verify(cb).equal(root.get("status"), AccountStatus.BLOCKED_AND_DELETED);
        verify(cb).or(blockedPredicate, blockedAndDeletedPredicate);
    }

    @Test
    void buildUserSpecification_WithDeletedStatusFilter_GeneratesOrPredicate() {
        // Arrange
        String status = "DELETED";
        UserFilterOptions filterOptions = new UserFilterOptions(
                null,
                null,
                null,
                null,
                status,
                null,
                null,
                "createdAt",
                "asc",
                0,
                10
        );

        Predicate deletedPredicate = mock(Predicate.class);
        Predicate blockedAndDeletedPredicate = mock(Predicate.class);
        when(cb.equal(root.get("status"), AccountStatus.DELETED)).thenReturn(deletedPredicate);
        when(cb.equal(root.get("status"), AccountStatus.BLOCKED_AND_DELETED)).thenReturn(blockedAndDeletedPredicate);

        Predicate orPredicate = mock(Predicate.class);
        when(cb.or(deletedPredicate, blockedAndDeletedPredicate)).thenReturn(orPredicate);

        // Act
        Specification<User> specification = UserSpecification.buildUserSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).equal(root.get("status"), AccountStatus.DELETED);
        verify(cb).equal(root.get("status"), AccountStatus.BLOCKED_AND_DELETED);

        verify(cb).or(deletedPredicate, blockedAndDeletedPredicate);
    }

    @Test
    void buildUserSpecification_WithDateFilters_GeneratesBetweenPredicates() {
        // Arrange
        LocalDateTime fromDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime toDate = LocalDateTime.of(2023, 12, 31, 23, 59);
        UserFilterOptions filterOptions = new UserFilterOptions(
                null,
                null,
                null,
                null,
                null,
                fromDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm")),
                toDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm")),
                "createdAt",
                "asc",
                0,
                10
        );

        when(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate)).thenReturn(mock(Predicate.class));
        when(cb.lessThanOrEqualTo(root.get("createdAt"), toDate)).thenReturn(mock(Predicate.class));

        // Act
        Specification<User> specification = UserSpecification.buildUserSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).greaterThanOrEqualTo(root.get("createdAt"), fromDate);
        verify(cb).lessThanOrEqualTo(root.get("createdAt"), toDate);
    }

    @Test
    void buildUserSpecification_WithOtherStatusFilter_GeneratesEqualPredicate() {
        // Arrange
        String status = "PENDING";
        UserFilterOptions filterOptions = new UserFilterOptions(
                null,
                null,
                null,
                null,
                status,
                null,
                null,
                "createdAt",
                "asc",
                0,
                10
        );

        Predicate pendingPredicate = mock(Predicate.class);
        when(cb.equal(root.get("status"), AccountStatus.PENDING)).thenReturn(pendingPredicate);

        // Act
        Specification<User> specification = UserSpecification.buildUserSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).equal(root.get("status"), AccountStatus.PENDING);
    }
}