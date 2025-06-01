package com.example.CryptoPortfolioTracker.repository;

import com.example.CryptoPortfolioTracker.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
}
