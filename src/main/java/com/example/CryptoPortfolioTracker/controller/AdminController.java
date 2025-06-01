package com.example.CryptoPortfolioTracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.CryptoPortfolioTracker.dto.LoginRequest;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginRequest request) {
        return adminService.loginAdmin(request).getBody();
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<ApiResponse> updateAdmin(@PathVariable Long id, @RequestBody User user,
            @RequestParam String token) {
        return adminService.updateAdmin(id, user, token);
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers(@RequestParam String token) {
        return adminService.getAllUsers(token);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id, @RequestParam String token) {
        return adminService.getUserById(id, token);
    }

}
