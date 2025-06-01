package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.dto.LoginRequest;
import com.example.CryptoPortfolioTracker.dto.RegisterRequest;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.enums.Role;
import com.example.CryptoPortfolioTracker.exception.ResourceNotFoundException;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void register_ShouldReturnBadRequestForInvalidEmail() {
        RegisterRequest request = new RegisterRequest("user1", "invalid-email", "pass123", "User One");

        ResponseEntity<ApiResponse> response = userService.register(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid Gmail address", response.getBody().getMessage());
    }

    @Test
    void register_ShouldReturnBadRequestForDuplicateUsername() {
        RegisterRequest request = new RegisterRequest("user1", "user1@gmail.com", "pass123", "User One");
        when(userRepo.findByUsername("user1")).thenReturn(Optional.of(new User()));

        ResponseEntity<ApiResponse> response = userService.register(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Username already exists", response.getBody().getMessage());
    }

    @Test
    void register_ShouldReturnBadRequestForDuplicateEmail() {
        RegisterRequest request = new RegisterRequest("user1", "user1@gmail.com", "pass123", "User One");
        when(userRepo.findByUsername("user1")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("user1@gmail.com")).thenReturn(Optional.of(new User()));

        ResponseEntity<ApiResponse> response = userService.register(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Email already registered", response.getBody().getMessage());
    }

    @Test
    void register_ShouldCreateUserSuccessfully() {
        RegisterRequest request = new RegisterRequest("user1", "user1@gmail.com", "pass123", "User One");
        User savedUser = new User();
        savedUser.setId(1L);

        when(userRepo.findByUsername("user1")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("user1@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("hashed");
        when(userRepo.save(any(User.class))).thenReturn(savedUser);

        ResponseEntity<ApiResponse> response = userService.register(request);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData());
    }


    @Test
    void loginUser_ShouldThrowExceptionIfUserNotFound() {
        LoginRequest request = new LoginRequest("user1", "pass123");
        when(userRepo.findByUsernameOrEmail("user1", "user1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.loginUser(request));
    }

    @Test
    void loginUser_ShouldReturnUnauthorizedIfRoleInvalid() {
        User user = new User();
        user.setRole(Role.ADMIN);
        when(userRepo.findByUsernameOrEmail("user1", "user1")).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest("user1", "pass123");
        ResponseEntity<ApiResponse> response = userService.loginUser(request);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid role", response.getBody().getMessage());
    }

    @Test
    void loginUser_ShouldReturnUnauthorizedIfPasswordInvalid() {
        User user = new User();
        user.setRole(Role.USER);
        user.setPassword("hashed");
        when(userRepo.findByUsernameOrEmail("user1", "user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "hashed")).thenReturn(false);

        LoginRequest request = new LoginRequest("user1", "pass123");
        ResponseEntity<ApiResponse> response = userService.loginUser(request);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid password", response.getBody().getMessage());
    }

    @Test
    void loginUser_ShouldReturnSuccessWithToken() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.USER);
        user.setUsername("user1");
        user.setPassword("hashed");

        when(userRepo.findByUsernameOrEmail("user1", "user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "hashed")).thenReturn(true);
        when(userRepo.save(any(User.class))).thenReturn(user);

        LoginRequest request = new LoginRequest("user1", "pass123");
        ResponseEntity<ApiResponse> response = userService.loginUser(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Login successful", response.getBody().getMessage());
        assertTrue(((Map<?, ?>) response.getBody().getData()).containsKey("token"));
    }

    @Test
    void updateUser_ShouldReturnUnauthorizedIfTokenInvalid() {
        when(userRepo.findBySessionToken("token123")).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = userService.updateUser(1L, new User(), "token123");

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid User", response.getBody().getMessage());
    }

    @Test
    void updateUser_ShouldReturnForbiddenIfDifferentUser() {
        User requester = new User();
        requester.setId(2L);
        requester.setRole(Role.USER);
        when(userRepo.findBySessionToken("token123")).thenReturn(Optional.of(requester));

        ResponseEntity<ApiResponse> response = userService.updateUser(1L, new User(), "token123");

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Forbidden: You can only update your own account", response.getBody().getMessage());
    }

    @Test
    void updateUser_ShouldReturnNotFoundIfUserMissing() {
        User requester = new User();
        requester.setId(1L);
        requester.setRole(Role.USER);
        when(userRepo.findBySessionToken("token123")).thenReturn(Optional.of(requester));
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = userService.updateUser(1L, new User(), "token123");

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void updateUser_ShouldUpdateAndReturnSuccess() {
        User requester = new User();
        requester.setId(1L);
        requester.setRole(Role.USER);

        User existing = new User();
        existing.setId(1L);

        User update = new User();
        update.setUsername("newUser");
        update.setPassword("newPass");
        update.setEmail("new@gmail.com");
        update.setName("New Name");

        when(userRepo.findBySessionToken("token123")).thenReturn(Optional.of(requester));
        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newPass")).thenReturn("hashedNewPass");

        ResponseEntity<ApiResponse> response = userService.updateUser(1L, update, "token123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User updated successfully", response.getBody().getMessage());
        verify(userRepo).save(any(User.class));
    }
}
