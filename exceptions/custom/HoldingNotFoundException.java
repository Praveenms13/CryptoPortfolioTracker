package com.example.CryptoPortfolioTracker.exceptions.custom;

public class HoldingNotFoundException extends RuntimeException {
    public HoldingNotFoundException(String message) {
        super(message);
    }

    public HoldingNotFoundException(Long holdingId) {
        super("Holding not found with ID: " + holdingId);
    }
}