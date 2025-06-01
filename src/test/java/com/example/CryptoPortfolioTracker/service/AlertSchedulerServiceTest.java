package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.entity.Alert;
import com.example.CryptoPortfolioTracker.enums.AlertDirection;
import com.example.CryptoPortfolioTracker.enums.AlertStatus;
import com.example.CryptoPortfolioTracker.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AlertSchedulerServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private PriceService priceService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AlertSchedulerService alertSchedulerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkAlerts_noPendingAlerts_nothingHappens() {
        when(alertRepository.findByStatus(AlertStatus.PENDING)).thenReturn(List.of());

        alertSchedulerService.checkAlerts();

        verify(alertRepository, times(1)).findByStatus(AlertStatus.PENDING);
        verifyNoMoreInteractions(alertRepository, priceService, notificationService);
    }

    @Test
    void checkAlerts_priceNotFetched_skipsAlert() throws Exception {
        Alert alert = new Alert(1L, "btc", 30000.0, AlertDirection.ABOVE);
        alert.setStatus(AlertStatus.PENDING);

        when(alertRepository.findByStatus(AlertStatus.PENDING)).thenReturn(List.of(alert));
        when(priceService.getPrice("btc")).thenReturn(null);

        alertSchedulerService.checkAlerts();

        verify(priceService).getPrice("btc");
        verify(notificationService, never()).sendNotification(any(), anyDouble());
        verify(alertRepository, never()).save(any());
    }

    @Test
    void checkAlerts_alertDoesNotTrigger_priceBelowThreshold() throws Exception {
        Alert alert = new Alert(1L, "btc", 30000.0, AlertDirection.ABOVE);
        alert.setStatus(AlertStatus.PENDING);

        when(alertRepository.findByStatus(AlertStatus.PENDING)).thenReturn(List.of(alert));
        when(priceService.getPrice("btc")).thenReturn(29000.0);

        alertSchedulerService.checkAlerts();

        verify(notificationService, never()).sendNotification(any(), anyDouble());
        verify(alertRepository, never()).save(any());
    }

    @Test
    void checkAlerts_alertTriggersAboveDirection_notificationSentAndAlertUpdated() throws Exception {
        Alert alert = new Alert(1L, "btc", 30000.0, AlertDirection.ABOVE);
        alert.setStatus(AlertStatus.PENDING);

        when(alertRepository.findByStatus(AlertStatus.PENDING)).thenReturn(List.of(alert));
        when(priceService.getPrice("btc")).thenReturn(31000.0);
        doNothing().when(notificationService).sendNotification(alert, 31000.0);
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alertSchedulerService.checkAlerts();

        verify(notificationService).sendNotification(alert, 31000.0);
        verify(alertRepository).save(argThat(savedAlert ->
                savedAlert.getStatus() == AlertStatus.TRIGGERED &&
                        savedAlert.getTriggeredAt() != null));
    }

    @Test
    void checkAlerts_alertTriggersBelowDirection_notificationSentAndAlertUpdated() throws Exception {
        Alert alert = new Alert(2L, "eth", 2000.0, AlertDirection.BELOW);
        alert.setStatus(AlertStatus.PENDING);

        when(alertRepository.findByStatus(AlertStatus.PENDING)).thenReturn(List.of(alert));
        when(priceService.getPrice("eth")).thenReturn(1900.0);
        doNothing().when(notificationService).sendNotification(alert, 1900.0);
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alertSchedulerService.checkAlerts();

        verify(notificationService).sendNotification(alert, 1900.0);
        verify(alertRepository).save(argThat(savedAlert ->
                savedAlert.getStatus() == AlertStatus.TRIGGERED &&
                        savedAlert.getTriggeredAt() != null));
    }

    @Test
    void checkAlerts_notificationThrows_exceptionCaughtAndAlertStillUpdated() throws Exception {
        Alert alert = new Alert(3L, "ltc", 100.0, AlertDirection.ABOVE);
        alert.setStatus(AlertStatus.PENDING);

        when(alertRepository.findByStatus(AlertStatus.PENDING)).thenReturn(List.of(alert));
        when(priceService.getPrice("ltc")).thenReturn(150.0);

        doThrow(new RuntimeException("Notification error")).when(notificationService).sendNotification(alert, 150.0);
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> alertSchedulerService.checkAlerts());

        verify(notificationService).sendNotification(alert, 150.0);
        verify(alertRepository).save(argThat(savedAlert -> savedAlert.getStatus() == AlertStatus.TRIGGERED));
    }
}
