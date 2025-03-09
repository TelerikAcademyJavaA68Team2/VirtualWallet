package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import com.example.virtualwallet.services.contracts.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Endpoints for admin actions like user, post, and comment management")
public class AdminRestController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> filterUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String accountStatus,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().body("Invalid page or size parameters.");
        }
        UserFilterOptions userFilterOptions = new UserFilterOptions(
                username,
                email,
                phoneNumber,
                role,
                accountStatus,
                sortBy,
                sortOrder,
                page,
                size
        );
        return ResponseEntity.ok(userService.filterUsers(userFilterOptions));
    }

    @GetMapping("users/{id}")
    public ResponseEntity<?> getUserInfoById(@PathVariable UUID id) {
        return new ResponseEntity<>(userService.getUserProfileById(id), HttpStatus.OK);
    }

    @PostMapping("users/{id}/make-admin")
    public ResponseEntity<?> promoteUserToAdmin(@PathVariable UUID id) {
        userService.promoteToAdmin(id);
        return new ResponseEntity<>("Admin promotion was successful", HttpStatus.OK);
    }

    @PostMapping("users/{id}/revoke-admin")
    public ResponseEntity<?> demoteAdminToUser(@PathVariable UUID id) {
        userService.demoteToUser(id);
        return new ResponseEntity<>("Admin demoted successfully", HttpStatus.OK);
    }

    @PostMapping("users/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable UUID id) {
        userService.blockUser(id);
        return new ResponseEntity<>("Block was successful", HttpStatus.OK);
    }

    @PostMapping("users/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable UUID id) {
        userService.unblockUser(id);
        return new ResponseEntity<>("Unblock was successful", HttpStatus.OK);
    }
}