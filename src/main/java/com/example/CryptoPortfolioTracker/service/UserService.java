package com.example.CryptoPortfolioTracker.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.CryptoPortfolioTracker.dto.LoginRequest;
import com.example.CryptoPortfolioTracker.dto.RegisterRequest;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.enums.Role;
import com.example.CryptoPortfolioTracker.exception.ResourceNotFoundException;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<ApiResponse> register(RegisterRequest request) {
        String gmailRegex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        if (!request.getEmail().matches(gmailRegex)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Invalid Gmail address"));
        }
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Username already exists"));
        }
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Email already registered"));
        }
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setName(request.getName());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepo.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "User registered successfully", savedUser.getId()));
    }

    public ResponseEntity<ApiResponse> loginUser(LoginRequest request) {
        String usernameOrEmail = request.getUsernameOrEmail();
        String password = request.getPassword();
        User user = userRepo.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with username or email: " + usernameOrEmail);
        }
        if (user.getRole() != Role.USER) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid role"));
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
        return ResponseEntity.ok(new ApiResponse(true, "Login successful", data));
    }

    public ResponseEntity<ApiResponse> updateUser(Long id, User user, String token) {
        User requester = userRepo.findBySessionToken(token).orElse(null);
        if (requester == null || requester.getRole() != Role.USER) {
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
        return ResponseEntity.ok(new ApiResponse(true, "User updated successfully"));
    }
}
