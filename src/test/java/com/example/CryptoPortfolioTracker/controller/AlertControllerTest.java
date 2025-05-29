package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.dto.AlertRequestDTO;
import com.example.CryptoPortfolioTracker.entity.Alert;
import com.example.CryptoPortfolioTracker.enums.AlertDirection;
import com.example.CryptoPortfolioTracker.enums.AlertStatus;
import com.example.CryptoPortfolioTracker.service.AlertService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertControllerTest {

    @InjectMocks
    private AlertController alertController;

    @Mock
    private AlertService alertService;

    private Alert sampleAlert;
    private AlertRequestDTO alertRequestDTO;

    @BeforeEach
    void setUp() {
        alertRequestDTO = new AlertRequestDTO();
        alertRequestDTO.setUserId(1L);
        alertRequestDTO.setSymbol("BTC");
        alertRequestDTO.setTriggerPrice(50000.0);
        alertRequestDTO.setDirection(AlertDirection.ABOVE);

        sampleAlert = new Alert();
        sampleAlert.setId(1L);
        sampleAlert.setUserId(1L);
        sampleAlert.setSymbol("BTC");
        sampleAlert.setTriggerPrice(50000.0);
        sampleAlert.setDirection(AlertDirection.ABOVE);
        sampleAlert.setStatus(AlertStatus.PENDING);
    }

    @Test
    void testCreateAlert_Success() {
        when(alertService.createAlert(alertRequestDTO)).thenReturn(sampleAlert);

        ResponseEntity<Alert> response = alertController.createAlert(alertRequestDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("BTC", response.getBody().getSymbol());
    }

    @Test
    void testCreateAlert_Failure() {
        when(alertService.createAlert(alertRequestDTO)).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<Alert> response = alertController.createAlert(alertRequestDTO);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testGetUserAlerts() {
        when(alertService.getAlertsByUserId(1L)).thenReturn(Arrays.asList(sampleAlert));

        ResponseEntity<List<Alert>> response = alertController.getUserAlerts(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
        assertEquals("BTC", response.getBody().get(0).getSymbol());
    }

    @Test
    void testGetTriggeredAlerts() {
        sampleAlert.setStatus(AlertStatus.TRIGGERED);
        when(alertService.getTriggeredAlerts(1L)).thenReturn(List.of(sampleAlert));

        ResponseEntity<List<Alert>> response = alertController.getTriggeredAlerts(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(AlertStatus.TRIGGERED, response.getBody().get(0).getStatus());
    }

    @Test
    void testUpdateAlert_Success() {
        when(alertService.updateAlert(eq(1L), any(AlertRequestDTO.class))).thenReturn(sampleAlert);

        ResponseEntity<Alert> response = alertController.updateAlert(1L, alertRequestDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("BTC", response.getBody().getSymbol());
    }

    @Test
    void testUpdateAlert_NotFound() {
        when(alertService.updateAlert(eq(1L), any(AlertRequestDTO.class)))
                .thenThrow(new RuntimeException("Not found"));

        ResponseEntity<Alert> response = alertController.updateAlert(1L, alertRequestDTO);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDeleteAlert_Success() {
        doNothing().when(alertService).deleteAlert(1L);

        ResponseEntity<Void> response = alertController.deleteAlert(1L);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testDeleteAlert_NotFound() {
        doThrow(new RuntimeException("Not found")).when(alertService).deleteAlert(1L);

        ResponseEntity<Void> response = alertController.deleteAlert(1L);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDisableAlert_Success() {
        sampleAlert.setStatus(AlertStatus.DISABLED);
        when(alertService.disableAlert(1L)).thenReturn(sampleAlert);

        ResponseEntity<Alert> response = alertController.disableAlert(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(AlertStatus.DISABLED, response.getBody().getStatus());
    }

    @Test
    void testDisableAlert_NotFound() {
        when(alertService.disableAlert(1L)).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<Alert> response = alertController.disableAlert(1L);

        assertEquals(404, response.getStatusCodeValue());
    }
}
