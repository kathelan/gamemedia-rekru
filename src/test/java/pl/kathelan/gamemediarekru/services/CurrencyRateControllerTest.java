package pl.kathelan.gamemediarekru.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.kathelan.gamemediarekru.controllers.CurrencyRateController;
import pl.kathelan.gamemediarekru.dtos.CurrencyExchangeDetails;
import pl.kathelan.gamemediarekru.dtos.CurrencyRatesResponse;
import pl.kathelan.gamemediarekru.dtos.ExchangeRequest;
import pl.kathelan.gamemediarekru.dtos.ExchangeResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurrencyRateController.class)
class CurrencyRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CurrencyRateService currencyRateService;

    @Autowired
    private ExchangeService exchangeService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CurrencyRateService currencyRateService() {
            return mock(CurrencyRateService.class);
        }

        @Bean
        public ExchangeService exchangeService() {
            return mock(ExchangeService.class);
        }
    }

    @Test
    void shouldFetchCurrencyRates() throws Exception {
        CurrencyRateService localCurrencyRateService = mock(CurrencyRateService.class);
        MockMvc localMockMvc = MockMvcBuilders.standaloneSetup(new CurrencyRateController(localCurrencyRateService, exchangeService)).build();

        // Given
        String baseCurrency = "BTC";
        List<String> filters = List.of("USDT", "ETH");

        CurrencyRatesResponse mockResponse = new CurrencyRatesResponse(
                baseCurrency,
                Map.of("USDT", "1.0", "ETH", "0.05")
        );

        Mockito.doReturn(CompletableFuture.completedFuture(mockResponse))
                .when(localCurrencyRateService)
                .getRates(baseCurrency, filters);

        // When & Then
        localMockMvc.perform(get("/currencies/{currency}", baseCurrency)
                        .param("filter", "USDT")
                        .param("filter", "ETH")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value("BTC"))
                .andExpect(jsonPath("$.rates.USDT").value("1.0"))
                .andExpect(jsonPath("$.rates.ETH").value("0.05"));
    }

    @Test
    void shouldExchangeCurrency() throws Exception {
        // Given
        ExchangeRequest request = new ExchangeRequest();
        request.setFrom("BTC");
        request.setTo(List.of("PLN", "EUR"));
        request.setAmount(100);

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

        ExchangeResponse mockResponse = new ExchangeResponse();
        mockResponse.setFrom("BTC");
        mockResponse.setExchanges(Map.of(
                "PLN", plnDetails,
                "EUR", eurDetails
        ));

        when(exchangeService.calculateExchange(any(ExchangeRequest.class)))
                .thenReturn(mockResponse);
        // When & Then
        mockMvc.perform(post("/currencies/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "from": "BTC",
                                  "to": ["PLN", "EUR"],
                                  "amount": 100
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("BTC"))
                .andExpect(jsonPath("$.exchanges.PLN.rate").value(423795.0))
                .andExpect(jsonPath("$.exchanges.PLN.amount").value(100.0))
                .andExpect(jsonPath("$.exchanges.PLN.result").value(41955705.0000))
                .andExpect(jsonPath("$.exchanges.PLN.fee").value(1.000))
                .andExpect(jsonPath("$.exchanges.EUR.rate").value(23000.0))
                .andExpect(jsonPath("$.exchanges.EUR.amount").value(100.0))
                .andExpect(jsonPath("$.exchanges.EUR.result").value(2295000.0000))
                .andExpect(jsonPath("$.exchanges.EUR.fee").value(1.000));
    }
}
