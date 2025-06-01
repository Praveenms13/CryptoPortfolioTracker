package com.example.CryptoPortfolioTracker.service;
import com.example.CryptoPortfolioTracker.dto.AddHoldingRequest;
import com.example.CryptoPortfolioTracker.dto.HoldingDTO;
import com.example.CryptoPortfolioTracker.entity.Holding;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.entity.Log;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.HoldingRepository;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import com.example.CryptoPortfolioTracker.repository.LogRepository;
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
    private LogRepository logRepository;
    @Autowired
    private UserRepository userRepository;
    Optional<ResponseEntity<ApiResponse>> validateUser(Long userId, String token) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Optional.of(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "User not found")));
        }
        User user = userOptional.get();
        if (!token.equals(user.getSessionToken())) {
            return Optional.of(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Unauthorized token or user mismatch")));
        }
        return Optional.empty();
    }
    public ResponseEntity<ApiResponse> addHolding(Long userId, String token, AddHoldingRequest request) {
        Optional<ResponseEntity<ApiResponse>> error = validateUser(userId, token);
        if (error.isPresent())
            return error.get();
        try {
            User user = userRepository.findById(userId).get();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "http://localhost:8080/api/crypto/getAllCryptos",
                    HttpMethod.GET,
                    entity,
                    Map.class);
            if (!response.getStatusCode().is2xxSuccessful() || !(Boolean) response.getBody().get("result")) {
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
            Holding holding = new Holding();
            holding.setUser(user);
            holding.setCoinId((String) matchedCoin.get("id"));
            holding.setCoinName((String) matchedCoin.get("name"));
            holding.setCoinSymbol((String) matchedCoin.get("symbol"));
            holding.setCoinQuantity((int) request.getQuantity());
            holding.setBoughtPrice(((Number) matchedCoin.get("current_price")).longValue());
            holding.setBoughtDate(LocalDateTime.now());
            holding.setPricealert(null);
            holdingRepository.save(holding);
            return ResponseEntity.ok(new ApiResponse(true, "Holding added successfully", holding));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error adding holding: " + e.getMessage()));
        }
    }
    public ResponseEntity<ApiResponse> getHoldingsWithNetValue(Long userId, String token) {
        Optional<ResponseEntity<ApiResponse>> error = validateUser(userId, token);
        if (error.isPresent())
            return error.get();
        try {
            List<Holding> holdings = holdingRepository.findByUserIdOrderByBoughtDateDesc(userId.intValue());
            if (holdings.isEmpty()) {
                Map<String, Object> emptyResult = new HashMap<>();
                emptyResult.put("total_portfolio_value", 0);
                emptyResult.put("holdings", Collections.emptyList());
                return ResponseEntity.ok(new ApiResponse(true, "No holdings found", emptyResult));
            }
            Map<String, CoinInfo> coins = new HashMap<>();
            for (Holding h : holdings) {
                String coinId = h.getCoinId();
                CoinInfo info = coins.getOrDefault(coinId, new CoinInfo(coinId, h.getCoinName(), h.getCoinSymbol()));
                info.quantity += h.getCoinQuantity();
                info.invested += h.getBoughtPrice() * h.getCoinQuantity();
                coins.put(coinId, info);
            }
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8080/api/crypto/getAllCryptos";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(new ApiResponse(false, "Failed to fetch crypto prices"));
            }
            List<Map<String, Object>> cryptoList = (List<Map<String, Object>>) response.getBody().get("data");
            double totalValue = 0;
            List<Map<String, Object>> resultHoldings = new ArrayList<>();
            for (CoinInfo coin : coins.values()) {
                double currentPrice = 0;
                for (Map<String, Object> crypto : cryptoList) {
                    if (crypto.get("id").equals(coin.coinId)) {
                        currentPrice = ((Number) crypto.get("current_price")).doubleValue();
                        break;
                    }
                }
                double currentValue = currentPrice * coin.quantity;
                double profitLoss = currentValue - coin.invested;
                Map<String, Object> entry = new HashMap<>();
                entry.put("coin_id", coin.coinId);
                entry.put("coin_name", coin.coinName);
                entry.put("coin_symbol", coin.coinSymbol);
                entry.put("quantity", coin.quantity);
                entry.put("invested", coin.invested);
                entry.put("current_value", currentValue);
                entry.put("profit_loss", profitLoss);
                resultHoldings.add(entry);
                totalValue += currentValue;
            }
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("total_portfolio_value", totalValue);
            responseData.put("holdings", resultHoldings);
            return ResponseEntity
                    .ok(new ApiResponse(true, "Holdings and net value fetched successfully", responseData));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error fetching holdings and net value: " + e.getMessage()));
        }
    }

    protected ResponseEntity<Map> callCryptoApi(String token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange("http://localhost:8080/api/crypto/getAllCryptos",
                HttpMethod.GET, entity, Map.class);
    }


    static class CoinInfo {
        String coinId;
        String coinName;
        String coinSymbol;
        double quantity = 0;
        double invested = 0;
        public CoinInfo(String coinId, String coinName, String coinSymbol) {
            this.coinId = coinId;
            this.coinName = coinName;
            this.coinSymbol = coinSymbol;
        }
    }
    public ResponseEntity<ApiResponse> sellHolding(Long userId, String token, String coinId, int quantityToSell) {
        Optional<ResponseEntity<ApiResponse>> error = validateUser(userId, token);
        if (error.isPresent())
            return error.get();
        try {
            List<Holding> userHoldings = holdingRepository.findByUserIdOrderByBoughtDateDesc(userId.intValue());
            List<Holding> coinHoldings = new ArrayList<>();
            for (Holding h : userHoldings) {
                if (h.getCoinId().equalsIgnoreCase(coinId)) {
                    coinHoldings.add(h);
                }
            }
            if (coinHoldings.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(false, "No holdings found for coin: " + coinId));
            }
            int totalQuantity = 0;
            for (Holding h : coinHoldings) {
                totalQuantity += h.getCoinQuantity();
            }
            if (quantityToSell > totalQuantity) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(false, "Not enough quantity to sell. You own " + totalQuantity));
            }
            int quantityLeftToSell = quantityToSell;

            Optional<User> optionalUser = userRepository.findById(userId);
            User user;
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
            } else {
                throw new RuntimeException("User not found");
            }
            for (Holding h : coinHoldings) {
                if (quantityLeftToSell == 0)
                    break;
                int hQuantity = h.getCoinQuantity();
                int quantitySoldNow = Math.min(hQuantity, quantityLeftToSell);
                Log log = new Log();
                log.setBoughtDate(h.getBoughtDate());
                log.setBoughtPrice(h.getBoughtPrice());
                log.setCoinId(h.getCoinId());
                log.setQuantity(quantitySoldNow);
                log.setCoinName(h.getCoinName());
                log.setCoinSymbol(h.getCoinSymbol());
                log.setSoldDate(LocalDateTime.now());
                log.setSoldPrice(h.getBoughtPrice()); 
                log.setUser(user);
                logRepository.save(log);
                if (hQuantity <= quantityLeftToSell) {
                    quantityLeftToSell -= hQuantity;
                    holdingRepository.delete(h);
                } else {
                    h.setCoinQuantity(hQuantity - quantityLeftToSell);
                    holdingRepository.save(h);
                    quantityLeftToSell = 0;
                }
            }
            return ResponseEntity
                    .ok(new ApiResponse(true, "Successfully sold " + quantityToSell + " units of " + coinId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error selling holding: " + e.getMessage()));
        }
    }
}
