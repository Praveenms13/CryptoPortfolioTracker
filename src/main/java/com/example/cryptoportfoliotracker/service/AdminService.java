package com.example.cryptoportfoliotracker.service;

import com.example.cryptoportfoliotracker.dto.LoginRequest;
import com.example.cryptoportfoliotracker.model.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<ApiResponse> login(LoginRequest req);
    ResponseEntity<ApiResponse> getAllUsers();
    ResponseEntity<ApiResponse> getUserById(Long id);
}
