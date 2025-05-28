package com.example.CryptoPortfolioTracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "holdings")
public class Holding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String coinId;
    private String coinName;
    private String coinSymbol;
    private Integer coinQuantity;
    private LocalDateTime boughtDate;
    private Long boughtPrice;
    private Double pricealert;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCoinId() { return coinId; }
    public void setCoinId(String coinId) { this.coinId = coinId; }

    public String getCoinName() { return coinName; }
    public void setCoinName(String coinName) { this.coinName = coinName; }

    public String getCoinSymbol() { return coinSymbol; }
    public void setCoinSymbol(String coinSymbol) { this.coinSymbol = coinSymbol; }

    public Integer getCoinQuantity() { return coinQuantity; }
    public void setCoinQuantity(Integer coinQuantity) { this.coinQuantity = coinQuantity; }

    public LocalDateTime getBoughtDate() { return boughtDate; }
    public void setBoughtDate(LocalDateTime boughtDate) { this.boughtDate = boughtDate; }

    public Long getBoughtPrice() { return boughtPrice; }
    public void setBoughtPrice(Long boughtPrice) { this.boughtPrice = boughtPrice; }

    public Double getPricealert() { return pricealert; }
    public void setPricealert(Double pricealert) { this.pricealert = pricealert; }
}
