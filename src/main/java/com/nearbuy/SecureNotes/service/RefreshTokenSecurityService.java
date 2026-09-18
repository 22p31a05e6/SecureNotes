package com.nearbuy.SecureNotes.service;

import com.nearbuy.SecureNotes.entity.RefreshToken;
import com.nearbuy.SecureNotes.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RefreshTokenSecurityService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenSecurityService(
            RefreshTokenRepository refreshTokenRepository) {

        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeAllUserTokens(String userEmail) {

        List<RefreshToken> tokens =
                refreshTokenRepository.findByUserEmail(userEmail);

        for (RefreshToken token : tokens) {
            token.setRevoked(true);
        }

        refreshTokenRepository.saveAll(tokens);
    }
}