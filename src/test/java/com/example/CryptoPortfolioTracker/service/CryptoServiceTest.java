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


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchAllCryptos_successResponse_returnsApiResponseWithData() {
        Object[] mockBody = new Object[] { "bitcoin", "ethereum" };
        ResponseEntity<Object[]> mockResponse = new ResponseEntity<>(mockBody, HttpStatus.OK);
        CryptoService spyService = spy(new CryptoService());

        doReturn(mockResponse).when(spyService).callRestTemplate();

        ResponseEntity<ApiResponse> response = spyService.fetchAllCryptos();

        assertTrue(response.getBody().isResult());
        assertEquals("Cryptos fetched successfully", response.getBody().getMessage());
        assertArrayEquals(mockBody, (Object[]) response.getBody().getData());
    }

    @Test
    void fetchAllCryptos_notModifiedResponse_returnsApiResponseWithNoUpdateMessage() {
        ResponseEntity<Object[]> mockResponse = new ResponseEntity<>(null, HttpStatus.NOT_MODIFIED);

        CryptoService spyService = spy(new CryptoService());

        doReturn(mockResponse).when(spyService).callRestTemplate();

        ResponseEntity<ApiResponse> response = spyService.fetchAllCryptos();

        assertTrue(response.getBody().isResult());
        assertEquals("No update - using cached data", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void fetchAllCryptos_errorStatus_returnsFailureResponse() {
        ResponseEntity<Object[]> mockResponse = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        CryptoService spyService = spy(new CryptoService());

        doReturn(mockResponse).when(spyService).callRestTemplate();

        ResponseEntity<ApiResponse> response = spyService.fetchAllCryptos();

        assertFalse(response.getBody().isResult());
        assertEquals("Failed to fetch cryptos", response.getBody().getMessage());
    }

    @Test
    void fetchAllCryptos_exceptionThrown_returnsErrorResponse() {
        CryptoService spyService = spy(new CryptoService());

        doThrow(new RuntimeException("API down")).when(spyService).callRestTemplate();

        ResponseEntity<ApiResponse> response = spyService.fetchAllCryptos();

        assertFalse(response.getBody().isResult());
        assertTrue(response.getBody().getMessage().contains("Error fetching cryptos"));
    }
}
