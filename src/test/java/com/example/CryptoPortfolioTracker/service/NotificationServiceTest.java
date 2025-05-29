package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.entity.Alert;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.enums.AlertDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    public void testSendNotification_UserExists_EmailSent() throws Exception {
        // Arrange
        Alert alert = new Alert();
        alert.setSymbol("btc");
        alert.setTriggerPrice(25000.0);
        alert.setDirection(AlertDirection.ABOVE);
        alert.setUserId(1L);

        User mockUser = new User();
        mockUser.setEmail("user@example.com");

        when(userService.getUserById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        notificationService.sendNotification(alert, 25500.0);

        // Assert
        String expectedMessage = String.format(
                "PRICE ALERT TRIGGERED!\n" +
                        "Symbol: %s\n" +
                        "Current Price: $%.2f\n" +
                        "Trigger Price: $%.2f\n" +
                        "Direction: %s\n" +
                        "User ID: %d",
                "BTC", 25500.0, 25000.0, "ABOVE", 1L);

        verify(emailService, times(1))
                .sendEmail("user@example.com", "subject", expectedMessage);
    }

    @Test
    public void testSendNotification_UserDoesNotExist_EmailNotSent() throws Exception {
        // Arrange
        Alert alert = new Alert();
        alert.setSymbol("eth");
        alert.setTriggerPrice(1800.0);
        alert.setDirection(AlertDirection.BELOW);
        alert.setUserId(2L);

        when(userService.getUserById(2L)).thenReturn(Optional.empty());

        // Act
        notificationService.sendNotification(alert, 1700.0);

        // Assert
        verify(emailService, never()).sendEmail(any(), any(), any());
    }
}
