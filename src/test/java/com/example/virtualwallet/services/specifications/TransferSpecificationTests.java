package com.example.virtualwallet.services.specifications;

import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.enums.TransferStatus;
import com.example.virtualwallet.models.fillterOptions.TransferFilterOptions;
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

class TransferSpecificationTests {
    private CriteriaBuilder cb;
    private Root<Transfer> root;
    private CriteriaQuery<?> query;

    @BeforeEach
    void setup() {
        cb = mock(CriteriaBuilder.class);
        root = mock(Root.class);
        query = mock(CriteriaQuery.class);

        when(root.get("recipientUsername")).thenReturn(mock());
        when(root.get("date")).thenReturn(mock());
        when(root.get("amount")).thenReturn(mock());
        when(root.get("currency")).thenReturn(mock());
        when(root.get("status")).thenReturn(mock());
    }

    @Test
    void buildTransferSpecification_WithNoFilters_ReturnsEmptyPredicate() {
        // Arrange
        TransferFilterOptions filterOptions = new TransferFilterOptions(
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
        Specification<Transfer> specification = TransferSpecification.buildTransferSpecification(filterOptions);
        Predicate predicate = specification.toPredicate(root, query, cb);

        // Assert
        assertNull(predicate);
        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb, times(1)).and(captor.capture());
        Predicate[] capturedPredicates = captor.getValue();
        assertEquals(0, capturedPredicates.length);
    }

    @Test
    void buildTransferSpecification_WithRecipientFilter_GeneratesLikePredicate() {
        // Arrange
        String recipient = "john.doe";
        TransferFilterOptions filterOptions = new TransferFilterOptions(
                recipient,
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

        when(cb.like(root.get("recipientUsername"), "%" + recipient + "%")).thenReturn(mock(Predicate.class));

        // Act
        Specification<Transfer> specification = TransferSpecification.buildTransferSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).like(root.get("recipientUsername"), "%" + recipient + "%");
    }

    @Test
    void buildTransferSpecification_WithDateFilters_GeneratesBetweenPredicates() {
        // Arrange
        LocalDateTime fromDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime toDate = LocalDateTime.of(2023, 12, 31, 23, 59);
        TransferFilterOptions filterOptions = new TransferFilterOptions(
                null,
                fromDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm")),
                toDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm")),
                null,
                null,
                null,
                null,
                "date",
                "asc",
                0,
                10
        );

        when(cb.greaterThanOrEqualTo(root.get("date"), fromDate)).thenReturn(mock(Predicate.class));
        when(cb.lessThanOrEqualTo(root.get("date"), toDate)).thenReturn(mock(Predicate.class));

        // Act
        Specification<Transfer> specification = TransferSpecification.buildTransferSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).greaterThanOrEqualTo(root.get("date"), fromDate);
        verify(cb).lessThanOrEqualTo(root.get("date"), toDate);
    }

    @Test
    void buildTransferSpecification_WithAmountRangeFilters_GeneratesBetweenPredicates() {
        // Arrange
        BigDecimal minAmount = new BigDecimal("100.00");
        BigDecimal maxAmount = new BigDecimal("500.00");
        TransferFilterOptions filterOptions = new TransferFilterOptions(
                null,
                null,
                null,
                minAmount,
                maxAmount,
                null,
                null,
                "date",
                "asc",
                0,
                10
        );

        when(cb.greaterThanOrEqualTo(root.get("amount"), minAmount)).thenReturn(mock(Predicate.class));
        when(cb.lessThanOrEqualTo(root.get("amount"), maxAmount)).thenReturn(mock(Predicate.class));

        // Act
        Specification<Transfer> specification = TransferSpecification.buildTransferSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).greaterThanOrEqualTo(root.get("amount"), minAmount);
        verify(cb).lessThanOrEqualTo(root.get("amount"), maxAmount);
    }

    @Test
    void buildTransferSpecification_WithCurrencyFilter_GeneratesEqualPredicate() {
        // Arrange
        String currency = "USD";
        TransferFilterOptions filterOptions = new TransferFilterOptions(
                null,
                null,
                null,
                null,
                null,
                currency,
                null,
                "date",
                "asc",
                0,
                10
        );

        when(cb.equal(root.get("currency"), Currency.valueOf(currency))).thenReturn(mock(Predicate.class));

        // Act
        Specification<Transfer> specification = TransferSpecification.buildTransferSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).equal(root.get("currency"), Currency.valueOf(currency));
    }

    @Test
    void buildTransferSpecification_WithStatusFilter_GeneratesEqualPredicate() {
        // Arrange
        String status = "APPROVED";
        TransferFilterOptions filterOptions = new TransferFilterOptions(
                null,
                null,
                null,
                null,
                null,
                null,
                status,
                "date",
                "asc",
                0,
                10
        );

        when(cb.equal(root.get("status"), TransferStatus.valueOf(status))).thenReturn(mock(Predicate.class));

        // Act
        Specification<Transfer> specification = TransferSpecification.buildTransferSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).equal(root.get("status"), TransferStatus.valueOf(status));
    }
}