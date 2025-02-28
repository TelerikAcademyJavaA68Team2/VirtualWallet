package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.models.Dtos.UserOutput;
import com.example.virtualwallet.models.fillterOptions.UserFilterOptions;
import com.example.virtualwallet.services.contracts.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final UserService userService;

    public AdminRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers(@RequestParam(required = false) String username,
                                              @RequestParam(required = false) String email,
                                              @RequestParam(required = false) String phoneNumber,
                                              @RequestParam(required = false) String account_type,
                                              @RequestParam(required = false) String account_status,
                                              @RequestParam(required = false) Integer minNumberOfTransactions,
                                              @RequestParam(required = false) Integer maxNumberOfTransactions,
                                              @RequestParam(required = false) String orderBy,
                                              @RequestParam(required = false) String orderType,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size) {

        Sort sort = (orderBy != null && orderType != null)
                ? Sort.by(Sort.Direction.fromString(orderType), orderBy)
                : Sort.unsorted();

        UserFilterOptions userFilterOptions = new UserFilterOptions(
                username,
                email,
                phoneNumber,
                account_type,
                account_status,
                minNumberOfTransactions,
                maxNumberOfTransactions,
                orderBy);

   /*     Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserOutput> userPage = userService.filterUsers(userFilterOptions, pageable);

        if (userPage.hasContent()) {
            return ResponseEntity.ok(userPage);
        } else {
            return ResponseEntity.noContent().build();
        }*/
        return ResponseEntity.ok("userPage");
    }

}
