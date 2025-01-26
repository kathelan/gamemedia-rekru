package pl.kathelan.gamemediarekru.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.kathelan.gamemediarekru.controllers.CurrencyRateController;
import pl.kathelan.gamemediarekru.dtos.CurrencyRatesResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurrencyRateController.class)
class CurrencyRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CurrencyRateService currencyRateService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CurrencyRateService currencyRateService() {
            return mock(CurrencyRateService.class);
        }
    }

    @Test
    void shouldFetchCurrencyRates() throws Exception {
        // Given
        String baseCurrency = "BTC";
        List<String> filters = List.of("USDT", "ETH");

        CurrencyRatesResponse mockResponse = new CurrencyRatesResponse(
                baseCurrency,
                Map.of("USDT", "1.0", "ETH", "0.05")
        );

        when(currencyRateService.getRates(baseCurrency, filters))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        // When & Then
        mockMvc.perform(get("/currencies/{currency}", baseCurrency)
                        .param("filter", "USDT")
                        .param("filter", "ETH")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value("BTC"))
                .andExpect(jsonPath("$.rates.USDT").value("1.0"))
                .andExpect(jsonPath("$.rates.ETH").value("0.05"));
    }
}
