package com.example.CryptoPortfolioTracker.service.Implementation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.CryptoPortfolioTracker.dto.ClientResponse;
import com.example.CryptoPortfolioTracker.dto.LoginRequest;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.enums.Role;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import com.example.CryptoPortfolioTracker.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public AdminServiceImpl(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @Override
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<ClientResponse> users = userRepo.findAll().stream()
                .filter(user -> user.getRole() == Role.USER)
                .map(user -> new ClientResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getName(),
                        user.getEmail()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse(true, "All users fetched", users));
    }

    @Override
    public ResponseEntity<ApiResponse> getUserById(Long id) {
        Optional<User> userOpt = userRepo.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse(false, "User not found"));
        }

        User user = userOpt.get();

        if (user.getRole() != Role.USER) {
            return ResponseEntity.status(403).body(new ApiResponse(false, "Not a regular user"));
        }

        ClientResponse dto = new ClientResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail()
        );

        return ResponseEntity.ok(new ApiResponse(true, "User fetched", dto));
    }

    public ResponseEntity<ApiResponse> login(LoginRequest req) {
        System.out.println("Custom Log: Came into Login Service");

        Optional<User> optionalUser = userRepo.findByUsernameOrEmail(req.getUsernameOrEmail(), req.getUsernameOrEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (encoder.matches(req.getPassword(), user.getPassword())) {
                if (!user.getRole().equals(Role.ADMIN)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ApiResponse(false, "Access denied: Only ADMIN role is allowed"));
                }

                // Removed JWT token generation, just send a success message
                return ResponseEntity.ok(new ApiResponse(true, "Login successful"));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, "Invalid credentials"));
    }
}
