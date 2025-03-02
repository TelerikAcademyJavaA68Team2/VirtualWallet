package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.UserOutput;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import com.example.virtualwallet.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsersNoFilters() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/usersWIthFilter")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String accountStatus,
            @RequestParam(required = false) BigDecimal minTotalBalance,
            @RequestParam(required = false) BigDecimal maxTotalBalance,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 1 || size <= 0) {
            return ResponseEntity.badRequest().body("Invalid page or size parameters.");
        }

        Sort sort = (sortBy != null && sortOrder != null)
                ? Sort.by(Sort.Direction.fromString(sortOrder), sortBy)
                : Sort.unsorted();

        UserFilterOptions userFilterOptions = new UserFilterOptions(
                username,
                email,
                phoneNumber,
                role,
                accountStatus,
                minTotalBalance,
                maxTotalBalance);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserOutput> userPage = userService.filterUsers(userFilterOptions, pageable);

        if (userPage.hasContent()) {
            return ResponseEntity.ok(userPage);
        } else {
            return ResponseEntity.noContent().build();
        }
    }



}