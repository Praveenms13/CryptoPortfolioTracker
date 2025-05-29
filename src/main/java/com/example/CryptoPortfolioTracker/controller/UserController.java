package com.example.CryptoPortfolioTracker.controller;

<<<<<<< HEAD
=======
import com.example.CryptoPortfolioTracker.config.JwtUtil;
>>>>>>> d7004b07246d1267259ae862815afc0d5a5bbab4
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import com.example.CryptoPortfolioTracker.service.UserService;
<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
=======
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
>>>>>>> d7004b07246d1267259ae862815afc0d5a5bbab4

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
<<<<<<< HEAD

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
=======
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
>>>>>>> d7004b07246d1267259ae862815afc0d5a5bbab4
        }

        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isEmpty()) {
<<<<<<< HEAD
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
=======
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

>>>>>>> d7004b07246d1267259ae862815afc0d5a5bbab4
}
