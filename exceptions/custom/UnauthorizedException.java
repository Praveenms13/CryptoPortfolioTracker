package com.example.CryptoPortfolioTracker.exceptions.custom;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("Access denied. Insufficient privileges");
    }
}