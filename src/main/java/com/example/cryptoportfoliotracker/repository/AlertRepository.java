package com.example.cryptoportfoliotracker.repository;

import com.example.cryptoportfoliotracker.entity.Alert;
import com.example.cryptoportfoliotracker.entity.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByUserId(Long userId);
    List<Alert> findByUserIdAndStatus(Long userId, AlertStatus status);
    List<Alert> findByStatus(AlertStatus pending);
}