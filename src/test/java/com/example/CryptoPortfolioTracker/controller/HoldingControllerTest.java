package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.dto.AddHoldingRequest;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import com.example.CryptoPortfolioTracker.service.HoldingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HoldingControllerTest {

    @InjectMocks
    private HoldingController controller;

    @Mock
    private HoldingService holdingService;

    @Mock
    private UserRepository userRepository;

    private User testUser;
    private final Long userId = 1L;
    private final String validToken = "valid-token";
    private final String invalidToken = "invalid-token";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setSessionToken(validToken);
    }

    // ---------- getMyHoldings Tests ----------

    @Test
    void testGetMyHoldings_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(holdingService.getHoldingsByUserId(userId.intValue()))
                .thenReturn(ResponseEntity.ok(new ApiResponse(true, "Holdings fetched")));

        ResponseEntity<ApiResponse> response = controller.getMyHoldings(userId, validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
    }

    @Test
    void testGetMyHoldings_InvalidToken() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        ResponseEntity<ApiResponse> response = controller.getMyHoldings(userId, invalidToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isResult());
    }

    @Test
    void testGetMyHoldings_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = controller.getMyHoldings(userId, validToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isResult());
    }

    // ---------- addHolding Tests ----------

    @Test
    void testAddHolding_Success() {
        AddHoldingRequest request = new AddHoldingRequest();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(holdingService.addHolding(eq(testUser), eq(request), isNull()))
                .thenReturn(ResponseEntity.ok(new ApiResponse(true, "Added successfully")));

        ResponseEntity<ApiResponse> response = controller.addHolding(request, userId, validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
    }

    @Test
    void testAddHolding_Unauthorized() {
        AddHoldingRequest request = new AddHoldingRequest();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        ResponseEntity<ApiResponse> response = controller.addHolding(request, userId, invalidToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isResult());
    }

    // ---------- getMyNetValue Tests ----------

    @Test
    void testGetMyNetValue_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(holdingService.getMyNetValue(userId, null))
                .thenReturn(ResponseEntity.ok(new ApiResponse(true, "Net value calculated")));

        ResponseEntity<ApiResponse> response = controller.getMyNetValue(userId, validToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
    }

    @Test
    void testGetMyNetValue_InvalidToken() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        ResponseEntity<ApiResponse> response = controller.getMyNetValue(userId, invalidToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isResult());
    }

    @Test
    void testGetMyNetValue_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = controller.getMyNetValue(userId, validToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isResult());
    }
}
