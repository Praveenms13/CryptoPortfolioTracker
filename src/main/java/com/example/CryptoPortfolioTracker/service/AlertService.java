package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.dto.AlertRequestDTO;
import com.example.CryptoPortfolioTracker.entity.Alert;
import com.example.CryptoPortfolioTracker.entity.AlertStatus;
import com.example.CryptoPortfolioTracker.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    public Alert createAlert(AlertRequestDTO dto) {
        Alert alert = new Alert(
                dto.getUserId(),
                dto.getSymbol().toLowerCase(), // Store in lowercase for consistency
                dto.getTriggerPrice(),
                dto.getDirection()
        );
        return alertRepository.save(alert);
    }

    public Alert updateAlert(Long id, AlertRequestDTO updatedDto) {
        return alertRepository.findById(id).map(alert -> {
            alert.setSymbol(updatedDto.getSymbol().toLowerCase());
            alert.setTriggerPrice(updatedDto.getTriggerPrice());
            alert.setDirection(updatedDto.getDirection());
            alert.setStatus(AlertStatus.PENDING);
            alert.setTriggeredAt(null);
            return alertRepository.save(alert);
        }).orElseThrow(() -> new RuntimeException("Alert not found with id: " + id));
    }

    public Alert disableAlert(Long id) {
        return alertRepository.findById(id).map(alert -> {
            alert.setStatus(AlertStatus.DISABLED);
            return alertRepository.save(alert);
        }).orElseThrow(() -> new RuntimeException("Alert not found with id: " + id));
    }

    public void deleteAlert(Long id) {
        if (!alertRepository.existsById(id)) {
            throw new RuntimeException("Alert not found with id: " + id);
        }
        alertRepository.deleteById(id);
    }

    public List<Alert> getAlertsByUserId(Long userId) {
        return alertRepository.findByUserId(userId);
    }

    public List<Alert> getTriggeredAlerts(Long userId) {
        return alertRepository.findByUserIdAndStatus(userId, AlertStatus.TRIGGERED);
    }

    public List<Alert> getPendingAlerts() {
        return alertRepository.findByStatus(AlertStatus.PENDING);
}
}
