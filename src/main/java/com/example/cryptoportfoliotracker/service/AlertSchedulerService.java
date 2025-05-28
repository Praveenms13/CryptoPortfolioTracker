package com.example.cryptoportfoliotracker.service;

import com.example.cryptoportfoliotracker.entity.Alert;
import com.example.cryptoportfoliotracker.entity.AlertDirection;
import com.example.cryptoportfoliotracker.entity.AlertStatus;
import com.example.cryptoportfoliotracker.repository.AlertRepository;
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

    @Scheduled(fixedRate = 30000) // Check every 30 seconds
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

                boolean shouldTrigger = checkTriggerCondition(alert, currentPrice);

                if (shouldTrigger) {
                    alert.setStatus(AlertStatus.TRIGGERED);
                    alert.setTriggeredAt(LocalDateTime.now());
                    alertRepository.save(alert);

                    notificationService.sendNotification(alert, currentPrice);
                    System.out.println("Alert triggered for " + alert.getSymbol() + " at $" + currentPrice);
                }
            } catch (Exception e) {
                System.err.println("Error processing alert ID " + alert.getId() + ": " + e.getMessage());
            }
        }
    }

    private boolean checkTriggerCondition(Alert alert, Double currentPrice) {
        return (alert.getDirection() == AlertDirection.ABOVE && currentPrice >= alert.getTriggerPrice()) ||
                (alert.getDirection() == AlertDirection.BELOW && currentPrice <= alert.getTriggerPrice());
}
}
