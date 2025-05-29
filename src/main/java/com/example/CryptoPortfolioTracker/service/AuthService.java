package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.dto.LoginRequest;
import com.example.CryptoPortfolioTracker.dto.RegisterRequest;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.enums.Role;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    public ResponseEntity<ApiResponse> register(RegisterRequest req) {
        System.out.println("Custom Log: Came into Register Service");
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, "Username already exists"));
        }

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, "Email already exists"));
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(Role.USER);  // set default role, if needed

        userRepo.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "User registered successfully"));
    }

    public ResponseEntity<ApiResponse> login(LoginRequest req) {
        System.out.println("Custom Log: Came into Login Service");

        Optional<User> optionalUser = userRepo.findByUsernameOrEmail(req.getUsernameOrEmail(), req.getUsernameOrEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (encoder.matches(req.getPassword(), user.getPassword())) {
                if (!user.getRole().equals(Role.USER)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ApiResponse(false, "Access denied: Only USER role is allowed"));
                }

                // No token generation here anymore
                return ResponseEntity.ok(new ApiResponse(true, "Login successful"));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, "Invalid credentials"));
    }
}
