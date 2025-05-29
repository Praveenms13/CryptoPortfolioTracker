package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.config.JwtUtil;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import com.example.CryptoPortfolioTracker.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/getId")
    public ResponseEntity<ApiResponse> getLoggedInUserId(HttpServletRequest request) {
        System.out.println("Custom Log: Came into Get Logged In User ID");

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "You are UnAuthorized"));
        }

        String token = authHeader.substring(7);
        String username;

        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "You are UnAuthorized"));
        }

        return userRepo.findByUsernameOrEmail(username, username)
                .map(user -> ResponseEntity.ok(new ApiResponse(true, "User ID fetched", user.getId())))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "User not found")));
    }

    /* @GetMapping("/getAllUsers")
    public ResponseEntity<ApiResponse> getAllUsers() {
        System.out.println("Custom Log: Came into Get All Users");
        return userService.getAllUsers();
    } */

    /* @GetMapping("/getUser/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        System.out.println("Custom Log: Came into Get User by ID");
        return userService.getUserById(id);
    } */
    @GetMapping("/getUser/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id, HttpServletRequest request) {
        System.out.println("Custom Log: Came into Get User by ID");

        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "You are UnAuthorized"));
        }

        String jwt = authorizationHeader.substring(7);
        String username;

        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "You are UnAuthorized"));
        }

        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "User not found"));
        }

        User user = userOptional.get();
        if (!user.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "You can't access this user's data"));
        }

        return ResponseEntity.ok(new ApiResponse(true, "User found", user));
    }

    /* @PostMapping("/updateUser/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody User user) {
        System.out.println("Custom Log: Came into Update User by ID");
        return userService.updateUser(id, user);
    } */
    // @PutMapping("/updateUser/{id}")
    // @PostMapping("/updateUser/{id}")
    @PutMapping("/updateUser/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody User user) {
        System.out.println("Custom Log: Came into Update User by ID");
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userRepo.findByUsername(loggedInUsername).orElse(null);
        if (loggedInUser == null || !loggedInUser.getId().equals(id)) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "Unauthorized access"));
        }
        User existingUser = userRepo.findById(id).orElse(null);
        if (existingUser == null) {
            return ResponseEntity.status(404).body(new ApiResponse(false, "User not found"));
        }
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        userRepo.save(existingUser);
        return ResponseEntity.ok(new ApiResponse(true, "User updated successfully"));
    }

}
