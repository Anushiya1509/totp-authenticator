package com.anushiya.totp.dto;

public class UsernameRequest {
    private String username;

    public String getUsername() {
        return username;
    }

    public UsernameRequest(String username) {
        this.username = username;
    }
}
