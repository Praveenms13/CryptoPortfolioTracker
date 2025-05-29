package com.example.cryptoportfoliotracker.dto;

import java.math.BigDecimal;

public class AddHoldingRequest {

    private String coinId;
    private String coinName;
    private String coinSymbol;
    private Integer quantity;
    private BigDecimal boughtPrice;

    public AddHoldingRequest() {}

    public AddHoldingRequest(String coinId, String coinName, String coinSymbol,
                             Integer quantity, BigDecimal boughtPrice) {
        this.coinId = coinId;
        this.coinName = coinName;
        this.coinSymbol = coinSymbol;
        this.quantity = quantity;
        this.boughtPrice = boughtPrice;
    }
    public String getCoinId() {
        return coinId;
    }
    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }
    public String getCoinName() {
        return coinName;
    }
    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }
    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getBoughtPrice() {
        return boughtPrice;
    }

    public void setBoughtPrice(BigDecimal boughtPrice) {
        this.boughtPrice = boughtPrice;
    }

    @Override
    public String toString() {
        return "AddHoldingRequest{" +
                "coinId='" + coinId + '\'' +
                ", coinName='" + coinName + '\'' +
                ", coinSymbol='" + coinSymbol + '\'' +
                ", quantity=" + quantity +
                ", boughtPrice=" + boughtPrice +
                '}';
    }
}