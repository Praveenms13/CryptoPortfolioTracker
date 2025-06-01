package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.dto.AddHoldingRequest;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import com.example.CryptoPortfolioTracker.service.HoldingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/holdings")
public class HoldingController {

    @Autowired
    private HoldingService holdingService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addHolding(@RequestBody AddHoldingRequest request,
            @RequestParam Long userId,
            @RequestParam String token) {
        return holdingService.addHolding(userId, token, request);
    }

    @GetMapping("/getHoldingsWithNetValue")
    public ResponseEntity<ApiResponse> getHoldingsWithNetValue(@RequestParam Long userId, @RequestParam String token) {
        return holdingService.getHoldingsWithNetValue(userId, token);
    }

    @PostMapping("/sell")
    public ResponseEntity<ApiResponse> sellCrypto(
            @RequestParam Long userId,
            @RequestParam String token,
            @RequestParam String coinId,
            @RequestParam int quantity) {
        
        return holdingService.sellHolding(userId, token, coinId, quantity);
    }
}
