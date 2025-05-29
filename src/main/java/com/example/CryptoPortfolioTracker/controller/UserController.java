package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import com.example.CryptoPortfolioTracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // LOGIN ENDPOINT (token generated and stored)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Optional<User> userOptional = userRepo.findByUsernameOrEmail(username, username);
        if (userOptional.isEmpty() || !passwordEncoder.matches(password, userOptional.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid credentials"));
        }

        User user = userOptional.get();

        // Generate token
        String token = UUID.randomUUID().toString();
        user.setSessionToken(token);
        userRepo.save(user);

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("token", token);

        return ResponseEntity.ok(new ApiResponse(true, "Login successful", data));
    }

    // AUTH HELPER FUNCTION
    private User authenticateToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) return null;
        String realToken = token.substring(7);
        return userRepo.findBySessionToken(realToken).orElse(null);
    }

    // Get logged-in user ID using token
    @GetMapping("/getId")
    public ResponseEntity<ApiResponse> getLoggedInUserId(@RequestHeader("Authorization") String authHeader) {
        User user = authenticateToken(authHeader);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Invalid or missing token"));
        return ResponseEntity.ok(new ApiResponse(true, "User ID fetched", user.getId()));
    }

    // Get user by ID
    @GetMapping("/getUser/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        User requester = authenticateToken(authHeader);
        if (requester == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Invalid or missing token"));
        }

        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found"));
        }

        User targetUser = userOptional.get();
        if (!Objects.equals(requester.getId(), targetUser.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "You can't access this user's data"));
        }

        return ResponseEntity.ok(new ApiResponse(true, "User found", targetUser));
    }

    // Update user by ID
    @PutMapping("/updateUser/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody User user, @RequestHeader("Authorization") String authHeader) {
        User requester = authenticateToken(authHeader);
        if (requester == null || !requester.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Unauthorized access"));
        }

        Optional<User> existingUserOptional = userRepo.findById(id);
        if (existingUserOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found"));
        }

        User existingUser = existingUserOptional.get();

        if (user.getUsername() != null) existingUser.setUsername(user.getUsername());
        if (user.getEmail() != null) existingUser.setEmail(user.getEmail());
        if (user.getPassword() != null)
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getName() != null) existingUser.setName(user.getName());

        userRepo.save(existingUser);

        return ResponseEntity.ok(new ApiResponse(true, "User updated successfully"));
    }
}
