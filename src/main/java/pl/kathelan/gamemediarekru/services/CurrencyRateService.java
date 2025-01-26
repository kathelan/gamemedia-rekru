package pl.kathelan.gamemediarekru.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.kathelan.gamemediarekru.dtos.CurrencyRatesResponse;
import pl.kathelan.gamemediarekru.dtos.CurrencyType;
import pl.kathelan.gamemediarekru.services.clients.BinanceClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyRateService {
    private final BinanceClient binanceClient;

    @Async
    public CompletableFuture<CurrencyRatesResponse> getRates(String baseCurrency, List<String> filters) {
        log.info("Fetching rates for base currency: {} with filters: {}", baseCurrency, filters);
        log.info("Running in thread: {}", Thread.currentThread().getName());

        List<String> symbols = filters.stream()
                .map(filter -> {
                    if (CurrencyType.QUOTE.isQuoteCurrency(filter)) {
                        return baseCurrency + filter;
                    } else {
                        return filter + baseCurrency;
                    }
                })
                .toList();

        Map<String, String> rates = symbols.stream()
                .map(symbol -> {
                    try {
                        Map<String, String> response = binanceClient.getRates(symbol);
                        return Map.entry(symbol.replace(baseCurrency, ""), response.get("price"));
                    } catch (Exception e) {
                        log.error("Failed to fetch rate for symbol: {}", symbol, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        CurrencyRatesResponse response = new CurrencyRatesResponse(baseCurrency, rates);
        return CompletableFuture.completedFuture(response);
    }
}