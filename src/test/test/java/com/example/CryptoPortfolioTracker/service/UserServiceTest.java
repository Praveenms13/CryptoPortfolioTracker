package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserById_UserExists() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepo, times(1)).findById(1L);
    }

    @Test
    public void testGetUserById_UserNotFound() {
        when(userRepo.findById(2L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(2L);

        assertFalse(result.isPresent());
        verify(userRepo, times(1)).findById(2L);
    }
    @Test
    public void testUpdateUser_UserExists() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("olduser");
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");

        User updatedUser = new User();
        updatedUser.setUsername("newuser");
        updatedUser.setName("New Name");
        updatedUser.setEmail("new@example.com");

        when(userRepo.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepo.save(any(User.class))).thenReturn(existingUser);

        ResponseEntity<ApiResponse> response = userService.updateUser(1L, updatedUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
        assertEquals("User updated", response.getBody().getMessage());

        verify(userRepo).save(existingUser);
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        User updatedUser = new User();
        updatedUser.setUsername("newuser");

        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse> response = userService.updateUser(1L, updatedUser);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isResult());
        assertEquals("User not found", response.getBody().getMessage());
    }
}
