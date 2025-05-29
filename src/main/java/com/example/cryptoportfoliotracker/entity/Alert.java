package com.example.cryptoportfoliotracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private Double triggerPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status = AlertStatus.PENDING;

    private LocalDateTime triggeredAt;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public Alert() {}

    public Alert(Long userId, String symbol, Double triggerPrice, AlertDirection direction) {
        this.userId = userId;
        this.symbol = symbol;
        this.triggerPrice = triggerPrice;
        this.direction = direction;
        this.status = AlertStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public Double getTriggerPrice() { return triggerPrice; }
    public void setTriggerPrice(Double triggerPrice) { this.triggerPrice = triggerPrice; }

    public AlertDirection getDirection() { return direction; }
    public void setDirection(AlertDirection direction) { this.direction = direction; }

    public AlertStatus getStatus() { return status; }
    public void setStatus(AlertStatus status) { this.status = status; }

    public LocalDateTime getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(LocalDateTime triggeredAt) { this.triggeredAt = triggeredAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isTriggered() {
        return this.status == AlertStatus.TRIGGERED;
}
}