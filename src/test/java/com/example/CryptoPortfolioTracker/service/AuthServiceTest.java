package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.dto.LoginRequest;
import com.example.CryptoPortfolioTracker.dto.RegisterRequest;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.enums.Role;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/*
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder encoder;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
    }

    @Test
    void testRegister_Success() {
        when(userRepo.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepo.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(encoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

        ResponseEntity<ApiResponse> response = authService.register(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isResult());
        assertEquals("User registered successfully", response.getBody().getMessage());

        verify(userRepo).save(any(User.class));
    }

    @Test
    void testRegister_UsernameExists() {
        when(userRepo.findByUsername(registerRequest.getUsername())).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse> response = authService.register(registerRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertFalse(response.getBody().isResult());
        assertEquals("Username already exists", response.getBody().getMessage());

        verify(userRepo, never()).save(any());
    }

    @Test
    void testRegister_EmailExists() {
        when(userRepo.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepo.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse> response = authService.register(registerRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertFalse(response.getBody().isResult());
        assertEquals("Email already exists", response.getBody().getMessage());

        verify(userRepo, never()).save(any());
    }

    @Test
    void testLogin_Success() {
        when(userRepo.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(encoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

        ResponseEntity<ApiResponse> response = authService.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
        assertEquals("Login successful", response.getBody().getMessage());
    }

    @Test
    void testLogin_AccessDeniedForNonUserRole() {
        user.setRole(Role.ADMIN);
        when(userRepo.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(encoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

        ResponseEntity<ApiResponse> response = authService.login(loginRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(response.getBody().isResult());
        assertEquals("Access denied: Only USER role is allowed", response.getBody().getMessage());
    }

    @Test
    void testLogin_InvalidCredentials() {
        when(userRepo.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()))
                .thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = authService.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isResult());
        assertEquals("Invalid credentials", response.getBody().getMessage());
    }

    @Test
    void testLogin_InvalidPassword() {
        when(userRepo.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(encoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        ResponseEntity<ApiResponse> response = authService.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isResult());
        assertEquals("Invalid credentials", response.getBody().getMessage());
    }
}

 */
