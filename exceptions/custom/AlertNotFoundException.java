package com.example.CryptoPortfolioTracker.exceptions.custom;

public class AlertNotFoundException extends RuntimeException {
    public AlertNotFoundException(String message) {
        super(message);
    }

    public AlertNotFoundException(Long alertId) {
        super("Alert not found with ID: " + alertId);
    }
}