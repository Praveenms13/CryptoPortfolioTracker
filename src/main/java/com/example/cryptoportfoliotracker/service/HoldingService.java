package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.dto.AddHoldingRequest;
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

    public ResponseEntity<ApiResponse> addHolding(User user, AddHoldingRequest request) {
        try {
            Holding holding = new Holding();
            holding.setUser(user);
            holding.setCoinId(request.getCoinId());
            holding.setCoinName(request.getCoinName());
            holding.setCoinSymbol(request.getCoinSymbol());
            holding.setCoinQuantity(request.getQuantity());
            holding.setBoughtPrice(request.getBoughtPrice().longValue());
            holding.setBoughtDate(LocalDateTime.now());
            holding.setPricealert(null);

            holdingRepository.save(holding);

            return ResponseEntity.ok(new ApiResponse(true, "Holding added successfully", holding));
        } catch (Exception e) {
            System.err.println("Error adding holding: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error adding holding"));
        }
    }

}