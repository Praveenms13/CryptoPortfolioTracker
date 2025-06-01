package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.dto.RegisterRequest;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    /* public ResponseEntity<ApiResponse> getAllUsers() {
        System.out.println("Custom Log: Came into Get All Users Service to get all users");
        List<User> users = userRepo.findAll();
        return ResponseEntity.ok(new ApiResponse(true, "Users fetched", users));
    } */
    public ResponseEntity<ApiResponse> registerUser(RegisterRequest request, PasswordEncoder encoder) {
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
        newUser.setPassword(encoder.encode(request.getPassword()));
        newUser.setVerified(false);

        User savedUser = userRepo.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "User registered successfully", savedUser.getId()));
    }



    public Optional<User> getUserById(Long id) {
        System.out.println("Custom Log: Came into Get User Service to get User by ID");
        return userRepo.findById(id);
    }

    public ResponseEntity<ApiResponse> updateUser(Long id, User updatedUser) {
        System.out.println("Custom Log: Came into Update User Service");
        return userRepo.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            userRepo.save(user);
            return ResponseEntity.ok(new ApiResponse(true, "User updated", user));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, "User not found")));
    }
}
