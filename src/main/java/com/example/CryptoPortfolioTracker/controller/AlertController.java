package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.dto.AlertRequestDTO;
import com.example.CryptoPortfolioTracker.entity.Alert;
import com.example.CryptoPortfolioTracker.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alert")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @PostMapping("/create")
    public ResponseEntity<Alert> createAlert(@RequestBody AlertRequestDTO request) {
        try {
            Alert alert = alertService.createAlert(request);
            return ResponseEntity.ok(alert);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Alert>> getUserAlerts(@PathVariable Long userId) {
        List<Alert> alerts = alertService.getAlertsByUserId(userId);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/user/{userId}/triggered")
    public ResponseEntity<List<Alert>> getTriggeredAlerts(@PathVariable Long userId) {
        List<Alert> alerts = alertService.getTriggeredAlerts(userId);
        return ResponseEntity.ok(alerts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alert> updateAlert(@PathVariable Long id, @RequestBody AlertRequestDTO updatedData) {
        try {
            Alert alert = alertService.updateAlert(id, updatedData);
            return ResponseEntity.ok(alert);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        try {
            alertService.deleteAlert(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<Alert> disableAlert(@PathVariable Long id) {
        try {
            Alert alert = alertService.disableAlert(id);
            return ResponseEntity.ok(alert);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
 }
}
}
