package com.example.CryptoPortfolioTracker.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.example.CryptoPortfolioTracker.dto.RegisterRequest;
import com.example.CryptoPortfolioTracker.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.CryptoPortfolioTracker.exception.ResourceNotFoundException;

import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import com.example.CryptoPortfolioTracker.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        String username = request.getUsernameOrEmail();
        String password = request.getPassword();

        Optional<User> userOptional = userRepo.findByUsernameOrEmail(username, username);

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with username or email: " + username);
        }

        User user = userOptional.get();

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

    private User authenticateToken(String token) {
        if (token == null || !token.startsWith("Bearer "))
            return null;
        String realToken = token.substring(7);
        return userRepo.findBySessionToken(realToken).orElse(null);
    }
    @GetMapping("/getId")
    public ResponseEntity<ApiResponse> getLoggedInUserId(@RequestHeader("Authorization") String authHeader) {
        System.out.println("authheader here: " + authHeader);
        User user = authenticateToken(authHeader);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid or missing token"));
        return ResponseEntity.ok(new ApiResponse(true, "User ID fetched", user.getId()));
    }
    @GetMapping("/getUser/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id,
                                                   @RequestHeader("Authorization") String authHeader) {
        User requester = authenticateToken(authHeader);
        if (requester == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid or missing token"));
        }

        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found"));
        }

        User targetUser = userOptional.get();
        if (!Objects.equals(requester.getId(), targetUser.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "You can't access this user's data"));
        }

        return ResponseEntity.ok(new ApiResponse(true, "User found", targetUser));
    }
    @PutMapping("/updateUser/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody User user,
                                                  @RequestHeader("Authorization") String authHeader) {
        User requester = authenticateToken(authHeader);
        if (requester == null || !requester.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Unauthorized access"));
        }

        Optional<User> existingUserOptional = userRepo.findById(id);
        if (existingUserOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found"));
        }

        User existingUser = existingUserOptional.get();

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

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody RegisterRequest request) {
        return userService.registerUser(request, passwordEncoder);
    }

}
