package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.dtos.user.UserOutput;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import com.example.virtualwallet.services.contracts.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Endpoints for admin actions like user, post, and comment management")
public class AdminRestController {

    private final UserService userService;

    @Operation(
            summary = "Retrieve all users",
            description = "Fetch a list of users",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsersNoFilters() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @GetMapping("/usersWithFilters")
    public ResponseEntity<?> getAllUsers2(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String accountStatus,
            @RequestParam(required = false) BigDecimal minTotalBalance,
            @RequestParam(required = false) BigDecimal maxTotalBalance,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().body("Invalid page or size parameters.");
        }

        Pageable pageable = PageRequest.of(page, size);
        UserFilterOptions userFilterOptions = new UserFilterOptions(
                username,
                email,
                phoneNumber,
                role,
                accountStatus,
                minTotalBalance,
                maxTotalBalance,
                sortBy,
                sortOrder
        );

        Page<UserOutput> userPage = userService.filterUsers(userFilterOptions, pageable);

        if (userPage.hasContent()) {
            return ResponseEntity.ok(userPage);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

}