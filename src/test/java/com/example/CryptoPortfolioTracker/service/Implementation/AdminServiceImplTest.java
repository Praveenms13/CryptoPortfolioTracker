package com.example.CryptoPortfolioTracker.service.Implementation;

import com.example.CryptoPortfolioTracker.dto.ClientResponse;
import com.example.CryptoPortfolioTracker.dto.LoginRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder encoder;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
    }

    @Test
    void testGetAllUsers() {
        when(userRepo.findAll()).thenReturn(Arrays.asList(user));

        ResponseEntity<ApiResponse> response = adminService.getAllUsers();

        assertNotNull(response);
        assertTrue(response.getBody().isResult());
        assertEquals("All users fetched", response.getBody().getMessage());

        List<ClientResponse> data = (List<ClientResponse>) response.getBody().getData();
        assertEquals(1, data.size());
        assertEquals(user.getUsername(), data.get(0).getUsername());
    }

    @Test
    void testGetUserById_UserNotFound() {
        Long userId = 1L;
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = adminService.getUserById(userId);

        assertEquals(404, response.getStatusCodeValue());
        assertFalse(response.getBody().isResult());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void testGetUserById_NotRegularUser() {
        user.setRole(Role.ADMIN);
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse> response = adminService.getUserById(user.getId());

        assertEquals(403, response.getStatusCodeValue());
        assertFalse(response.getBody().isResult());
        assertEquals("Not a regular user", response.getBody().getMessage());
    }

    @Test
    void testGetUserById_Success() {
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));

        ResponseEntity<ApiResponse> response = adminService.getUserById(user.getId());

        assertNotNull(response);
        assertTrue(response.getBody().isResult());
        assertEquals("User fetched", response.getBody().getMessage());

        ClientResponse client = (ClientResponse) response.getBody().getData();
        assertEquals(user.getId(), client.getId());
        assertEquals(user.getUsername(), client.getUsername());
        assertEquals(user.getEmail(), client.getEmail());
        assertEquals(user.getName(), client.getName());
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("test@example.com");
        loginRequest.setPassword("password");

        when(userRepo.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()))
                .thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = adminService.login(loginRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isResult());
        assertEquals("Invalid credentials", response.getBody().getMessage());
    }

    @Test
    void testLogin_InvalidPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("test@example.com");
        loginRequest.setPassword("wrongPassword");

        when(userRepo.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(encoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        ResponseEntity<ApiResponse> response = adminService.login(loginRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isResult());
        assertEquals("Invalid credentials", response.getBody().getMessage());
    }

    @Test
    void testLogin_AccessDenied() {
        user.setRole(Role.USER);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("test@example.com");
        loginRequest.setPassword("encodedPassword");

        when(userRepo.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(encoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

        ResponseEntity<ApiResponse> response = adminService.login(loginRequest);

        assertEquals(403, response.getStatusCodeValue());
        assertFalse(response.getBody().isResult());
        assertEquals("Access denied: Only ADMIN role is allowed", response.getBody().getMessage());
    }

    @Test
    void testLogin_Success() {
        user.setRole(Role.ADMIN);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("test@example.com");
        loginRequest.setPassword("encodedPassword");

        when(userRepo.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(encoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

        ResponseEntity<ApiResponse> response = adminService.login(loginRequest);

        assertNotNull(response);
        assertTrue(response.getBody().isResult());
        assertEquals("Login successful", response.getBody().getMessage());
    }
}
