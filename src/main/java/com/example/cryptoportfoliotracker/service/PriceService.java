package com.example.cryptoportfoliotracker.service;

import com.example.cryptoportfoliotracker.dto.PriceResponse;
import com.example.cryptoportfoliotracker.dto.CryptoData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PriceService {
    private final RestTemplate restTemplate = new RestTemplate();

    // External API endpoint - Navya's API
    private static final String EXTERNAL_API_URL = "http://10.9.151.102:8080/api/crypto/getAllCryptos";

    public Double getPrice(String symbol) {
        try {
            PriceResponse response = restTemplate.getForObject(EXTERNAL_API_URL, PriceResponse.class);

            if (response != null && response.isResult()) {
                List<CryptoData> cryptos = response.getData();

                for (CryptoData crypto : cryptos) {
                    if (crypto.getSymbol().equalsIgnoreCase(symbol.trim())) {
                        return crypto.getCurrentPrice();
                    }
                }
            }

            System.out.println("Symbol not found in API response: " + symbol);
            return null;

        } catch (Exception e) {
            System.err.println("Error fetching price for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
}
