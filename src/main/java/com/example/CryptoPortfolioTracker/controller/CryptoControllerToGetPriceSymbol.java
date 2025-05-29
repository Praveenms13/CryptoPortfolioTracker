package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crypto")
public class CryptoControllerToGetPriceSymbol {

    @Autowired
    private PriceService priceService;

    @GetMapping("/getPrice/{symbol}")
    public ResponseEntity<?> getPrice(@PathVariable String symbol) {
        try {
            Double price = priceService.getPrice(symbol);
            if (price == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Symbol not found: " + symbol);
            }
            return ResponseEntity.ok(price);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching price for symbol: " + symbol);
        }
}
}
