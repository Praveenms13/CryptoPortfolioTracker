package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.dto.LoginRequest;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import com.example.CryptoPortfolioTracker.service.AdminService;

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
class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private AdminService adminService;

    @Mock
    private UserRepository userRepository;

    private LoginRequest loginRequest;

    @BeforeEach
    void setup() {
        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("admin@example.com");
        loginRequest.setPassword("adminpass");
    }

    @Test
    void testLogin() {
        ApiResponse expectedResponse = new ApiResponse(true, "Login successful", null);
        when(adminService.login(loginRequest)).thenReturn(ResponseEntity.ok(expectedResponse));

        ApiResponse actualResponse = adminController.login(loginRequest);

        assertNotNull(actualResponse);
        assertTrue(actualResponse.isResult());
        assertEquals("Login successful", actualResponse.getMessage());
    }

    @Test
    void testGetAllUsers() {
        ApiResponse expectedResponse = new ApiResponse(true, "All users fetched", null);
        when(adminService.getAllUsers()).thenReturn(ResponseEntity.ok(expectedResponse));

        ApiResponse actualResponse = adminController.getAllUsers();

        assertNotNull(actualResponse);
        assertTrue(actualResponse.isResult());
        assertEquals("All users fetched", actualResponse.getMessage());
    }

    @Test
    void testGetUserById() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("user1");

        ApiResponse expectedResponse = new ApiResponse(true, "User found", mockUser);
        when(adminService.getUserById(userId)).thenReturn(ResponseEntity.ok(expectedResponse));

        ApiResponse actualResponse = adminController.getUserById(userId);

        assertNotNull(actualResponse);
        assertTrue(actualResponse.isResult());
        assertEquals("User found", actualResponse.getMessage());

        User returnedUser = (User) actualResponse.getData();
        assertEquals(userId, returnedUser.getId());
        assertEquals("user1", returnedUser.getUsername());
    }
}
