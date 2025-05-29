package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.dto.LoginRequest;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import com.example.CryptoPortfolioTracker.service.AdminService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    public AdminController(AdminService adminService, UserRepository userRepository) {
        this.adminService = adminService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginRequest req) {
        System.out.println("Custom Log: Came into Login Request");
        return adminService.login(req).getBody();
    }

    @GetMapping("/users")
    public ApiResponse getAllUsers() {
        // No JWT or auth checks here
        return adminService.getAllUsers().getBody();
    }

    @GetMapping("/user/{id}")
    public ApiResponse getUserById(@PathVariable Long id) {
        // No JWT or auth checks here
        return adminService.getUserById(id).getBody();
    }
}
