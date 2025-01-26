package pl.kathelan.gamemediarekru.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kathelan.gamemediarekru.dtos.CurrencyExchangeDetails;
import pl.kathelan.gamemediarekru.dtos.ExchangeRequest;
import pl.kathelan.gamemediarekru.dtos.ExchangeResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeServiceTest {

    @Mock
    private ExchangeCalculationService calculationService;

    @InjectMocks
    private ExchangeService exchangeService;

    @Test
    void shouldCalculateExchangeForMultipleCurrencies()  {
        // Given
        ExchangeRequest request = new ExchangeRequest();
        request.setFrom("BTC");
        request.setTo(List.of("PLN", "EUR"));
        request.setAmount(100.0);

        CurrencyExchangeDetails plnDetails = new CurrencyExchangeDetails();
        plnDetails.setRate(423795.0);
        plnDetails.setAmount(100.0);
        plnDetails.setResult(new BigDecimal("41955705.0000"));
        plnDetails.setFee(new BigDecimal("1.000"));

        CurrencyExchangeDetails eurDetails = new CurrencyExchangeDetails();
        eurDetails.setRate(23000.0);
        eurDetails.setAmount(100.0);
        eurDetails.setResult(new BigDecimal("2295000.0000"));
        eurDetails.setFee(new BigDecimal("1.000"));

        when(calculationService.calculateSingleExchange("BTC", "PLN", 100.0))
                .thenReturn(CompletableFuture.completedFuture(plnDetails));
        when(calculationService.calculateSingleExchange("BTC", "EUR", 100.0))
                .thenReturn(CompletableFuture.completedFuture(eurDetails));

        // When
        ExchangeResponse response = exchangeService.calculateExchange(request);

        // Then
        assertEquals("BTC", response.getFrom());
        assertEquals(2, response.getExchanges().size());

        assertEquals(plnDetails, response.getExchanges().get("PLN"));
        assertEquals(eurDetails, response.getExchanges().get("EUR"));
    }

    @Test
    void shouldHandleErrorsGracefully() {
        // Given
        ExchangeRequest request = new ExchangeRequest();
        request.setFrom("BTC");
        request.setTo(List.of("PLN", "EUR"));
        request.setAmount(100.0);

        CurrencyExchangeDetails plnDetails = new CurrencyExchangeDetails();
        plnDetails.setRate(423795.0);
        plnDetails.setAmount(100.0);
        plnDetails.setResult(new BigDecimal("41955705.0000"));
        plnDetails.setFee(new BigDecimal("1.000"));

        when(calculationService.calculateSingleExchange("BTC", "PLN", 100.0))
                .thenReturn(CompletableFuture.completedFuture(plnDetails));
        when(calculationService.calculateSingleExchange("BTC", "EUR", 100.0))
                .thenReturn(CompletableFuture.completedFuture(null)); // Błąd dla EUR

        // When
        ExchangeResponse response = exchangeService.calculateExchange(request);

        // Then
        assertEquals("BTC", response.getFrom());
        assertEquals(1, response.getExchanges().size());
        assertEquals(plnDetails, response.getExchanges().get("PLN"));
        assertNull(response.getExchanges().get("EUR"));
    }
}

