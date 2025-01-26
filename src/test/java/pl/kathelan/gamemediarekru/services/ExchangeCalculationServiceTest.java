package pl.kathelan.gamemediarekru.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kathelan.gamemediarekru.dtos.CurrencyExchangeDetails;
import pl.kathelan.gamemediarekru.services.clients.BinanceClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeCalculationServiceTest {

    @Mock
    private BinanceClient binanceClient;

    @InjectMocks
    private ExchangeCalculationService calculationService;

    @Test
    void shouldCalculateExchangeCorrectly() throws Exception {
        // Given
        String baseCurrency = "BTC";
        String targetCurrency = "PLN";
        double amount = 100.0;
        when(binanceClient.getRates("BTCPLN")).thenReturn(Map.of("price", "423795.0"));

        // When
        CompletableFuture<CurrencyExchangeDetails> future =
                calculationService.calculateSingleExchange(baseCurrency, targetCurrency, amount);
        CurrencyExchangeDetails details = future.get();

        // Then
        assertNotNull(details);
        assertEquals(423795.0, details.getRate());
        assertEquals(100.0, details.getAmount());
        assertEquals(new BigDecimal("41955705.0000"), details.getResult());
        assertEquals(new BigDecimal("1.000"), details.getFee());
    }

    @Test
    void shouldReturnNullOnException() throws Exception {
        // Given
        String baseCurrency = "BTC";
        String targetCurrency = "PLN";
        double amount = 100.0;
        when(binanceClient.getRates("BTCPLN")).thenThrow(new RuntimeException("API error"));

        // When
        CompletableFuture<CurrencyExchangeDetails> future =
                calculationService.calculateSingleExchange(baseCurrency, targetCurrency, amount);
        CurrencyExchangeDetails details = future.get();

        // Then
        assertNull(details);
    }
}

