package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.dto.AlertRequestDTO;
import com.example.CryptoPortfolioTracker.entity.Alert;
import com.example.CryptoPortfolioTracker.enums.AlertDirection;
import com.example.CryptoPortfolioTracker.enums.AlertStatus;
import com.example.CryptoPortfolioTracker.repository.AlertRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @InjectMocks
    private AlertService alertService;

    @Mock
    private AlertRepository alertRepository;

    private AlertRequestDTO alertRequestDTO;
    private Alert alert;

    @BeforeEach
    void setUp() {
        alertRequestDTO = new AlertRequestDTO();
        alertRequestDTO.setUserId(1L);
        alertRequestDTO.setSymbol("BTC");
        alertRequestDTO.setTriggerPrice(50000.0);
        alertRequestDTO.setDirection(AlertDirection.ABOVE);

        alert = new Alert(
                alertRequestDTO.getUserId(),
                alertRequestDTO.getSymbol().toLowerCase(),
                alertRequestDTO.getTriggerPrice(),
                alertRequestDTO.getDirection()
        );
        alert.setId(100L);
        alert.setStatus(AlertStatus.PENDING);
    }

    @Test
    void testCreateAlert() {
        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        Alert created = alertService.createAlert(alertRequestDTO);

        assertNotNull(created);
        assertEquals("btc", created.getSymbol());
        assertEquals(AlertStatus.PENDING, created.getStatus());
        verify(alertRepository, times(1)).save(any(Alert.class));
    }

    @Test
    void testUpdateAlert_Success() {
        when(alertRepository.findById(100L)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenAnswer(i -> i.getArgument(0));

        AlertRequestDTO updatedDto = new AlertRequestDTO();
        updatedDto.setUserId(1L);  // userId usually doesn't change but can be kept for DTO completeness
        updatedDto.setSymbol("ETH");
        updatedDto.setTriggerPrice(3000.0);
        updatedDto.setDirection(AlertDirection.BELOW);

        Alert updated = alertService.updateAlert(100L, updatedDto);

        assertEquals("eth", updated.getSymbol());
        assertEquals(3000.0, updated.getTriggerPrice());
        assertEquals(AlertDirection.BELOW, updated.getDirection());
        assertEquals(AlertStatus.PENDING, updated.getStatus());
        assertNull(updated.getTriggeredAt());
    }

    @Test
    void testUpdateAlert_NotFound() {
        when(alertRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                alertService.updateAlert(999L, alertRequestDTO));

        assertTrue(thrown.getMessage().contains("Alert not found with id"));
    }

    @Test
    void testDisableAlert_Success() {
        when(alertRepository.findById(100L)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenAnswer(i -> i.getArgument(0));

        Alert disabled = alertService.disableAlert(100L);

        assertEquals(AlertStatus.DISABLED, disabled.getStatus());
    }

    @Test
    void testDisableAlert_NotFound() {
        when(alertRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                alertService.disableAlert(999L));

        assertTrue(thrown.getMessage().contains("Alert not found with id"));
    }

    @Test
    void testDeleteAlert_Success() {
        when(alertRepository.existsById(100L)).thenReturn(true);

        alertService.deleteAlert(100L);

        verify(alertRepository, times(1)).deleteById(100L);
    }

    @Test
    void testDeleteAlert_NotFound() {
        when(alertRepository.existsById(999L)).thenReturn(false);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                alertService.deleteAlert(999L));

        assertTrue(thrown.getMessage().contains("Alert not found with id"));
        verify(alertRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetAlertsByUserId() {
        when(alertRepository.findByUserId(1L)).thenReturn(Arrays.asList(alert));

        List<Alert> alerts = alertService.getAlertsByUserId(1L);

        assertEquals(1, alerts.size());
        verify(alertRepository).findByUserId(1L);
    }

    @Test
    void testGetTriggeredAlerts() {
        alert.setStatus(AlertStatus.TRIGGERED);
        when(alertRepository.findByUserIdAndStatus(1L, AlertStatus.TRIGGERED))
                .thenReturn(Arrays.asList(alert));

        List<Alert> triggeredAlerts = alertService.getTriggeredAlerts(1L);

        assertEquals(1, triggeredAlerts.size());
        assertEquals(AlertStatus.TRIGGERED, triggeredAlerts.get(0).getStatus());
        verify(alertRepository).findByUserIdAndStatus(1L, AlertStatus.TRIGGERED);
    }

    @Test
    void testGetPendingAlerts() {
        when(alertRepository.findByStatus(AlertStatus.PENDING))
                .thenReturn(Arrays.asList(alert));

        List<Alert> pendingAlerts = alertService.getPendingAlerts();

        assertEquals(1, pendingAlerts.size());
        assertEquals(AlertStatus.PENDING, pendingAlerts.get(0).getStatus());
        verify(alertRepository).findByStatus(AlertStatus.PENDING);
    }
}
