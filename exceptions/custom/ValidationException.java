package com.example.CryptoPortfolioTracker.exceptions.custom;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}