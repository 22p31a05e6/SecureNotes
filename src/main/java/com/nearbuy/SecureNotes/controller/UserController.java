package com.nearbuy.SecureNotes.controller;

import com.nearbuy.SecureNotes.dto.RegisterRequest;
import com.nearbuy.SecureNotes.entity.User;
import com.nearbuy.SecureNotes.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(
            @Valid @RequestBody RegisterRequest request) {

        User savedUser = userService.createUser(request);

        return ResponseEntity.ok(savedUser);
    }
}