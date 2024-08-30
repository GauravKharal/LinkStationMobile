package com.example.linkstation.model;

public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;

    public ChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;

    }

    // Getter methods for the fields
    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    // Setter methods for the fields
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

