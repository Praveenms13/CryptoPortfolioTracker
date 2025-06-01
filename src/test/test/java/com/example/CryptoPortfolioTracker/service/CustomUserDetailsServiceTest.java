package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService service;

    @Mock
    private UserRepository userRepository;

    @Test
    void loadUserByUsername_UserFound() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPass");

        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = service.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());

        verify(userRepository).findByUsernameOrEmail("testuser", "testuser");
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        when(userRepository.findByUsernameOrEmail("unknown", "unknown"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("unknown"));

        assertEquals("User not found: unknown", exception.getMessage());

        verify(userRepository).findByUsernameOrEmail("unknown", "unknown");
    }
}
