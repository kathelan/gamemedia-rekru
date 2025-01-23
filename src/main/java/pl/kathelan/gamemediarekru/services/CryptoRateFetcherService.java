package pl.kathelan.gamemediarekru.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoRateFetcherService {
    private static final String API_URL = "https://api.coingecko.com/api/v3/simple/price";
    private final RestTemplate restTemplate;


    public Map<String, Double> fetchCryptoRates(String baseCurrency, List<String> targetCurrencies) {
        String ids = baseCurrency.toLowerCase(); // e.g., "bitcoin"
        String targets = String.join(",", targetCurrencies).toLowerCase(); // e.g., "usd,eur"

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("ids", ids)
                .queryParam("vs_currencies", targets);

        log.info("Requesting URL: {}", uriBuilder.toUriString());


        ResponseEntity<Map<String, Map<String, Double>>> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("Response Body: {}", response.getBody());
            if (response.getBody() != null && response.getBody().containsKey(baseCurrency)) {
                return response.getBody().get(baseCurrency);
            } else {
                log.error("Unexpected response format or empty response.");
            }
        } else {
            log.error("Failed to fetch rates: {}", response.getStatusCode());
        }
        return Collections.emptyMap();
    }
}
