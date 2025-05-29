package com.example.cryptoportfoliotracker.dto;

import com.example.cryptoportfoliotracker.entity.AlertDirection;

public class AlertRequestDTO {
    private Long userId;
    private String symbol;
    private Double triggerPrice;
    private AlertDirection direction;

    // Constructors
    public AlertRequestDTO() {}

    public AlertRequestDTO(Long userId, String symbol, Double triggerPrice, AlertDirection direction) {
        this.userId = userId;
        this.symbol = symbol;
        this.triggerPrice = triggerPrice;
        this.direction = direction;
    }


    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public Double getTriggerPrice() { return triggerPrice; }
    public void setTriggerPrice(Double triggerPrice) { this.triggerPrice = triggerPrice; }

    public AlertDirection getDirection() { return direction; }
    public void setDirection(AlertDirection direction) { this.direction =direction;
    }
}
