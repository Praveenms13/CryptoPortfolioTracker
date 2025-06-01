package com.example.CryptoPortfolioTracker.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

import com.example.CryptoPortfolioTracker.dto.LoginRequest;
import com.example.CryptoPortfolioTracker.dto.SimpleUserResponse;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.enums.Role;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User adminUser;
    private User normalUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);
        adminUser.setSessionToken("admin-token");
        adminUser.setUsername("admin");

        normalUser = new User();
        normalUser.setId(2L);
        normalUser.setRole(Role.USER);
        normalUser.setUsername("user");
        normalUser.setName("User Name");
        normalUser.setEmail("user@example.com");
    }

    @Test
    void testGetAllUsers_Authorized() {
        when(userRepo.findBySessionToken("admin-token")).thenReturn(Optional.of(adminUser));
        when(userRepo.findAll()).thenReturn(List.of(adminUser, normalUser));

        ResponseEntity<ApiResponse> response = adminService.getAllUsers("admin-token");

        assertEquals(OK, response.getStatusCode());
        ApiResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isResult());
        assertEquals("All users fetched", body.getMessage());
    }

    @Test
    void testGetAllUsers_Unauthorized() {
        when(userRepo.findBySessionToken("invalid-token")).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = adminService.getAllUsers("invalid-token");

        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isResult());
    }

    @Test
    void testGetUserById_Success() {
        when(userRepo.findBySessionToken("admin-token")).thenReturn(Optional.of(adminUser));
        when(userRepo.findById(2L)).thenReturn(Optional.of(normalUser));

        ResponseEntity<ApiResponse> response = adminService.getUserById(2L, "admin-token");

        assertEquals(OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
    }

    @Test
    void testLoginAdmin_Success() {
        LoginRequest request = new LoginRequest("admin", "password");
        adminUser.setPassword("encoded-password");

        when(userRepo.findByUsernameOrEmail("admin", "admin")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("password", "encoded-password")).thenReturn(true);
        when(userRepo.save(any(User.class))).thenReturn(adminUser);

        ResponseEntity<ApiResponse> response = adminService.loginAdmin(request);

        assertEquals(OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
    }

    @Test
    void testLoginAdmin_InvalidPassword() {
        LoginRequest request = new LoginRequest("admin", "wrongpass");

        adminUser.setPassword("encoded-password");

        when(userRepo.findByUsernameOrEmail("admin", "admin")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("wrongpass", "encoded-password")).thenReturn(false);

        ResponseEntity<ApiResponse> response = adminService.loginAdmin(request);

        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isResult());
    }

    @Test
    void testUpdateAdmin_Success() {
        User updated = new User();
        updated.setUsername("newAdmin");
        updated.setEmail("admin@new.com");
        updated.setName("New Admin");
        updated.setPassword("newpass");

        when(userRepo.findBySessionToken("admin-token")).thenReturn(Optional.of(adminUser));
        when(userRepo.findById(1L)).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.encode("newpass")).thenReturn("hashed-pass");

        ResponseEntity<ApiResponse> response = adminService.updateAdmin(1L, updated, "admin-token");

        assertEquals(OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
    }

    @Test
    void testUpdateAdmin_Forbidden() {
        when(userRepo.findBySessionToken("admin-token")).thenReturn(Optional.of(adminUser));

        ResponseEntity<ApiResponse> response = adminService.updateAdmin(99L, new User(), "admin-token");

        assertEquals(FORBIDDEN, response.getStatusCode());
    }
}
