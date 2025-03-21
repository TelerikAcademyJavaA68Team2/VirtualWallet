package com.example.virtualwallet.models.dtos.exchange;

import lombok.Data;

import java.util.List;

@Data
public class ExchangePage {

    private List<ExchangeOutput> exchanges;

    private long totalElements;
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
}