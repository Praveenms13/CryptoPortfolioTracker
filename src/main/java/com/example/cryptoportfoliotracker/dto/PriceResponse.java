package com.example.cryptoportfoliotracker.dto;

import java.util.List;

public class PriceResponse {
    private boolean result;
    private String message;
    private List<CryptoData> data;

    // Getters and setters
    public boolean isResult() { return result; }
    public void setResult(boolean result) { this.result = result; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<CryptoData> getData() { return data; }
    public void setData(List<CryptoData> data) { this.data = data; }
}

