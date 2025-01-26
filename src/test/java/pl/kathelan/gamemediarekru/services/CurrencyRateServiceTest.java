package pl.kathelan.gamemediarekru.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kathelan.gamemediarekru.dtos.CurrencyRatesResponse;
import pl.kathelan.gamemediarekru.services.clients.BinanceClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyRateServiceTest {

    @Mock
    private BinanceClient binanceClient;

    @InjectMocks
    private CurrencyRateService currencyRateService;

    @Test
    void shouldFetchAndFormatRates() throws Exception {
        // Given
        String baseCurrency = "BTC";
        List<String> filters = List.of("ETH", "USDT");
        Map<String, String> mockRatesETH = Map.of("symbol", "ETHBTC", "price", "0.05");
        Map<String, String> mockRatesUSDT = Map.of("symbol", "BTCUSDT", "price", "1.0");
        when(binanceClient.getRates("ETHBTC")).thenReturn(mockRatesETH);
        when(binanceClient.getRates("BTCUSDT")).thenReturn(mockRatesUSDT);

        // When
        CompletableFuture<CurrencyRatesResponse> future = currencyRateService.getRates(baseCurrency, filters);
        CurrencyRatesResponse response = future.get();

        // Then
        assertEquals(baseCurrency, response.getSource());
        assertEquals(2, response.getRates().size());
        assertEquals("0.05", response.getRates().get("ETH"));
        assertEquals("1.0", response.getRates().get("USDT"));
    }

    @Test
    void shouldLogErrorAndSkipFailedSymbol() throws Exception {
        // Given
        String baseCurrency = "BTC";
        List<String> filters = List.of("ETH", "USDT");

        Map<String, String> mockRatesETH = Map.of("symbol", "ETHBTC", "price", "0.05");
        when(binanceClient.getRates("ETHBTC")).thenReturn(mockRatesETH);
        when(binanceClient.getRates("BTCUSDT")).thenThrow(new RuntimeException("Simulated error"));

        // When
        CompletableFuture<CurrencyRatesResponse> future = currencyRateService.getRates(baseCurrency, filters);
        CurrencyRatesResponse response = future.get();

        // Then
        assertEquals(baseCurrency, response.getSource());
        assertEquals(1, response.getRates().size());
        assertEquals("0.05", response.getRates().get("ETH"));
        assertNull(response.getRates().get("USDT"));
    }
}
