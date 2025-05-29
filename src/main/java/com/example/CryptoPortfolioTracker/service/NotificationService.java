package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.entity.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
@Autowired
private EmailService emailService;

//@Autowired
//private UserService userService;//after merging
    public void sendNotification(Alert alert, Double currentPrice) throws Exception{//some exception
        // Simulate email notification
        String message = String.format(
                "PRICE ALERT TRIGGERED!\n" +
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
//String receiverEmail = userService.getUserById(alert.getUserId()).getEmail();
emailService.sendEmail("aa1946@srmist.edu.in","subject", message);
        System.out.println(message);

        // TODO: Implement actual email sending logic here
        //emailService.sendAlert(alert.getUserId(), message);
}
}
