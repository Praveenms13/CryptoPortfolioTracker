package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.dto.LoginRequest;
import com.example.CryptoPortfolioTracker.dto.RegisterRequest;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.service.AuthService;
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
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setup() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testUser ");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("testPassword");

        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("test@example.com");
        loginRequest.setPassword("testPassword");
    }

    @Test
    void testRegister() {
        ApiResponse expectedResponse = new ApiResponse(true, "Registration successful", null);
        when(authService.register(registerRequest)).thenReturn(ResponseEntity.ok(expectedResponse));

        ApiResponse actualResponse = authController.register(registerRequest);

        assertNotNull(actualResponse);
        assertTrue(actualResponse.isResult());
        assertEquals("Registration successful", actualResponse.getMessage());
    }

    @Test
    void testLogin() {
        ApiResponse expectedResponse = new ApiResponse(true, "Login successful", null);
        when(authService.login(loginRequest)).thenReturn(ResponseEntity.ok(expectedResponse));

        ApiResponse actualResponse = authController.login(loginRequest);

        assertNotNull(actualResponse);
        assertTrue(actualResponse.isResult());
        assertEquals("Login successful", actualResponse.getMessage());
    }
}
