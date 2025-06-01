package com.example.CryptoPortfolioTracker.exceptions.custom;

public class CryptoServiceException extends RuntimeException {
    public CryptoServiceException(String message) {
        super(message);
    }

    public CryptoServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}