package pl.kathelan.gamemediarekru.services.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinanceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String BINANCE_API_URL = "https://api.binance.com/api/v3/ticker/price";

    public Map<String, String> getRates(String currency) {
        try {
            String uri = UriComponentsBuilder.fromUriString(BINANCE_API_URL)
                    .queryParam("symbol", currency)
                    .toUriString();
            log.info("Fetching rates for currency: {}", currency);
            String responseJson = restTemplate.getForObject(uri, String.class);

            return objectMapper.readValue(responseJson, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Failed to fetch rates from Binance API for currency: {}", currency, e);
            throw new RuntimeException("Failed to fetch rates from Binance API", e);
        }
    }
}