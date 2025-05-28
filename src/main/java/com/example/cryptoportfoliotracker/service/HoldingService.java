package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.entity.Holding;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.HoldingRepository;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HoldingService {

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<ApiResponse> getHoldingsByUserId(int userId) {
        try {
            List<Holding> holdings = holdingRepository.findByUserIdOrderByBoughtDateDesc(userId);

            if (holdings.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse(true, "No holdings found for this user", holdings));
            }

            return ResponseEntity.ok(new ApiResponse(true, "Holdings fetched successfully", holdings));

        } catch (Exception e) {
            System.err.println("Error fetching holdings: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error fetching holdings"));
        }
    }
}