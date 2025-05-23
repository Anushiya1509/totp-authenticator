package com.anushiya.totp.dto;

public class VerifyTotpRequest {
    private String username;
    private String code;

    public String getUsername() {
        return username;
    }

    public String getCode() {
        return code;
    }

    public VerifyTotpRequest(String username, String code) {
        this.username = username;
        this.code = code;
    }
}
