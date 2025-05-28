package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.model.ApiResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CryptoService {

    private final String COINGECKO_URL = "https://api.coingecko.com/api/v3/coins/markets" +
            "?vs_currency=usd&order=market_cap_desc&per_page=250&page=1&x_cg_demo_api_key=CG-c7Eoq1PyYSu3r7Hg2YC6DmUC";

    public ResponseEntity<ApiResponse> fetchAllCryptos() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Object[]> response = restTemplate.getForEntity(COINGECKO_URL, Object[].class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok(new ApiResponse(true, "Cryptos fetched successfully", response.getBody()));
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(new ApiResponse(false, "Failed to fetch cryptos"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error fetching cryptos: " + e.getMessage()));
        }
    }
}
