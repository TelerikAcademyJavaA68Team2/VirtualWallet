package com.example.virtualwallet.models.dtos.pageable;

import com.example.virtualwallet.models.dtos.user.UserOutput;
import lombok.Data;

import java.util.List;

@Data
public class UserPageOutput {

    int numberOfPages;

    long totalResults;

    List<UserOutput> content;
}
