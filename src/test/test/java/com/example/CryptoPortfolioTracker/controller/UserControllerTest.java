package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.dto.LoginRequest;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import com.example.CryptoPortfolioTracker.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController controller;

    @Mock
    private UserRepository userRepo;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private final Long userId = 1L;
    private final String token = UUID.randomUUID().toString();
    private final String bearerToken = "Bearer " + token;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("john");
        testUser.setEmail("john@example.com");
        testUser.setPassword("encoded-password");
        testUser.setSessionToken(token);
    }

    // ---------- LOGIN TESTS ----------

    @Test
    void testLogin_Success() {
        Map<String, String> request = Map.of("username", "john", "password", "1234");

        when(userRepo.findByUsernameOrEmail("john", "john")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("1234", "encoded-password")).thenReturn(true);
        when(userRepo.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<ApiResponse> response = controller.login((LoginRequest) request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
        assertNotNull(((Map<?, ?>) response.getBody().getData()).get("token"));
    }

    @Test
    void testLogin_InvalidCredentials() {
        Map<String, String> request = Map.of("username", "john", "password", "wrong");

        when(userRepo.findByUsernameOrEmail("john", "john")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong", "encoded-password")).thenReturn(false);

        ResponseEntity<ApiResponse> response = controller.login((LoginRequest) request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isResult());
    }

    // ---------- GET LOGGED-IN USER ID ----------

    @Test
    void testGetLoggedInUserId_Success() {
        when(userRepo.findBySessionToken(token)).thenReturn(Optional.of(testUser));

        ResponseEntity<ApiResponse> response = controller.getLoggedInUserId(bearerToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
        assertEquals(userId, response.getBody().getData());
    }

    @Test
    void testGetLoggedInUserId_InvalidToken() {
        when(userRepo.findBySessionToken(token)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = controller.getLoggedInUserId(bearerToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isResult());
    }

    // ---------- GET USER BY ID ----------

    @Test
    void testGetUserById_Success() {
        when(userRepo.findBySessionToken(token)).thenReturn(Optional.of(testUser));
        when(userRepo.findById(userId)).thenReturn(Optional.of(testUser));

        ResponseEntity<ApiResponse> response = controller.getUserById(userId, bearerToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
        assertEquals(testUser, response.getBody().getData());
    }

    @Test
    void testGetUserById_UnauthorizedAccess() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        when(userRepo.findBySessionToken(token)).thenReturn(Optional.of(testUser));
        when(userRepo.findById(2L)).thenReturn(Optional.of(anotherUser));

        ResponseEntity<ApiResponse> response = controller.getUserById(2L, bearerToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isResult());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepo.findBySessionToken(token)).thenReturn(Optional.of(testUser));
        when(userRepo.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = controller.getUserById(99L, bearerToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isResult());
    }

    // ---------- UPDATE USER ----------

    @Test
    void testUpdateUser_Success() {
        User updatedData = new User();
        updatedData.setUsername("johnny");
        updatedData.setEmail("johnny@example.com");
        updatedData.setPassword("newpass");
        updatedData.setName("Johnny");

        when(userRepo.findBySessionToken(token)).thenReturn(Optional.of(testUser));
        when(userRepo.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newpass")).thenReturn("encoded-newpass");

        ResponseEntity<ApiResponse> response = controller.updateUser(userId, updatedData, bearerToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void testUpdateUser_Unauthorized() {
        when(userRepo.findBySessionToken(token)).thenReturn(Optional.of(testUser));

        ResponseEntity<ApiResponse> response = controller.updateUser(99L, new User(), bearerToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isResult());
    }

    @Test
    void testUpdateUser_NotFound() {
        when(userRepo.findBySessionToken(token)).thenReturn(Optional.of(testUser));
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = controller.updateUser(userId, new User(), bearerToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isResult());
    }
}
