package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.dto.AddHoldingRequest;
import com.example.CryptoPortfolioTracker.entity.Holding;
import com.example.CryptoPortfolioTracker.entity.Log;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.HoldingRepository;
import com.example.CryptoPortfolioTracker.repository.LogRepository;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class HoldingServiceTest {

    @InjectMocks
    private HoldingService holdingService;

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private LogRepository logRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private void injectRestTemplate(HoldingService service, RestTemplate restTemplate) throws Exception {
        java.lang.reflect.Field field = HoldingService.class.getDeclaredField("restTemplate");
        field.setAccessible(true);
        field.set(service, restTemplate);
    }

    @Test
    void validateUser_whenUserNotFound_returnsNotFoundResponse() {
        Long userId = 1L;
        String token = "token";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<ResponseEntity<ApiResponse>> response = holdingService.validateUser(userId, token);

        assertThat(response).isPresent();
        ResponseEntity<ApiResponse> respEntity = response.get();
        assertThat(respEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(respEntity.getBody().isResult()).isFalse();
        assertThat(respEntity.getBody().getMessage()).isEqualTo("User not found");
    }

    @Test
    void validateUser_whenTokenMismatch_returnsUnauthorized() {
        Long userId = 1L;
        String token = "wrongToken";
        User user = new User();
        user.setSessionToken("correctToken");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<ResponseEntity<ApiResponse>> response = holdingService.validateUser(userId, token);

        assertThat(response).isPresent();
        ResponseEntity<ApiResponse> respEntity = response.get();
        assertThat(respEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(respEntity.getBody().getMessage()).isEqualTo("Unauthorized token or user mismatch");
    }

    @Test
    void validateUser_whenUserAndTokenValid_returnsEmpty() {
        Long userId = 1L;
        String token = "token";
        User user = new User();
        user.setSessionToken(token);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<ResponseEntity<ApiResponse>> response = holdingService.validateUser(userId, token);

        assertThat(response).isEmpty();
    }

    @Test
    void addHolding_success() {
        Long userId = 1L;
        String token = "token";
        AddHoldingRequest request = new AddHoldingRequest();
        request.setCoinId("bitcoin");
        request.setQuantity(2);

        User user = new User();
        user.setId(userId);
        user.setSessionToken(token);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Map<String, Object> crypto = new HashMap<>();
        crypto.put("id", "bitcoin");
        crypto.put("name", "Bitcoin");
        crypto.put("symbol", "BTC");
        crypto.put("current_price", 30000L);

        Map<String, Object> body = new HashMap<>();
        body.put("result", true);
        body.put("data", Collections.singletonList(crypto));

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        HoldingService spyService = Mockito.spy(holdingService);
        doReturn(Optional.empty()).when(spyService).validateUser(userId, token);
        doReturn(responseEntity).when(spyService).callCryptoApi(token);
        ArgumentCaptor<Holding> holdingCaptor = ArgumentCaptor.forClass(Holding.class);
        when(holdingRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<ApiResponse> response = spyService.addHolding(userId, token, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isResult()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Holding added successfully");

        verify(holdingRepository).save(holdingCaptor.capture());
        Holding savedHolding = holdingCaptor.getValue();
        assertThat(savedHolding.getCoinId()).isEqualTo("bitcoin");
        assertThat(savedHolding.getCoinName()).isEqualTo("Bitcoin");
        assertThat(savedHolding.getCoinSymbol()).isEqualTo("BTC");
        assertThat(savedHolding.getCoinQuantity()).isEqualTo(2);
        assertThat(savedHolding.getBoughtPrice()).isEqualTo(30000L);
        assertThat(savedHolding.getUser()).isEqualTo(user);
    }

    @Test
    void addHolding_invalidCoinId_returnsBadRequest() {
        Long userId = 1L;
        String token = "token";
        AddHoldingRequest request = new AddHoldingRequest();
        request.setCoinId("invalidcoin");
        request.setQuantity(1);

        User user = new User();
        user.setId(userId);
        user.setSessionToken(token);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Map<String, Object> crypto = new HashMap<>();
        crypto.put("id", "bitcoin");
        crypto.put("name", "Bitcoin");
        crypto.put("symbol", "BTC");
        crypto.put("current_price", 30000L);

        Map<String, Object> body = new HashMap<>();
        body.put("result", true);
        body.put("data", Collections.singletonList(crypto));

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        HoldingService spyService = Mockito.spy(holdingService);
        doReturn(Optional.empty()).when(spyService).validateUser(userId, token);
        doReturn(responseEntity).when(spyService).callCryptoApi(token);

        ResponseEntity<ApiResponse> response = spyService.addHolding(userId, token, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().isResult()).isFalse();
        assertThat(response.getBody().getMessage()).contains("Invalid coinId");
    }

    @Test
    void getHoldingsWithNetValue_emptyHoldings_returnsEmptyResult() {
        Long userId = 1L;
        String token = "token";

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(holdingRepository.findByUserIdOrderByBoughtDateDesc(userId.intValue())).thenReturn(Collections.emptyList());

        HoldingService spyService = Mockito.spy(holdingService);
        doReturn(Optional.empty()).when(spyService).validateUser(userId, token);

        ResponseEntity<ApiResponse> response = spyService.getHoldingsWithNetValue(userId, token);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isResult()).isTrue();
        Map<String, Object> data = (Map<String, Object>) response.getBody().getData();
        assertThat(data.get("total_portfolio_value")).isEqualTo(0);
        assertThat(((List<?>) data.get("holdings"))).isEmpty();
    }

    @Test
    void sellHolding_successfulSale() {
        Long userId = 1L;
        String token = "token";
        String coinId = "bitcoin";
        int quantityToSell = 3;

        User user = new User();
        user.setId(userId);
        user.setSessionToken(token);

        Holding h1 = new Holding();
        h1.setCoinId("bitcoin");
        h1.setCoinQuantity(2);
        h1.setBoughtPrice(10000L);
        h1.setBoughtDate(LocalDateTime.now().minusDays(5));
        h1.setCoinName("Bitcoin");
        h1.setCoinSymbol("BTC");
        h1.setUser(user);

        Holding h2 = new Holding();
        h2.setCoinId("bitcoin");
        h2.setCoinQuantity(2);
        h2.setBoughtPrice(12000L);
        h2.setBoughtDate(LocalDateTime.now().minusDays(2));
        h2.setCoinName("Bitcoin");
        h2.setCoinSymbol("BTC");
        h2.setUser(user);

        List<Holding> holdings = Arrays.asList(h1, h2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(holdingRepository.findByUserIdOrderByBoughtDateDesc(userId.intValue())).thenReturn(holdings);

        HoldingService spyService = Mockito.spy(holdingService);
        doReturn(Optional.empty()).when(spyService).validateUser(userId, token);

        ResponseEntity<ApiResponse> response = spyService.sellHolding(userId, token, coinId, quantityToSell);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isResult()).isTrue();
        assertThat(response.getBody().getMessage()).contains("Successfully sold");

        verify(logRepository, times(2)).save(any(Log.class));

        verify(holdingRepository, atLeastOnce()).delete(any(Holding.class));
        verify(holdingRepository, atLeastOnce()).save(any(Holding.class));
    }

    @Test
    void sellHolding_insufficientQuantity_returnsBadRequest() {
        Long userId = 1L;
        String token = "token";
        String coinId = "bitcoin";
        int quantityToSell = 5;

        User user = new User();
        user.setId(userId);
        user.setSessionToken(token);

        Holding h1 = new Holding();
        h1.setCoinId("bitcoin");
        h1.setCoinQuantity(2);

        List<Holding> holdings = Collections.singletonList(h1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(holdingRepository.findByUserIdOrderByBoughtDateDesc(userId.intValue())).thenReturn(holdings);

        HoldingService spyService = Mockito.spy(holdingService);
        doReturn(Optional.empty()).when(spyService).validateUser(userId, token);

        ResponseEntity<ApiResponse> response = spyService.sellHolding(userId, token, coinId, quantityToSell);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Not enough quantity");
    }
}
