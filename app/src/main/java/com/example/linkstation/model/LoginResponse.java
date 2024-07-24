package com.example.linkstation.model;

public class LoginResponse {
    private boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
