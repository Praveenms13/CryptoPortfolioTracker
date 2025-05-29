package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.config.JwtUtil;
import com.example.CryptoPortfolioTracker.enums.Role;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import com.example.CryptoPortfolioTracker.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AdminController(AdminService adminService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Authorization token is missing or invalid."));
        }

        token = token.substring(7);
        String username;

        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid token."));
        }

        User user = userRepository.findByUsernameOrEmail(username, username).orElse(null);
        // System.out.print("=> "+user.getRole());
        // System.out.print("=> "+Role.ADMIN.name());
        if (user == null || user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "Access denied. Admins only."));
        }

        return adminService.getAllUsers();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Authorization token is missing or invalid."));
        }

        token = token.substring(7);
        String username;

        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid token."));
        }

        User user = userRepository.findByUsernameOrEmail(username, username).orElse(null);

        if (user == null || user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "Access denied. Admins only."));
        }


        return adminService.getUserById(id);
    }
}