package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.entity.Alert;
import com.example.CryptoPortfolioTracker.enums.AlertDirection;
import com.example.CryptoPortfolioTracker.enums.AlertStatus;
import com.example.CryptoPortfolioTracker.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertSchedulerServiceTest {

    @InjectMocks
    private AlertSchedulerService alertSchedulerService;

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private PriceService priceService;

    @Mock
    private NotificationService notificationService;

    private Alert alertAboveTrigger;
    private Alert alertBelowTrigger;
    private Alert alertNotTriggered;

    @BeforeEach
    void setUp() {
        alertAboveTrigger = new Alert();
        alertAboveTrigger.setId(1L);
        alertAboveTrigger.setSymbol("BTC");
        alertAboveTrigger.setTriggerPrice(30000.0);
        alertAboveTrigger.setDirection(AlertDirection.ABOVE);
        alertAboveTrigger.setStatus(AlertStatus.PENDING);
        alertAboveTrigger.setUserId(10L); // assuming you have this field

        alertBelowTrigger = new Alert();
        alertBelowTrigger.setId(2L);
        alertBelowTrigger.setSymbol("ETH");
        alertBelowTrigger.setTriggerPrice(2000.0);
        alertBelowTrigger.setDirection(AlertDirection.BELOW);
        alertBelowTrigger.setStatus(AlertStatus.PENDING);
        alertBelowTrigger.setUserId(20L);

        alertNotTriggered = new Alert();
        alertNotTriggered.setId(3L);
        alertNotTriggered.setSymbol("DOGE");
        alertNotTriggered.setTriggerPrice(0.5);
        alertNotTriggered.setDirection(AlertDirection.ABOVE);
        alertNotTriggered.setStatus(AlertStatus.PENDING);
        alertNotTriggered.setUserId(30L);
    }

    @Test
    void testCheckAlerts_TriggersAlertsAndSaves() throws Exception {
        List<Alert> pendingAlerts = Arrays.asList(alertAboveTrigger, alertBelowTrigger, alertNotTriggered);

        when(alertRepository.findByStatus(AlertStatus.PENDING)).thenReturn(pendingAlerts);

        when(priceService.getPrice("BTC")).thenReturn(35000.0);
        when(priceService.getPrice("ETH")).thenReturn(1500.0);
        when(priceService.getPrice("DOGE")).thenReturn(0.4);

        // Do nothing when sendNotification is called (default is do nothing for void)
        doNothing().when(notificationService).sendNotification(any(Alert.class), any(Double.class));

        alertSchedulerService.checkAlerts();

        // Verify notificationService called only for alerts that should trigger
        verify(notificationService).sendNotification(eq(alertAboveTrigger), eq(35000.0));
        verify(notificationService).sendNotification(eq(alertBelowTrigger), eq(1500.0));
        verify(notificationService, never()).sendNotification(eq(alertNotTriggered), any(Double.class));

        // Capture saved alerts
        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository, times(2)).save(captor.capture());

        List<Alert> savedAlerts = captor.getAllValues();

        for (Alert alert : savedAlerts) {
            assertEquals(AlertStatus.TRIGGERED, alert.getStatus());
            assertNotNull(alert.getTriggeredAt());
        }
    }

    @Test
    void testCheckAlerts_PriceServiceReturnsNull() throws Exception {
        List<Alert> pendingAlerts = List.of(alertAboveTrigger);

        when(alertRepository.findByStatus(AlertStatus.PENDING)).thenReturn(pendingAlerts);
        when(priceService.getPrice("BTC")).thenReturn(null);

        alertSchedulerService.checkAlerts();

        verify(notificationService, never()).sendNotification(any(Alert.class), any(Double.class));
        verify(alertRepository, never()).save(any());
    }

    @Test
    void testCheckAlerts_NotificationServiceThrowsException() throws Exception {
        List<Alert> pendingAlerts = List.of(alertAboveTrigger);

        when(alertRepository.findByStatus(AlertStatus.PENDING)).thenReturn(pendingAlerts);
        when(priceService.getPrice("BTC")).thenReturn(40000.0);

        doThrow(new RuntimeException("Notification failure"))
                .when(notificationService).sendNotification(alertAboveTrigger, 40000.0);

        alertSchedulerService.checkAlerts();

        // Even if notification throws, alert status updated and saved
        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository).save(captor.capture());

        Alert savedAlert = captor.getValue();
        assertEquals(AlertStatus.TRIGGERED, savedAlert.getStatus());
        assertNotNull(savedAlert.getTriggeredAt());
    }
}
