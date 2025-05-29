package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.dto.LoginRequest;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<ApiResponse> login(LoginRequest req);
    ResponseEntity<ApiResponse> getAllUsers();
    ResponseEntity<ApiResponse> getUserById(Long id);
}
