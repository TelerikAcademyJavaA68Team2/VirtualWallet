package com.example.virtualwallet.services.specifications;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.fillterOptions.TransactionFilterOptions;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionSpecificationTests {

    private CriteriaBuilder cb;
    private Root<Transaction> root;
    private CriteriaQuery<?> query;

    @BeforeEach
    void setup() {
        cb = mock(CriteriaBuilder.class);
        root = mock(Root.class);
        query = mock(CriteriaQuery.class);

        when(root.get("senderWallet")).thenReturn(mock());
        when(root.get("recipientWallet")).thenReturn(mock());
        when(root.get("senderWallet").get("owner")).thenReturn(mock());
        when(root.get("recipientWallet").get("owner")).thenReturn(mock());
        when(root.get("senderWallet").get("owner").get("username")).thenReturn(mock());
        when(root.get("recipientWallet").get("owner").get("username")).thenReturn(mock());
    }

    @Test
    void buildTransactionSpecification_WithNoFilters_ReturnsEmptyPredicate() {
        // Arrange
        TransactionFilterOptions filterOptions = new TransactionFilterOptions(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "date",
                "asc",
                0,
                10
        );

        // Act
        Specification<Transaction> specification = TransactionSpecification.buildTransactionSpecification(filterOptions);
        Predicate predicate = specification.toPredicate(root, query, cb);

        // Assert
        assertNull(predicate);
        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb, times(1)).and(captor.capture());
        Predicate[] capturedPredicates = captor.getValue();
        assertEquals(0, capturedPredicates.length);
    }

    @Test
    void buildTransactionSpecification_WithProfileUsernameFilter_GeneratesOrPredicate() {
        // Arrange
        TransactionFilterOptions filterOptions = new TransactionFilterOptions(
                "john.doe",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "date",
                "asc",
                0,
                10
        );

        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(mock(Predicate.class));

        // Act
        Specification<Transaction> specification = TransactionSpecification.buildTransactionSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).or(
                cb.equal(root.get("senderWallet").get("owner").get("username"), "john.doe"),
                cb.equal(root.get("recipientWallet").get("owner").get("username"), "john.doe")
        );
    }

    @Test
    void buildTransactionSpecification_WithDateFilters_GeneratesBetweenPredicates() {
        // Arrange
        LocalDateTime fromDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime toDate = LocalDateTime.of(2023, 12, 31, 23, 59);

        TransactionFilterOptions filterOptions = new TransactionFilterOptions(
                null,
                fromDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm")),
                toDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm")),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "date",
                "asc",
                0,
                10
        );

        when(root.get("date")).thenReturn(mock());
        when(cb.greaterThanOrEqualTo(root.get("date"), fromDate)).thenReturn(mock(Predicate.class));
        when(cb.lessThanOrEqualTo(root.get("date"), toDate)).thenReturn(mock(Predicate.class));

        // Act
        Specification<Transaction> specification = TransactionSpecification.buildTransactionSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).greaterThanOrEqualTo(root.get("date"), fromDate);
        verify(cb).lessThanOrEqualTo(root.get("date"), toDate);
    }

    @Test
    void buildTransactionSpecification_WithAmountRangeFilters_GeneratesBetweenPredicates() {
        // Arrange
        BigDecimal minAmount = new BigDecimal("100.00");
        BigDecimal maxAmount = new BigDecimal("500.00");

        TransactionFilterOptions filterOptions = new TransactionFilterOptions(
                null,
                null,
                null,
                minAmount,
                maxAmount,
                null,
                null,
                null,
                null,
                null,
                "date",
                "asc",
                0,
                10
        );

        when(root.get("amount")).thenReturn(mock());
        when(cb.greaterThanOrEqualTo(root.get("amount"), minAmount)).thenReturn(mock(Predicate.class));
        when(cb.lessThanOrEqualTo(root.get("amount"), maxAmount)).thenReturn(mock(Predicate.class));

        // Act
        Specification<Transaction> specification = TransactionSpecification.buildTransactionSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).greaterThanOrEqualTo(root.get("amount"), minAmount);
        verify(cb).lessThanOrEqualTo(root.get("amount"), maxAmount);
    }

    @Test
    void buildTransactionSpecification_WithCurrencyFilter_GeneratesEqualPredicate() {
        // Arrange
        String currency = "USD";

        TransactionFilterOptions filterOptions = new TransactionFilterOptions(
                null,
                null,
                null,
                null,
                null,
                currency,
                null,
                null,
                null,
                null,
                "date",
                "asc",
                0,
                10
        );

        when(root.get("currency")).thenReturn(mock());
        when(cb.equal(root.get("currency"), Currency.valueOf(currency))).thenReturn(mock(Predicate.class));

        // Act
        Specification<Transaction> specification = TransactionSpecification.buildTransactionSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).equal(root.get("currency"), Currency.valueOf(currency));
    }

    @Test
    void buildTransactionSpecification_WithDirectionFilter_GeneratesDirectionPredicate() {
        // Arrange
        String profileUsername = "john.doe";
        String direction = "incoming";

        TransactionFilterOptions filterOptions = new TransactionFilterOptions(
                profileUsername,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                direction,
                null,
                "date",
                "asc",
                0,
                10
        );

        when(root.get("recipientUsername")).thenReturn(mock());
        when(cb.equal(root.get("recipientUsername"), profileUsername)).thenReturn(mock(Predicate.class));

        // Act
        Specification<Transaction> specification = TransactionSpecification.buildTransactionSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).equal(root.get("recipientUsername"), profileUsername);
    }

    @Test
    void buildTransactionSpecification_WithSenderAndRecipientFilters_GeneratesLikePredicates() {
        // Arrange
        String sender = "alice";
        String recipient = "bob";

        TransactionFilterOptions filterOptions = new TransactionFilterOptions(
                null,
                null,
                null,
                null,
                null,
                null,
                sender,
                recipient,
                null,
                null,
                "date",
                "asc",
                0,
                10
        );

        when(root.get("senderUsername")).thenReturn(mock());
        when(root.get("recipientUsername")).thenReturn(mock());
        when(cb.like(root.get("senderUsername"), "%" + sender + "%")).thenReturn(mock(Predicate.class));
        when(cb.like(root.get("recipientUsername"), "%" + recipient + "%")).thenReturn(mock(Predicate.class));

        // Act
        Specification<Transaction> specification = TransactionSpecification.buildTransactionSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).like(root.get("senderUsername"), "%" + sender + "%");
        verify(cb).like(root.get("recipientUsername"), "%" + recipient + "%");
    }

    @Test
    void buildTransactionSpecification_WithDescriptionFilter_GeneratesLikePredicate() {
        // Arrange
        String description = "rent";

        TransactionFilterOptions filterOptions = new TransactionFilterOptions(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                description,
                "date",
                "asc",
                0,
                10
        );

        when(root.get("description")).thenReturn(mock());
        when(cb.like(root.get("description"), "%" + description + "%")).thenReturn(mock(Predicate.class));

        // Act
        Specification<Transaction> specification = TransactionSpecification.buildTransactionSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).like(root.get("description"), "%" + description + "%");
    }

    @Test
    void buildTransactionSpecification_WithProfileUsernameAndOutgoingDirectionFilters_GeneratesDirectionalPredicate() {
        // Arrange
        String profileUsername = "john.doe";
        String direction = "outgoing";
        TransactionFilterOptions filterOptions = new TransactionFilterOptions(
                profileUsername,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                direction,
                null,
                "date",
                "asc",
                0,
                10
        );

        when(root.get("senderUsername")).thenReturn(mock());
        when(cb.equal(root.get("senderUsername"), profileUsername)).thenReturn(mock(Predicate.class));

        // Act
        Specification<Transaction> specification = TransactionSpecification.buildTransactionSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).equal(root.get("senderUsername"), profileUsername);
    }
}