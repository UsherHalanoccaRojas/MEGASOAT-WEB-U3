package com.example.demo.web.dto;

public class UserProfileResponse {
    private String email;
    private String fullName;
    private String username;
    private String role;
    private boolean active;

    public UserProfileResponse() {}

    public UserProfileResponse(String email, String fullName, String username, String role, boolean active) {
        this.email = email;
        this.fullName = fullName;
        this.username = username;
        this.role = role;
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
