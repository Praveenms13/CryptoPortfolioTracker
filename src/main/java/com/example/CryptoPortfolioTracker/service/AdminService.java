package com.example.CryptoPortfolioTracker.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.CryptoPortfolioTracker.dto.LoginRequest;
import com.example.CryptoPortfolioTracker.dto.SimpleUserResponse;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.enums.Role;
import com.example.CryptoPortfolioTracker.exception.ResourceNotFoundException;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;

@Service
public class AdminService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<ApiResponse> getAllUsers(String token) {
        User admin = userRepo.findBySessionToken(token).orElse(null);
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Unauthorized: Admin access required"));
        }

        List<User> allUsers = userRepo.findAll();
        List<SimpleUserResponse> userOnly = new ArrayList<>();
        for (User u : allUsers) {
            if (u.getRole() == Role.USER) {
                userOnly.add(new SimpleUserResponse(u.getId(), u.getUsername(), u.getName(), u.getEmail()));
            }
        }

        return ResponseEntity.ok(new ApiResponse(true, "All users fetched", userOnly));
    }

    public ResponseEntity<ApiResponse> getUserById(Long id, String token) {
        User admin = userRepo.findBySessionToken(token).orElse(null);
        if (admin == null || admin.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Unauthorized: Admin access required"));
        }

        User user = userRepo.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "User not found"));
        }

        if (user.getRole() != Role.USER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "Forbidden: Not a regular user"));
        }

        SimpleUserResponse response = new SimpleUserResponse(user.getId(), user.getUsername(), user.getName(),
                user.getEmail());

        return ResponseEntity.ok(new ApiResponse(true, "User fetched", response));
    }

    public ResponseEntity<ApiResponse> loginAdmin(LoginRequest request) {
        String usernameOrEmail = request.getUsernameOrEmail();
        String password = request.getPassword();
        User user = userRepo.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("Admin not found with username or email: " + usernameOrEmail);
        }
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "Access denied: Only ADMIN role is allowed"));
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid password"));
        }
        String token = UUID.randomUUID().toString();
        user.setSessionToken(token);
        userRepo.save(user);
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("token", token);
        return ResponseEntity.ok(new ApiResponse(true, "Admin login successful", data));
    }

    public ResponseEntity<ApiResponse> updateAdmin(Long id, User user, String token) {
        User requester = userRepo.findBySessionToken(token).orElse(null);
        if (requester == null || requester.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid User"));
        }
        if (!requester.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "Forbidden: You can only update your own account"));
        }
        User existingUser = userRepo.findById(id).orElse(null);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "User not found"));
        }
        if (user.getUsername() != null)
            existingUser.setUsername(user.getUsername());
        if (user.getEmail() != null)
            existingUser.setEmail(user.getEmail());
        if (user.getPassword() != null)
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getName() != null)
            existingUser.setName(user.getName());
        userRepo.save(existingUser);
        return ResponseEntity.ok(new ApiResponse(true, "Admin updated successfully"));
    }
}