package com.example.linkstation.model;

public class SocialMedia {
    private int icon;          // Icon resource ID for the social media platform
    private String username;   // Username or account name

    // Constructor to initialize the social media item with an icon and username
    public SocialMedia(int icon, String username) {
        this.icon = icon;
        this.username = username;
    }

    // Getter for the icon resource ID
    public int getIcon() {
        return icon;
    }

    // Getter for the username
    public String getUsername() {
        return username;
    }

    // Setter for updating the username
    public void setUsername(String username) {
        this.username = username;
    }
}
