package com.nearbuy.SecureNotes.repository;

import com.nearbuy.SecureNotes.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteByTokenHash(String tokenHash);

    List<RefreshToken> findByUserEmail(String userEmail);
}