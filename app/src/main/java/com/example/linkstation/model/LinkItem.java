package com.example.linkstation.model;

import android.net.Uri;

public class LinkItem {
    private Uri imageUri;
    private String title;
    private Uri url;
    private boolean selected;

    public LinkItem(Uri imageUri, String title, Uri url) {
        this.imageUri = imageUri;
        this.title = title;
        this.url = url;
        this.selected = false;
    }

    // Getters and setters
    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getUrl() {
        return url;
    }

    public void setUrl(Uri url) {
        this.url = url;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
