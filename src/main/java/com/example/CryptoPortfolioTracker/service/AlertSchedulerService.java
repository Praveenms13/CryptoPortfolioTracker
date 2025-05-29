package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.entity.Alert;
import com.example.CryptoPortfolioTracker.enums.AlertDirection;
import com.example.CryptoPortfolioTracker.enums.AlertStatus;
import com.example.CryptoPortfolioTracker.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertSchedulerService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private PriceService priceService;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(fixedRate = 30000) // Runs every 30 seconds
    public void checkAlerts() {
        List<Alert> pendingAlerts = alertRepository.findByStatus(AlertStatus.PENDING);

        System.out.println("Checking " + pendingAlerts.size() + " pending alerts...");

        for (Alert alert : pendingAlerts) {
            try {
                Double currentPrice = priceService.getPrice(alert.getSymbol());
                if (currentPrice == null) {
                    System.out.println("Could not fetch price for: " + alert.getSymbol());
                    continue;
                }

                boolean shouldTrigger = false;

                if (alert.getDirection() == AlertDirection.ABOVE && currentPrice > alert.getTriggerPrice()) {
                    shouldTrigger = true;
                } else if (alert.getDirection() == AlertDirection.BELOW && currentPrice < alert.getTriggerPrice()) {
                    shouldTrigger = true;
                }

                if (shouldTrigger) {
                    try {
                        notificationService.sendNotification(alert, currentPrice);
                    } catch (Exception e) {
                        e.printStackTrace(); // log error from notification service
                    } finally {
                        System.out.println("Alert triggered for " + alert.getSymbol() + " at $" + currentPrice);
                        alert.setStatus(AlertStatus.TRIGGERED);
                        alert.setTriggeredAt(LocalDateTime.now());
                        alertRepository.save(alert);
                    }
                }

            } catch (Exception e) {
                System.err.println("Error processing alert ID " + alert.getId() + ": " + e.getMessage());
            }
        }
    }
}