package com.nearbuy.SecureNotes.controller;

import com.nearbuy.SecureNotes.dto.AuthResponse;
import com.nearbuy.SecureNotes.dto.LoginRequest;
import com.nearbuy.SecureNotes.dto.RefreshTokenRequest;
import com.nearbuy.SecureNotes.entity.RefreshToken;
import com.nearbuy.SecureNotes.security.JwtService;
import com.nearbuy.SecureNotes.service.RefreshTokenService;

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

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(email);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken()
        );
    }
    @PostMapping("/refresh")
    public AuthResponse refresh(
            @RequestBody RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.findByToken(
                        request.getRefreshToken()
                );

        refreshTokenService.verifyExpiration(refreshToken);

        String accessToken =
                jwtService.generateToken(
                        refreshToken.getUserEmail()
                );

        return new AuthResponse(
                accessToken,
                refreshToken.getToken()
        );
    }
}