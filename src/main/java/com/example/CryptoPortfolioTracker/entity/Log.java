package com.example.CryptoPortfolioTracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id") 
    private User user;

    private String coinId;
    private String coinName;
    private String coinSymbol;
    private Long boughtPrice;
    private Long soldPrice;
    private int quantity;
    private LocalDateTime boughtDate;
    private LocalDateTime soldDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCoinId() { return coinId; }
    public void setCoinId(String coinId) { this.coinId = coinId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public String getCoinName() { return coinName; }
    public void setCoinName(String coinName) { this.coinName = coinName; }

    public String getCoinSymbol() { return coinSymbol; }
    public void setCoinSymbol(String coinSymbol) { this.coinSymbol = coinSymbol; }

    public Long getBoughtPrice() { return boughtPrice; }
    public void setBoughtPrice(Long boughtPrice) { this.boughtPrice = boughtPrice; }

    public Long getSoldPrice() { return soldPrice; }
    public void setSoldPrice(Long soldPrice) { this.soldPrice = soldPrice; }

    public LocalDateTime getBoughtDate() { return boughtDate; }
    public void setBoughtDate(LocalDateTime boughtDate) { this.boughtDate = boughtDate; }

    public LocalDateTime getSoldDate() { return soldDate; }
    public void setSoldDate(LocalDateTime soldDate) { this.soldDate = soldDate; }
}
