package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.entity.Alert;
import com.example.CryptoPortfolioTracker.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    private EmailService emailService;

    @Autowired
private UserService userService;//after merging
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


Optional<User> receiverUser = userService.getUserById(alert.getUserId());
if(receiverUser.isPresent()) {
    emailService.sendEmail(receiverUser.get().getEmail(), "subject", message);
    System.out.println(message);

}
    }
}