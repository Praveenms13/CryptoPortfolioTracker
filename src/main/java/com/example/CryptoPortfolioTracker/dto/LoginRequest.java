package com.example.CryptoPortfolioTracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login request object")
public class LoginRequest {

    // @Schema(description = "Username or email of the user", example = "johndoe")
    private String usernameOrEmail;

    // @Schema(description = "Password of the user", example = "securePassword123")
    private String password;

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
