package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.service.CryptoService;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {

    @Autowired
    private CryptoService cryptoService;

    @GetMapping("/getAllCryptos")
    public ResponseEntity<ApiResponse> getAllCryptos() {
        return cryptoService.fetchAllCryptos();
    }
}
