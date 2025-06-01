package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.entity.Alert;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendNotification_ShouldSendEmail_WhenUserExists() throws Exception {
        Alert alert = new Alert();
        alert.setSymbol("btc");
        alert.setTriggerPrice(50000.0);
        alert.setDirection(null);
        alert.setUserId(1L);

        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        notificationService.sendNotification(alert, 55000.0);


        verify(emailService).sendEmail(
                eq("user@example.com"),
                eq("subject"),
                contains("PRICE ALERT TRIGGERED!")
        );
    }

    @Test
    void sendNotification_ShouldDoNothing_WhenUserDoesNotExist() throws Exception {
        Alert alert = new Alert();
        alert.setUserId(2L);
        alert.setSymbol("btc");

        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        notificationService.sendNotification(alert, 1000.0);

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

}