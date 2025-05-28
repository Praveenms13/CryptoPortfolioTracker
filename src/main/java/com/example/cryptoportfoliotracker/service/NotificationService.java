package com.example.cryptoportfoliotracker.service;

import com.example.cryptoportfoliotracker.entity.Alert;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendNotification(Alert alert, Double currentPrice) {
        // Simulate email notification
        String message = String.format(
                "🚨 PRICE ALERT TRIGGERED!\n" +
                        "Symbol: %s\n" +
                        "Current Price: $%.2f\n" +
                        "Trigger Price: $%.2f\n" +
                        "Direction: %s\n" +
                        "User ID: %d",
                alert.getSymbol().toUpperCase(),
                currentPrice,
                alert.getTriggerPrice(),
                alert.getDirection(),
                alert.getUserId()
        );

        System.out.println(message);

        // TODO: Implement actual email sending logic here
        //emailService.sendAlert(alert.getUserId(), message);
}
}
