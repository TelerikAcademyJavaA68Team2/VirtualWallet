package com.example.virtualwallet.services.specifications;

import com.example.virtualwallet.models.Exchange;
import com.example.virtualwallet.models.enums.Currency;
import com.example.virtualwallet.models.fillterOptions.ExchangeFilterOptions;
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

class ExchangeSpecificationTests {

    private CriteriaBuilder cb;
    private Root<Exchange> root;
    private CriteriaQuery<?> query;

    @BeforeEach
    void setup() {
        cb = mock(CriteriaBuilder.class);
        root = mock(Root.class);
        query = mock(CriteriaQuery.class);
    }

    @Test
    void buildExchangeSpecification_WithNoFilters_ReturnsEmptyPredicate() {
        // Arrange
        ExchangeFilterOptions filterOptions = new ExchangeFilterOptions(
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
        Specification<Exchange> specification = ExchangeSpecification.buildExchangeSpecification(filterOptions);
        Predicate predicate = specification.toPredicate(root, query, cb);

        // Assert
        assertNull(predicate);
        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb, times(1)).and(captor.capture());
        Predicate[] capturedPredicates = captor.getValue();
        assertEquals(0, capturedPredicates.length);
    }

    @Test
    void buildExchangeSpecification_WithRecipientUsernameFilter_GeneratesLikePredicate() {
        // Arrange
        ExchangeFilterOptions filterOptions = new ExchangeFilterOptions(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "john.doe",
                "date",
                "asc",
                0,
                10
        );

        when(root.get("recipientUsername")).thenReturn(mock());
        when(cb.like(any(), eq("%john.doe%"))).thenReturn(mock(Predicate.class));

        // Act
        Specification<Exchange> specification = ExchangeSpecification.buildExchangeSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).like(root.get("recipientUsername"), "%john.doe%");
    }

    @Test
    void buildExchangeSpecification_WithDateFilters_GeneratesBetweenPredicates() {
        // Arrange
        LocalDateTime fromDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime toDate = LocalDateTime.of(2023, 12, 31, 23, 59);
        String formattedFromDate = fromDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm"));
        String formattedToDate = toDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm"));

        ExchangeFilterOptions filterOptions = new ExchangeFilterOptions(
                formattedFromDate,
                formattedToDate,
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
        Specification<Exchange> specification = ExchangeSpecification.buildExchangeSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).greaterThanOrEqualTo(root.get("date"), fromDate);
        verify(cb).lessThanOrEqualTo(root.get("date"), toDate);
    }

    @Test
    void buildExchangeSpecification_WithCurrencyFilters_GeneratesEqualPredicates() {
        // Arrange
        ExchangeFilterOptions filterOptions = new ExchangeFilterOptions(
                null,
                null,
                "USD",
                "EUR",
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

        when(root.get("fromCurrency")).thenReturn(mock());
        when(root.get("toCurrency")).thenReturn(mock());
        when(cb.equal(root.get("fromCurrency"), Currency.USD)).thenReturn(mock(Predicate.class));
        when(cb.equal(root.get("toCurrency"), Currency.EUR)).thenReturn(mock(Predicate.class));

        // Act
        Specification<Exchange> specification = ExchangeSpecification.buildExchangeSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).equal(root.get("fromCurrency"), Currency.USD);
        verify(cb).equal(root.get("toCurrency"), Currency.EUR);
    }

    @Test
    void buildExchangeSpecification_WithAmountRangeFilters_GeneratesBetweenPredicates() {
        // Arrange
        ExchangeFilterOptions filterOptions = new ExchangeFilterOptions(
                null,
                null,
                null,
                null,
                new BigDecimal("100.00"),
                new BigDecimal("500.00"),
                new BigDecimal("80.00"),
                new BigDecimal("400.00"),
                null,
                "date",
                "asc",
                0,
                10
        );

        when(root.get("amount")).thenReturn(mock());
        when(root.get("toAmount")).thenReturn(mock());
        when(cb.greaterThanOrEqualTo(root.get("amount"), new BigDecimal("100.00")))
                .thenReturn(mock(Predicate.class));
        when(cb.lessThanOrEqualTo(root.get("amount"), new BigDecimal("500.00")))
                .thenReturn(mock(Predicate.class));
        when(cb.greaterThanOrEqualTo(root.get("toAmount"), new BigDecimal("80.00")))
                .thenReturn(mock(Predicate.class));
        when(cb.lessThanOrEqualTo(root.get("toAmount"), new BigDecimal("400.00")))
                .thenReturn(mock(Predicate.class));

        // Act
        Specification<Exchange> specification = ExchangeSpecification.buildExchangeSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        verify(cb).greaterThanOrEqualTo(root.get("amount"), new BigDecimal("100.00"));
        verify(cb).lessThanOrEqualTo(root.get("amount"), new BigDecimal("500.00"));
        verify(cb).greaterThanOrEqualTo(root.get("toAmount"), new BigDecimal("80.00"));
        verify(cb).lessThanOrEqualTo(root.get("toAmount"), new BigDecimal("400.00"));
    }

    @Test
    void buildExchangeSpecification_WithAllFilters_GeneratesCombinedPredicates() {
        // Arrange
        LocalDateTime fromDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime toDate = LocalDateTime.of(2023, 12, 31, 23, 59);
        String formattedFromDate = fromDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm"));
        String formattedToDate = toDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm"));
        ExchangeFilterOptions filterOptions = new ExchangeFilterOptions(
                formattedFromDate,
                formattedToDate,
                "USD",
                "EUR",
                new BigDecimal("100.00"),
                new BigDecimal("500.00"),
                new BigDecimal("80.00"),
                new BigDecimal("400.00"),
                "john.doe",
                "date",
                "asc",
                0,
                10
        );

        when(root.get("date")).thenReturn(mock());
        when(root.get("fromCurrency")).thenReturn(mock());
        when(root.get("toCurrency")).thenReturn(mock());
        when(root.get("amount")).thenReturn(mock());
        when(root.get("toAmount")).thenReturn(mock());
        when(root.get("recipientUsername")).thenReturn(mock());
        when(cb.greaterThanOrEqualTo(root.get("date"), fromDate)).thenReturn(mock(Predicate.class));
        when(cb.lessThanOrEqualTo(root.get("date"), toDate)).thenReturn(mock(Predicate.class));
        when(cb.equal(root.get("fromCurrency"), Currency.USD)).thenReturn(mock(Predicate.class));
        when(cb.equal(root.get("toCurrency"), Currency.EUR)).thenReturn(mock(Predicate.class));
        when(cb.greaterThanOrEqualTo(root.get("amount"), new BigDecimal("100.00"))).thenReturn(mock(Predicate.class));
        when(cb.lessThanOrEqualTo(root.get("amount"), new BigDecimal("500.00"))).thenReturn(mock(Predicate.class));
        when(cb.greaterThanOrEqualTo(root.get("toAmount"), new BigDecimal("80.00"))).thenReturn(mock(Predicate.class));
        when(cb.lessThanOrEqualTo(root.get("toAmount"), new BigDecimal("400.00"))).thenReturn(mock(Predicate.class));
        when(cb.like(root.get("recipientUsername"), "%john.doe%")).thenReturn(mock(Predicate.class));

        // Act
        Specification<Exchange> specification = ExchangeSpecification.buildExchangeSpecification(filterOptions);
        specification.toPredicate(root, query, cb);

        // Assert
        ArgumentCaptor<Predicate[]> captor = ArgumentCaptor.forClass(Predicate[].class);
        verify(cb, times(1)).and(captor.capture());
        Predicate[] capturedPredicates = captor.getValue();
        assertEquals(9, capturedPredicates.length);
    }
}