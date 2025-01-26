package pl.kathelan.gamemediarekru.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import pl.kathelan.gamemediarekru.services.clients.BinanceClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BinanceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private BinanceClient binanceClient;

    @Test
    void shouldFetchRatesFromBinance() throws Exception {
        // Given
        String currency = "BTCUSDT";
        String apiUrl = "https://api.binance.com/api/v3/ticker/price?symbol=BTCUSDT";

        String mockResponse = "{\"symbol\":\"BTCUSDT\",\"price\":\"50000.00\"}";

        Map<String, String> mockMappedResponse = Map.of(
                "symbol", "BTCUSDT",
                "price", "50000.00"
        );

        when(restTemplate.getForObject(apiUrl, String.class)).thenReturn(mockResponse);
        when(objectMapper.readValue(eq(mockResponse), any(TypeReference.class))).thenReturn(mockMappedResponse);

        // When
        Map<String, String> response = binanceClient.getRates(currency);

        // Then
        assertEquals("50000.00", response.get("price"));
        assertEquals("BTCUSDT", response.get("symbol"));
        verify(restTemplate, times(1)).getForObject(apiUrl, String.class);
        verify(objectMapper, times(1)).readValue(eq(mockResponse), any(TypeReference.class));
    }
}