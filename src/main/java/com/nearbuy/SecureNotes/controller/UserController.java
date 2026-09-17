package com.nearbuy.SecureNotes.controller;

import com.nearbuy.SecureNotes.dto.RegisterRequest;
import com.nearbuy.SecureNotes.entity.User;
import com.nearbuy.SecureNotes.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nearbuy.SecureNotes.dto.UserResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody RegisterRequest request) {

        User savedUser = userService.createUser(request);

        UserResponse response = new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );

        return ResponseEntity.ok(response);
    }
}