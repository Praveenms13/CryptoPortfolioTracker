package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getAllUsers")
    public ResponseEntity<ApiResponse> getAllUsers() {
        System.out.println("Custom Log: Came into Get All Users");
        return userService.getAllUsers();
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        System.out.println("Custom Log: Came into Get User by ID");
        return userService.getUserById(id);
    }

    @PostMapping("/updateUser/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody User user) {
        System.out.println("Custom Log: Came into Update User by ID");
        return userService.updateUser(id, user);
    }
}
