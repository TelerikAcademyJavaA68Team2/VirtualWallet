package com.example.virtualwallet.models.dtos;

import com.example.virtualwallet.models.dtos.user.UserOutput;
import lombok.Data;

import java.util.List;

@Data
public class UserPageOutput {

    int numberOfPages;

    int totalResults;

    List<UserOutput> content;
}
