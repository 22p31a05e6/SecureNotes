package com.nearbuy.SecureNotes.service;

import com.nearbuy.SecureNotes.entity.RefreshToken;
import com.nearbuy.SecureNotes.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenSecurityService
            refreshTokenSecurityService;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            RefreshTokenSecurityService refreshTokenSecurityService) {

        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenSecurityService =
                refreshTokenSecurityService;
    }

    public RefreshToken createRefreshToken(String userEmail) {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken.setUserEmail(userEmail);

        refreshToken.setExpiryDate(
                Instant.now().plus(30, ChronoUnit.DAYS)
        );

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(
            RefreshToken refreshToken) {

        if (refreshToken.getExpiryDate()
                .isBefore(Instant.now())) {

            refreshTokenRepository.delete(refreshToken);

            throw new RuntimeException(
                    "Refresh token has expired"
            );
        }

        return refreshToken;
    }
    public RefreshToken findByToken(String token) {

        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token not found"));
    }
    @Transactional
    public void deleteByToken(String token) {

        refreshTokenRepository.deleteByToken(token);
    }

    @Transactional
    public RefreshToken rotateRefreshToken(String token) {

        RefreshToken oldToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Refresh token not found"
                                ));

        if (oldToken.isRevoked()) {

            refreshTokenSecurityService.revokeAllUserTokens(
                    oldToken.getUserEmail()
            );

            throw new RuntimeException(
                    "Refresh token reuse detected. Please login again."
            );
        }

        if (oldToken.getExpiryDate()
                .isBefore(Instant.now())) {

            oldToken.setRevoked(true);
            refreshTokenRepository.save(oldToken);

            throw new RuntimeException(
                    "Refresh token has expired"
            );
        }

        String userEmail = oldToken.getUserEmail();

        // Revoke the old token
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        // Create a new refresh token
        RefreshToken newToken = new RefreshToken();

        newToken.setToken(
                UUID.randomUUID().toString()
        );

        newToken.setUserEmail(userEmail);

        newToken.setExpiryDate(
                Instant.now().plus(30, ChronoUnit.DAYS)
        );

        newToken.setRevoked(false);

        return refreshTokenRepository.save(newToken);
    }
    @Transactional
    public void revokeAllUserTokens(String userEmail) {

        List<RefreshToken> tokens =
                refreshTokenRepository.findByUserEmail(userEmail);

        for (RefreshToken token : tokens) {
            token.setRevoked(true);
        }

        refreshTokenRepository.saveAll(tokens);
    }
}