package com.nearbuy.SecureNotes.dto;

public class RefreshTokenResult {

    private final String userEmail;
    private final String refreshToken;

    public RefreshTokenResult(
            String userEmail,
            String refreshToken) {

        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}