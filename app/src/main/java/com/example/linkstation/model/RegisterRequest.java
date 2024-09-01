// RegisterRequest.java
package com.example.linkstation.model;

public class RegisterRequest {
    private String email;
    private String username;
    private String password;
    private String otp;

    public RegisterRequest(String email, String username, String password, String otp) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.otp = otp;
    }

    // Getters and setters can be added here if needed
}
