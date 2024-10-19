package com.example.foodapp.model;

public class UserProfile {
    private String username;
    private String dob;
    private String imageUrl;

    public UserProfile() {};

    public UserProfile(String username, String dob, String imageUrl) {
        this.username = username;
        this.dob = dob;
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
