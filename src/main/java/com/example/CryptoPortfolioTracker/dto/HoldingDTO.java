package com.example.CryptoPortfolioTracker.dto;

import com.example.CryptoPortfolioTracker.entity.Holding;

import java.time.LocalDateTime;
public class HoldingDTO {
    private Long id;
    private String coinId;
    private String coinName;
    private String coinSymbol;
    private double coinQuantity;
    private LocalDateTime boughtDate;
    private double boughtPrice;
    private Double pricealert;
    private int userId;
    public HoldingDTO() {}
    public HoldingDTO(Holding holding) {
        this.id = holding.getId();
        this.coinId = holding.getCoinId();
        this.coinName = holding.getCoinName();
        this.coinSymbol = holding.getCoinSymbol();
        this.coinQuantity = holding.getCoinQuantity();
        this.boughtDate = holding.getBoughtDate();
        this.boughtPrice = holding.getBoughtPrice();
        this.pricealert = holding.getPricealert();
        this.userId = Math.toIntExact(holding.getUser().getId());
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCoinId() { return coinId; }
    public void setCoinId(String coinId) { this.coinId = coinId; }

    public String getCoinName() { return coinName; }
    public void setCoinName(String coinName) { this.coinName = coinName; }

    public String getCoinSymbol() { return coinSymbol; }
    public void setCoinSymbol(String coinSymbol) { this.coinSymbol = coinSymbol; }

    public double getCoinQuantity() { return coinQuantity; }
    public void setCoinQuantity(double coinQuantity) { this.coinQuantity = coinQuantity; }

    public LocalDateTime getBoughtDate() { return boughtDate; }
    public void setBoughtDate(LocalDateTime boughtDate) { this.boughtDate = boughtDate; }

    public double getBoughtPrice() { return boughtPrice; }
    public void setBoughtPrice(double boughtPrice) { this.boughtPrice = boughtPrice; }

    public Double getPricealert() { return pricealert; }
    public void setPricealert(Double pricealert) { this.pricealert = pricealert; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
