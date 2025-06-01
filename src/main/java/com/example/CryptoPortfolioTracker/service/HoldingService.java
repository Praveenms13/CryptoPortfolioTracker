package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.dto.AddHoldingRequest;
import com.example.CryptoPortfolioTracker.dto.HoldingDTO;
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
import java.util.stream.Collectors;

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
                return ResponseEntity.ok(new ApiResponse(true, "No holdings found for this user", Collections.emptyList()));
            }

            List<HoldingDTO> holdingDTOs = holdings.stream()
                    .map(HoldingDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse(true, "Holdings fetched successfully", holdingDTOs));

        } catch (Exception e) {
            System.err.println("Error fetching holdings: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error fetching holdings"));
        }
    }


    public ResponseEntity<ApiResponse> addHolding(User user, AddHoldingRequest request, String token) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "http://localhost:8080/api/crypto/getAllCryptos",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() != HttpStatus.OK || !(Boolean) response.getBody().get("result")) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(new ApiResponse(false, "Failed to fetch crypto prices"));
            }

            List<Map<String, Object>> cryptoData = (List<Map<String, Object>>) response.getBody().get("data");

            Map<String, Object> matchedCoin = cryptoData.stream()
                    .filter(coin -> request.getCoinId().equalsIgnoreCase((String) coin.get("id")))
                    .findFirst()
                    .orElse(null);

            if (matchedCoin == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(false, "Invalid coinId: " + request.getCoinId()));
            }

            double currentPrice = Double.parseDouble(matchedCoin.get("current_price").toString());
            double quantity = request.getQuantity();

            Holding holding = new Holding();
            holding.setUser(user);
            holding.setCoinId((String) matchedCoin.get("id"));
            holding.setCoinName((String) matchedCoin.get("name"));
            holding.setCoinSymbol((String) matchedCoin.get("symbol"));
            holding.setCoinQuantity((int) quantity);
            holding.setBoughtPrice((long) currentPrice);
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
            Map<String, Double> coinQuantityMap = new HashMap<>();
            Map<String, Double> coinInvestmentMap = new HashMap<>();
            Map<String, String> coinSymbolMap = new HashMap<>();
            Map<String, String> coinNameMap = new HashMap<>();
            for (Holding holding : holdings) {
                String coinId = holding.getCoinId();
                double quantity = holding.getCoinQuantity();
                double investment = holding.getBoughtPrice() * holding.getCoinQuantity();
                coinQuantityMap.put(coinId, coinQuantityMap.getOrDefault(coinId, 0.0) + quantity);
                coinInvestmentMap.put(coinId, coinInvestmentMap.getOrDefault(coinId, 0.0) + investment);


                coinSymbolMap.put(coinId, holding.getCoinSymbol());
                coinNameMap.put(coinId, holding.getCoinName());
            }

            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "http://localhost:8080/api/crypto/getAllCryptos";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
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
            for (String coinId : coinQuantityMap.keySet()) {
                Optional<Map<String, Object>> cryptoOpt = cryptoData.stream()
                        .filter(c -> c.get("id").equals(coinId))
                        .findFirst();
                if (cryptoOpt.isPresent()) {
                    Map<String, Object> crypto = cryptoOpt.get();
                    double currentPrice = ((Number) crypto.get("current_price")).doubleValue();
                    double totalQuantity = coinQuantityMap.get(coinId);
                    double totalInvestment = coinInvestmentMap.get(coinId);
                    String coinSymbol = coinSymbolMap.get(coinId);
                    String coinName = coinNameMap.get(coinId);

                    double currentValue = currentPrice * totalQuantity;
                    double profitLoss = currentValue - totalInvestment;
                    System.out.println("Coin: " + coinName);
                    System.out.println("Bought Value: " + totalInvestment);
                    System.out.println("Current Value: " + currentValue);
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("coin_id", coinId);
                    entry.put("coin_name", coinName);
                    entry.put("coin_symbol", coinSymbol);
                    entry.put("quantity", totalQuantity);
                    entry.put("invested", totalInvestment);
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
                    .body(new ApiResponse(false, "Error calculating net value " + e.getMessage()));
        }
    }
}