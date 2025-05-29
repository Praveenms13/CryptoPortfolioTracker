package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.dto.CryptoData;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PriceService {
    private final RestTemplate restTemplate = new RestTemplate();

    // External API endpoint - CoinGecko
    private static final String EXTERNAL_API_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=250&page=1&x_cg_demo_api_key=CG-c7Eoq1PyYSu3r7Hg2YC6DmUC";

    public Double getPrice(String symbol) {
        try {
            ResponseEntity<List<CryptoData>> response = restTemplate.exchange(
                    EXTERNAL_API_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CryptoData>>() {}
            );

            List<CryptoData> cryptos = response.getBody();

            if (cryptos != null) {
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
