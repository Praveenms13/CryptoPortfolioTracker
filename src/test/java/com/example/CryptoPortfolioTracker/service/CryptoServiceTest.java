package com.example.CryptoPortfolioTracker.service;

import com.example.CryptoPortfolioTracker.model.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CryptoServiceTest {

    @InjectMocks
    private CryptoService cryptoService;

    @Mock
    private RestTemplate restTemplate;

    private final String COINGECKO_URL = "https://api.coingecko.com/api/v3/coins/markets" +
            "?vs_currency=usd&order=market_cap_desc&per_page=250&page=1&x_cg_demo_api_key=CG-c7Eoq1PyYSu3r7Hg2YC6DmUC";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchAllCryptos_successResponse_returnsApiResponseWithData() {
        Object[] mockBody = new Object[]{"bitcoin", "ethereum"};
        ResponseEntity<Object[]> mockResponse = new ResponseEntity<>(mockBody, HttpStatus.OK);

        when(restTemplate.getForEntity(COINGECKO_URL, Object[].class)).thenReturn(mockResponse);

        ResponseEntity<ApiResponse> response = cryptoService.fetchAllCryptos();

        assertTrue(response.getBody().isResult());
        assertEquals("Cryptos fetched successfully", response.getBody().getMessage());
        assertArrayEquals(mockBody, (Object[]) response.getBody().getData());
    }

    @Test
    void fetchAllCryptos_notModifiedResponse_returnsApiResponseWithNoUpdateMessage() {
        ResponseEntity<Object[]> mockResponse = new ResponseEntity<>(null, HttpStatus.NOT_MODIFIED);

        when(restTemplate.getForEntity(COINGECKO_URL, Object[].class)).thenReturn(mockResponse);

        ResponseEntity<ApiResponse> response = cryptoService.fetchAllCryptos();

        assertTrue(response.getBody().isResult());
        assertEquals("No update - using cached data", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void fetchAllCryptos_errorStatus_returnsFailureResponse() {
        ResponseEntity<Object[]> mockResponse = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        when(restTemplate.getForEntity(COINGECKO_URL, Object[].class)).thenReturn(mockResponse);

        ResponseEntity<ApiResponse> response = cryptoService.fetchAllCryptos();

        assertFalse(response.getBody().isResult());
        assertEquals("Failed to fetch cryptos", response.getBody().getMessage());
    }

    @Test
    void fetchAllCryptos_exceptionThrown_returnsErrorResponse() {
        when(restTemplate.getForEntity(COINGECKO_URL, Object[].class))
                .thenThrow(new RuntimeException("API down"));

        ResponseEntity<ApiResponse> response = cryptoService.fetchAllCryptos();

        assertFalse(response.getBody().isResult());
        assertTrue(response.getBody().getMessage().contains("Error fetching cryptos"));
    }
}
