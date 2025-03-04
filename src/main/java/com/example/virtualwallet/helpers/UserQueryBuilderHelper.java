package com.example.virtualwallet.helpers;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserQueryBuilderHelper {

    @Getter
    private final Map<String, Object> parameters = new HashMap<>();
    private final StringBuilder queryBuilder = new StringBuilder();
    private boolean hasFilters = false;

    public UserQueryBuilderHelper() {
        queryBuilder.append("SELECT NEW com.example.virtualwallet.models.dtos.user.UserOutput(");
        queryBuilder.append("u.id, u.username, u.email, u.phoneNumber, u.role, u.status, COALESCE(SUM(w.balance), 0) AS totalBalance) ");
        queryBuilder.append("FROM User u LEFT JOIN Wallet w ON u.id = w.owner.id ");
    }

    public void addUsernameFilter(Optional<String> username) {
        appendFilter(username, "username", "u.username LIKE :username");
    }

    public void addEmailFilter(Optional<String> email) {
        appendFilter(email, "email", "u.email LIKE :email");
    }

    public void addPhoneNumberFilter(Optional<String> phoneNumber) {
        appendFilter(phoneNumber, "phoneNumber", "u.phoneNumber LIKE :phoneNumber");
    }

    public void addRoleFilter(Optional<String> role) {
        appendFilter(role, "role", "u.role = :role");
    }

    public void addAccountStatusFilter(Optional<String> accountStatus) {
        appendFilter(accountStatus, "accountStatus", "u.status = :accountStatus");
    }

    public void addMinTotalBalanceFilter(Optional<BigDecimal> minTotalBalance) {
        appendFilter(minTotalBalance, "minTotalBalance", "(COALESCE(SUM(w.balance), 0) >= :minTotalBalance OR w IS NULL)");
    }

    public void addMaxTotalBalanceFilter(Optional<BigDecimal> maxTotalBalance) {
        appendFilter(maxTotalBalance, "maxTotalBalance", "(COALESCE(SUM(w.balance), 0) <= :maxTotalBalance OR w IS NULL)");
    }

    public void finalizeQuery(String sortBy, String sortOrder) {
        if (!hasFilters) {
            queryBuilder.append(" WHERE 1=1 ");
        }
        queryBuilder.append(" GROUP BY u.id, u.username, u.email, u.phoneNumber, u.role, u.status ");
        if (!sortBy.isEmpty() && !sortOrder.isEmpty()) {
            queryBuilder.append(" ORDER BY ").append(sortBy).append(" ").append(sortOrder);
        }
    }

    public String getQueryString() {
        return queryBuilder.toString();
    }

    public String getCountQuery() {
        if (hasFilters) {
            String filteringPart = queryBuilder.substring(queryBuilder.indexOf("WHERE"), queryBuilder.indexOf("GROUP BY"));
            return "SELECT COUNT(DISTINCT u.id) FROM User u LEFT JOIN Wallet w ON u.id = w.owner.id " +
                    filteringPart +
                    " GROUP BY u.id";
        } else {
            return "SELECT COUNT(DISTINCT u.id) FROM User u LEFT JOIN Wallet w ON u.id = w.owner.id GROUP BY u.id";
        }
    }

    private void appendFilter(Optional<?> value, String parameterName, String condition) {
        value.ifPresent(v -> {
            if (!hasFilters) {
                queryBuilder.append(" WHERE ");
            } else {
                queryBuilder.append(" AND ");
            }
            queryBuilder.append(condition).append(" ");
            parameters.put(parameterName, v instanceof String ? "%" + v + "%" : v);
            hasFilters = true;
        });
    }
}