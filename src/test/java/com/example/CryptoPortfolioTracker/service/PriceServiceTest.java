package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.dto.CryptoData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private PriceService priceService;

    private List<CryptoData> cryptoList;

    @BeforeEach
    void setUp() throws Exception {
        priceService = new PriceService();

        // Inject the mocked RestTemplate into the private final field via reflection
        Field restTemplateField = PriceService.class.getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        restTemplateField.set(priceService, restTemplate);

        // Prepare some sample crypto data
        CryptoData bitcoin = new CryptoData();
        bitcoin.setSymbol("btc");
        bitcoin.setCurrentPrice(30000.0);

        CryptoData ethereum = new CryptoData();
        ethereum.setSymbol("eth");
        ethereum.setCurrentPrice(2000.0);

        cryptoList = Arrays.asList(bitcoin, ethereum);
    }

    @Test
    void testGetPrice_Found() {
        ResponseEntity<List<CryptoData>> responseEntity = new ResponseEntity<>(cryptoList, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class))
        ).thenReturn(responseEntity);

        Double price = priceService.getPrice("btc");

        assertNotNull(price);
        assertEquals(30000.0, price);
    }

    @Test
    void testGetPrice_NotFound() {
        ResponseEntity<List<CryptoData>> responseEntity = new ResponseEntity<>(cryptoList, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class))
        ).thenReturn(responseEntity);

        Double price = priceService.getPrice("doge"); // not in list

        assertNull(price);
    }

    @Test
    void testGetPrice_ExceptionHandling() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class))
        ).thenThrow(new RuntimeException("API down"));

        Double price = priceService.getPrice("btc");

        assertNull(price);
    }
}
