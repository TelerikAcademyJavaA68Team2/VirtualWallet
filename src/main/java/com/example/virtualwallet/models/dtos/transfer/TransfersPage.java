package com.example.virtualwallet.models.dtos.transfer;

import lombok.Data;

import java.util.List;

@Data
public class TransfersPage {

    private List<TransferOutput> transfers;

    private long totalElements;
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
}