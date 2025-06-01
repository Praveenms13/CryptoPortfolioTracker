package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CryptoService {

    private final String COINGECKO_URL = "https://api.coingecko.com/api/v3/coins/markets" +
            "?vs_currency=usd&order=market_cap_desc&per_page=250&page=1&x_cg_demo_api_key=CG-c7Eoq1PyYSu3r7Hg2YC6DmUC";

    private final RestTemplate restTemplate;

    @Autowired
    public CryptoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected ResponseEntity<Object[]> callRestTemplate() {
        return restTemplate.getForEntity(COINGECKO_URL, Object[].class);
    }

    public ResponseEntity<ApiResponse> fetchAllCryptos() {
        try {
            ResponseEntity<Object[]> response = callRestTemplate();

            HttpStatus statusCode = HttpStatus.valueOf(response.getStatusCode().value());

            if (statusCode == HttpStatus.OK || statusCode == HttpStatus.NOT_MODIFIED) {
                return ResponseEntity.ok(new ApiResponse(
                        true,
                        statusCode == HttpStatus.OK ? "Cryptos fetched successfully" : "No update - using cached data",
                        statusCode == HttpStatus.OK ? response.getBody() : null
                ));
            } else {
                return ResponseEntity.status(statusCode)
                        .body(new ApiResponse(false, "Failed to fetch cryptos"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error fetching cryptos: " + e.getMessage()));
        }
    }
}
