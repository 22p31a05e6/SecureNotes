package com.nearbuy.SecureNotes.controller;

import com.nearbuy.SecureNotes.dto.AuthResponse;
import com.nearbuy.SecureNotes.dto.LoginRequest;
import com.nearbuy.SecureNotes.dto.RefreshTokenRequest;
import com.nearbuy.SecureNotes.entity.RefreshToken;
import com.nearbuy.SecureNotes.security.JwtService;
import com.nearbuy.SecureNotes.service.RefreshTokenService;
import com.nearbuy.SecureNotes.dto.RefreshTokenResult;

import jakarta.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        String email = authentication.getName();

        String accessToken =
                jwtService.generateToken(email);

        String refreshToken =
                refreshTokenService.createRefreshToken(email);

        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }
    @PostMapping("/refresh")
    public AuthResponse refresh(
            @RequestBody RefreshTokenRequest request) {

        RefreshTokenResult result =
                refreshTokenService.rotateRefreshToken(
                        request.getRefreshToken()
                );

        String accessToken =
                jwtService.generateToken(
                        result.getUserEmail()
                );

        return new AuthResponse(
                accessToken,
                result.getRefreshToken()
        );
    }
    @PostMapping("/logout")
    public String logout(
            @RequestBody RefreshTokenRequest request) {

        refreshTokenService.deleteByToken(
                request.getRefreshToken()
        );

        return "Logged out successfully";
    }
}