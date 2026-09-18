package com.nearbuy.SecureNotes.repository;

import com.nearbuy.SecureNotes.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByUserEmail(String userEmail);

    void deleteByToken(String token);
}