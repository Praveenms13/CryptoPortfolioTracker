package com.example.CryptoPortfolioTracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "price_alert")
public class PriceAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String coinId;
    private Integer triggerPrice;
    private String direction;
    private Boolean status;
    private Long triggeredAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCoinId() { return coinId; }
    public void setCoinId(String coinId) { this.coinId = coinId; }

    public Integer getTriggerPrice() { return triggerPrice; }
    public void setTriggerPrice(Integer triggerPrice) { this.triggerPrice = triggerPrice; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public Long getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(Long triggeredAt) { this.triggeredAt = triggeredAt; }
}
