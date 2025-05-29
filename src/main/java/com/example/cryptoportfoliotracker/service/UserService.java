package com.example.cryptoportfoliotracker.service;

import com.example.cryptoportfoliotracker.entity.User;
import com.example.cryptoportfoliotracker.model.ApiResponse;
import com.example.cryptoportfoliotracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    /* public ResponseEntity<ApiResponse> getAllUsers() {
        System.out.println("Custom Log: Came into Get All Users Service to get all users");
        List<User> users = userRepo.findAll();
        return ResponseEntity.ok(new ApiResponse(true, "Users fetched", users));
    } */

    public ResponseEntity<ApiResponse> getUserById(Long id) {
        System.out.println("Custom Log: Came into Get User Service to get User by ID");
        return userRepo.findById(id)
                .map(user -> ResponseEntity.ok(new ApiResponse(true, "User found", user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "User not found")));
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
