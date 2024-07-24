// RegisterRequest.java
package com.example.linkstation.model;

public class RegisterRequest {
    private String email;
    private String username;
    private String password;

    public RegisterRequest(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // Getters and setters can be added here if needed
}
