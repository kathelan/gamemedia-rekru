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

        List<CompletableFuture<Map.Entry<String, String>>> futures = filters.stream()
                .map(filter -> CompletableFuture.supplyAsync(() -> {
                    String symbol = CurrencyType.QUOTE.isQuoteCurrency(filter)
                            ? baseCurrency + filter
                            : filter + baseCurrency;
                    try {
                        Map<String, String> response = binanceClient.getRates(symbol);
                        String rate = response.get("price");
                        return Map.entry(filter, rate);
                    } catch (Exception e) {
                        log.error("Failed to fetch rate for symbol: {}", symbol, e);
                        return null;
                    }
                }))
                .toList();

        Map<String, String> rates = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        CurrencyRatesResponse response = new CurrencyRatesResponse(baseCurrency, rates);
        return CompletableFuture.completedFuture(response);
    }
}