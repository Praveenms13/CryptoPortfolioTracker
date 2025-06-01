package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.dto.AlertRequestDTO;
import com.example.CryptoPortfolioTracker.entity.Alert;
import com.example.CryptoPortfolioTracker.enums.AlertDirection;
import com.example.CryptoPortfolioTracker.enums.AlertStatus;
import com.example.CryptoPortfolioTracker.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAlert_success() {
        AlertRequestDTO dto = new AlertRequestDTO();
        dto.setUserId(1L);
        dto.setSymbol("BTC");
        dto.setTriggerPrice(30000.0);
        dto.setDirection(AlertDirection.valueOf("UP"));

        Alert expectedAlert = new Alert(1L, "btc", 30000.0, AlertDirection.UP);

        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Alert result = alertService.createAlert(dto);

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository).save(captor.capture());

        Alert savedAlert = captor.getValue();

        assertEquals(dto.getUserId(), savedAlert.getUserId());
        assertEquals(dto.getSymbol().toLowerCase(), savedAlert.getSymbol());
        assertEquals(dto.getTriggerPrice(), savedAlert.getTriggerPrice());
        assertEquals(AlertDirection.UP, savedAlert.getDirection());

        assertEquals(savedAlert, result);
    }

    @Test
    void updateAlert_success() {
        Long alertId = 1L;

        Alert existingAlert = new Alert(alertId, "btc", 25000.0, AlertDirection.DOWN);
        existingAlert.setStatus(AlertStatus.PENDING);

        AlertRequestDTO updatedDto = new AlertRequestDTO();
        updatedDto.setSymbol("ETH");
        updatedDto.setTriggerPrice(2000.0);
        updatedDto.setDirection(AlertDirection.valueOf("UP"));

        when(alertRepository.findById(alertId)).thenReturn(Optional.of(existingAlert));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Alert updatedAlert = alertService.updateAlert(alertId, updatedDto);

        assertEquals(updatedDto.getSymbol().toLowerCase(), updatedAlert.getSymbol());
        assertEquals(updatedDto.getTriggerPrice(), updatedAlert.getTriggerPrice());
        assertEquals(AlertDirection.UP, updatedAlert.getDirection());
        assertEquals(AlertStatus.PENDING, updatedAlert.getStatus());
        assertNull(updatedAlert.getTriggeredAt());
    }

    @Test
    void disableAlert_success() {
        Long alertId = 1L;
        Alert alert = new Alert(alertId, "btc", 25000.0, AlertDirection.UP);
        alert.setStatus(AlertStatus.PENDING);

        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Alert disabledAlert = alertService.disableAlert(alertId);

        assertEquals(AlertStatus.DISABLED, disabledAlert.getStatus());
    }

    @Test
    void deleteAlert_success() {
        Long alertId = 1L;

        when(alertRepository.existsById(alertId)).thenReturn(true);
        doNothing().when(alertRepository).deleteById(alertId);

        assertDoesNotThrow(() -> alertService.deleteAlert(alertId));

        verify(alertRepository).deleteById(alertId);
    }

    @Test
    void deleteAlert_notFound_throws() {
        Long alertId = 1L;

        when(alertRepository.existsById(alertId)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> alertService.deleteAlert(alertId));
        assertEquals("Alert not found with id: " + alertId, ex.getMessage());
    }

    @Test
    void getAlertsByUserId_success() {
        Long userId = 1L;

        List<Alert> mockAlerts = List.of(
                new Alert(userId, "btc", 30000.0, AlertDirection.UP),
                new Alert(userId, "eth", 2000.0, AlertDirection.DOWN)
        );

        when(alertRepository.findByUserId(userId)).thenReturn(mockAlerts);

        List<Alert> alerts = alertService.getAlertsByUserId(userId);

        assertEquals(2, alerts.size());
    }

    @Test
    void getTriggeredAlerts_success() {
        Long userId = 1L;

        List<Alert> triggeredAlerts = List.of(
                new Alert(userId, "btc", 30000.0, AlertDirection.UP)
        );
        triggeredAlerts.get(0).setStatus(AlertStatus.TRIGGERED);

        when(alertRepository.findByUserIdAndStatus(userId, AlertStatus.TRIGGERED)).thenReturn(triggeredAlerts);

        List<Alert> alerts = alertService.getTriggeredAlerts(userId);

        assertEquals(1, alerts.size());
        assertEquals(AlertStatus.TRIGGERED, alerts.get(0).getStatus());
    }

    @Test
    void getPendingAlerts_success() {
        List<Alert> pendingAlerts = List.of(
                new Alert(1L, "btc", 30000.0, AlertDirection.UP),
                new Alert(2L, "eth", 2000.0, AlertDirection.DOWN)
        );
        pendingAlerts.get(0).setStatus(AlertStatus.PENDING);
        pendingAlerts.get(1).setStatus(AlertStatus.PENDING);

        when(alertRepository.findByStatus(AlertStatus.PENDING)).thenReturn(pendingAlerts);

        List<Alert> alerts = alertService.getPendingAlerts();

        assertEquals(2, alerts.size());
        assertTrue(alerts.stream().allMatch(a -> a.getStatus() == AlertStatus.PENDING));
    }
}
