package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CryptoControllerToGetPriceSymbolTest {

    @InjectMocks
    private CryptoControllerToGetPriceSymbol controller;

    @Mock
    private PriceService priceService;

    private final String symbol = "BTC";

    @BeforeEach
    void setUp() {
        // Setup if needed
    }

    @Test
    void testGetPrice_Success() {
        when(priceService.getPrice(symbol)).thenReturn(50000.0);

        ResponseEntity<?> response = controller.getPrice(symbol);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(50000.0, response.getBody());
    }

    @Test
    void testGetPrice_SymbolNotFound() {
        when(priceService.getPrice(symbol)).thenReturn(null);

        ResponseEntity<?> response = controller.getPrice(symbol);

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Symbol not found"));
    }

    @Test
    void testGetPrice_InternalServerError() {
        when(priceService.getPrice(symbol)).thenThrow(new RuntimeException("API down"));

        ResponseEntity<?> response = controller.getPrice(symbol);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error fetching price"));
    }
}
