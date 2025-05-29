package com.example.CryptoPortfolioTracker.service;

import static org.junit.jupiter.api.Assertions.*;


import com.example.CryptoPortfolioTracker.dto.AddHoldingRequest;
import com.example.CryptoPortfolioTracker.entity.Holding;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.HoldingRepository;
import com.example.CryptoPortfolioTracker.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HoldingServiceTest {

    @InjectMocks
    private HoldingService holdingService;

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void testGetHoldingsByUserId_ReturnsHoldings() {
        int userId = 1;
        List<Holding> mockHoldings = List.of(new Holding());
        when(holdingRepository.findByUserIdOrderByBoughtDateDesc(userId)).thenReturn(mockHoldings);

        ResponseEntity<ApiResponse> response = holdingService.getHoldingsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
        assertEquals("Holdings fetched successfully", response.getBody().getMessage());
    }

    @Test
    void testAddHolding_Successful() {
        User user = new User();
        AddHoldingRequest request = new AddHoldingRequest();
        request.setCoinId("bitcoin");
        request.setQuantity(2);

        String token = "test-token";

        Map<String, Object> coin = new HashMap<>();
        coin.put("id", "bitcoin");
        coin.put("name", "Bitcoin");
        coin.put("symbol", "BTC");
        coin.put("current_price", 30000.0);

        List<Map<String, Object>> coinList = List.of(coin);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("result", true);
        responseMap.put("data", coinList);

        ResponseEntity<Map> mockResponse = new ResponseEntity<>(responseMap, HttpStatus.OK);

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (restTemplate, context) -> when(restTemplate.exchange(
                        eq("http://localhost:8080/api/crypto/getAllCryptos"),
                        eq(HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(Map.class)
                )).thenReturn(mockResponse))) {

            ResponseEntity<ApiResponse> response = holdingService.addHolding(user, request, token);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isResult());
            assertEquals("Holding added successfully", response.getBody().getMessage());
        }
    }

    @Test
    void testGetMyNetValue_ReturnsNetValue() {
        long userId = 1L;
        String token = "test-token";

        Holding h1 = new Holding();
        h1.setCoinId("bitcoin");
        h1.setCoinSymbol("BTC");
        h1.setCoinName("Bitcoin");
        h1.setCoinQuantity(2);
        h1.setBoughtPrice(25000L);

        when(holdingRepository.findByUserIdOrderByBoughtDateDesc((int) userId)).thenReturn(List.of(h1));

        Map<String, Object> coin = new HashMap<>();
        coin.put("id", "bitcoin");
        coin.put("current_price", 30000.0);

        List<Map<String, Object>> coinList = List.of(coin);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("result", true);
        responseMap.put("data", coinList);

        ResponseEntity<Map> mockResponse = new ResponseEntity<>(responseMap, HttpStatus.OK);

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (restTemplate, context) -> when(restTemplate.exchange(
                        eq("http://localhost:8080/api/crypto/getAllCryptos"),
                        eq(HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(Map.class)
                )).thenReturn(mockResponse))) {

            ResponseEntity<ApiResponse> response = holdingService.getMyNetValue(userId, token);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isResult());
            assertEquals("Net value calculated successfully", response.getBody().getMessage());
        }
    }
}
