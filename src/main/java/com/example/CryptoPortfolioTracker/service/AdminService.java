package com.example.CryptoPortfolioTracker.service;

<<<<<<< HEAD
=======
import com.example.CryptoPortfolioTracker.dto.LoginRequest;
>>>>>>> origin/bhanu
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface AdminService {
<<<<<<< HEAD
=======
    ResponseEntity<ApiResponse> login(LoginRequest req);
>>>>>>> origin/bhanu
    ResponseEntity<ApiResponse> getAllUsers();
    ResponseEntity<ApiResponse> getUserById(Long id);
}
