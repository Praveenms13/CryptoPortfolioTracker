package com.example.CryptoPortfolioTracker.repository;

import com.example.CryptoPortfolioTracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<Object> findByUsername(String username);

    Optional<Object> findByEmail(String email);
}
