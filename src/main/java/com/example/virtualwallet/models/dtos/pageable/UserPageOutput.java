package com.example.virtualwallet.models.dtos.pageable;

import com.example.virtualwallet.models.dtos.user.UserOutput;
import lombok.Data;

import java.util.List;

@Data
public class UserPageOutput {

    private List<UserOutput> content;

    private long totalElements;
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private boolean hasNextPage;
    private boolean hasPreviousPage;

}