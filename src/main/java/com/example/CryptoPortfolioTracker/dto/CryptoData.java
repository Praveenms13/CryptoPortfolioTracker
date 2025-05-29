package com.example.CryptoPortfolioTracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CryptoData {

    private String id;
    private String symbol;
    private String name;

    @JsonProperty("current_price")
    private Double currentPrice;

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }
}
