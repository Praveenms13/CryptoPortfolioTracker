package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.dto.LoginRequest;
import com.example.CryptoPortfolioTracker.dto.RegisterRequest;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.service.AuthService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse register(@RequestBody RegisterRequest req) {
        System.out.println("Custom Log: Came into Register Request");
        return authService.register(req).getBody();
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginRequest req) {
        System.out.println("Custom Log: Came into Login Request");
        return authService.login(req).getBody();
    }
}
