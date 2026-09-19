package com.nearbuy.SecureNotes.service;

import com.nearbuy.SecureNotes.dto.RefreshTokenResult;
import com.nearbuy.SecureNotes.entity.RefreshToken;
import com.nearbuy.SecureNotes.repository.RefreshTokenRepository;
import com.nearbuy.SecureNotes.security.TokenHashUtil;
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
    private final TokenHashUtil tokenHashUtil;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            RefreshTokenSecurityService refreshTokenSecurityService,
            TokenHashUtil tokenHashUtil) {

        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenSecurityService =
                refreshTokenSecurityService;
        this.tokenHashUtil = tokenHashUtil;
    }

    public String createRefreshToken(String userEmail) {

        // 1. Generate the raw token
        String rawToken = UUID.randomUUID().toString();

        // 2. Hash the raw token
        String tokenHash = tokenHashUtil.hash(rawToken);

        // 3. Store ONLY the hash in the database
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setTokenHash(tokenHash);
        refreshToken.setUserEmail(userEmail);

        refreshToken.setExpiryDate(
                Instant.now().plus(30, ChronoUnit.DAYS)
        );

        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        // 4. Return the RAW token to the client
        return rawToken;
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

        String tokenHash = tokenHashUtil.hash(token);

        return refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token not found"));
    }
    @Transactional
    public void deleteByToken(String token) {

        String tokenHash = tokenHashUtil.hash(token);

        refreshTokenRepository.deleteByTokenHash(tokenHash);
    }

    @Transactional
    public RefreshTokenResult rotateRefreshToken(String token) {

        // 1. Hash the raw token received from the client
        String tokenHash = tokenHashUtil.hash(token);

        // 2. Find the stored token using the hash
        RefreshToken oldToken =
                refreshTokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Refresh token not found"
                                ));

        // 3. Detect reuse
        if (oldToken.isRevoked()) {

            refreshTokenSecurityService.revokeAllUserTokens(
                    oldToken.getUserEmail()
            );

            throw new RuntimeException(
                    "Refresh token reuse detected. Please login again."
            );
        }

        // 4. Check expiration
        if (oldToken.getExpiryDate()
                .isBefore(Instant.now())) {

            oldToken.setRevoked(true);
            refreshTokenRepository.save(oldToken);

            throw new RuntimeException(
                    "Refresh token has expired"
            );
        }

        String userEmail = oldToken.getUserEmail();

        // 5. Revoke the old refresh token
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        // 6. Generate NEW raw refresh token
        String newRawToken = UUID.randomUUID().toString();

        // 7. Hash the new token before storing
        String newTokenHash =
                tokenHashUtil.hash(newRawToken);

        RefreshToken newToken = new RefreshToken();

        newToken.setTokenHash(newTokenHash);
        newToken.setUserEmail(userEmail);

        newToken.setExpiryDate(
                Instant.now().plus(30, ChronoUnit.DAYS)
        );

        newToken.setRevoked(false);

        refreshTokenRepository.save(newToken);

        // 8. Return email + RAW token
        return new RefreshTokenResult(
                userEmail,
                newRawToken
        );
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