package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.dto.CryptoData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PriceServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private PriceService priceService;

    @BeforeEach
    void setup() {
        priceService = new PriceService(restTemplate);
    }

    @Test
    void getPrice_ShouldReturnPrice_WhenSymbolFound() {
        CryptoData bitcoin = new CryptoData();
        bitcoin.setSymbol("btc");
        bitcoin.setCurrentPrice(55000.0);

        CryptoData ethereum = new CryptoData();
        ethereum.setSymbol("eth");
        ethereum.setCurrentPrice(3500.0);

        List<CryptoData> cryptoList = Arrays.asList(bitcoin, ethereum);
        ResponseEntity<List<CryptoData>> responseEntity = ResponseEntity.ok(cryptoList);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<CryptoData>>>any()
        )).thenReturn(responseEntity);

        Double price = priceService.getPrice("btc");

        assertNotNull(price);
        assertEquals(55000.0, price);
    }

    @Test
    void getPrice_ShouldReturnNull_WhenSymbolNotFound() {
        CryptoData bitcoin = new CryptoData();
        bitcoin.setSymbol("btc");
        bitcoin.setCurrentPrice(55000.0);

        List<CryptoData> cryptoList = List.of(bitcoin);
        ResponseEntity<List<CryptoData>> responseEntity = ResponseEntity.ok(cryptoList);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<CryptoData>>>any()
        )).thenReturn(responseEntity);

        Double price = priceService.getPrice("doge");

        assertNull(price);
    }

    @Test
    void getPrice_ShouldReturnNull_WhenApiThrowsException() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<CryptoData>>>any()
        )).thenThrow(new RuntimeException("API is down"));

        Double price = priceService.getPrice("btc");

        assertNull(price);
    }
}
