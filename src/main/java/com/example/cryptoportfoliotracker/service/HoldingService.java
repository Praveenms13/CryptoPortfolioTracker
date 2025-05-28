package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.dto.AddHoldingRequest;
import com.example.CryptoPortfolioTracker.entity.Holding;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.HoldingRepository;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class HoldingService {

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<ApiResponse> getHoldingsByUserId(int userId) {
        try {
            List<Holding> holdings = holdingRepository.findByUserIdOrderByBoughtDateDesc(userId);

            if (holdings.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse(true, "No holdings found for this user", holdings));
            }

            return ResponseEntity.ok(new ApiResponse(true, "Holdings fetched successfully", holdings));

        } catch (Exception e) {
            System.err.println("Error fetching holdings: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error fetching holdings"));
        }
    }

    public ResponseEntity<ApiResponse> addHolding(User user, AddHoldingRequest request) {
        try {
            Holding holding = new Holding();
            holding.setUser(user);
            holding.setCoinId(request.getCoinId());
            holding.setCoinName(request.getCoinName());
            holding.setCoinSymbol(request.getCoinSymbol());
            holding.setCoinQuantity(request.getQuantity());
            holding.setBoughtPrice(request.getBoughtPrice().longValue());
            holding.setBoughtDate(LocalDateTime.now());
            holding.setPricealert(null);

            holdingRepository.save(holding);

            return ResponseEntity.ok(new ApiResponse(true, "Holding added successfully", holding));
        } catch (Exception e) {
            System.err.println("Error adding holding: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error adding holding"));
        }
    }


    public ResponseEntity<ApiResponse> getMyNetValue(Long userId, String token) {
        try {
            List<Holding> holdings = holdingRepository.findByUserIdOrderByBoughtDateDesc(Math.toIntExact(userId));
            if (holdings.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse(true, "No holdings found", 0));
            }


            Map<String, AggregatedHolding> coinAggregates = new HashMap<>();
            for (Holding h : holdings) {
                coinAggregates.compute(h.getCoinId(), (k, v) -> {
                    if (v == null) {
                        v = new AggregatedHolding(h.getCoinId(), h.getCoinSymbol(), h.getCoinName(), 0.0, 0.0);
                    }
                    v.totalQuantity += h.getCoinQuantity();
                    v.totalInvestment += h.getBoughtPrice() * h.getCoinQuantity();
                    return v;
                });
            }


            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "http://localhost:8080/api/crypto/getAllCryptos";
            // ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token); // Adds "Authorization: Bearer <token>"
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );


            if (!response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(new ApiResponse(false, "Failed to fetch crypto prices"));
            }

            List<Map<String, Object>> cryptoData = (List<Map<String, Object>>) response.getBody().get("data");


            double totalValue = 0.0;
            List<Map<String, Object>> resultList = new ArrayList<>();

            for (AggregatedHolding agg : coinAggregates.values()) {
                Optional<Map<String, Object>> cryptoOpt = cryptoData.stream()
                        .filter(c -> c.get("id").equals(agg.coinId))
                        .findFirst();

                if (cryptoOpt.isPresent()) {
                    Map<String, Object> crypto = cryptoOpt.get();
                    double currentPrice = ((Number) crypto.get("current_price")).doubleValue();
                    double currentValue = currentPrice * agg.totalQuantity;
                    double profitLoss = currentValue - agg.totalInvestment;

                    Map<String, Object> entry = new HashMap<>();
                    entry.put("coin_id", agg.coinId);
                    entry.put("coin_name", agg.coinName);
                    entry.put("coin_symbol", agg.coinSymbol);
                    entry.put("quantity", agg.totalQuantity);
                    entry.put("invested", agg.totalInvestment);
                    entry.put("current_value", currentValue);
                    entry.put("profit_loss", profitLoss);

                    totalValue += currentValue;
                    resultList.add(entry);
                }
            }

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("total_portfolio_value", totalValue);
            responseMap.put("holdings", resultList);

            return ResponseEntity.ok(new ApiResponse(true, "Net value calculated successfully", responseMap));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error calculating net value "+e.getMessage()));
        }
    }


    private static class AggregatedHolding {
        String coinId;
        String coinSymbol;
        String coinName;
        double totalQuantity;
        double totalInvestment;

        AggregatedHolding(String coinId, String coinSymbol, String coinName, double totalQuantity, double totalInvestment) {
            this.coinId = coinId;
            this.coinSymbol = coinSymbol;
            this.coinName = coinName;
            this.totalQuantity = totalQuantity;
            this.totalInvestment = totalInvestment;
        }
    }

}