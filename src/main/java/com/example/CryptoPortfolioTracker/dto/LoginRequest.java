package com.example.CryptoPortfolioTracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login request object")
public class LoginRequest {
    private String usernameOrEmail;
    private String password;

    public LoginRequest(String admin, String password) {
        this.usernameOrEmail =  admin;
        this.password = password;
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
