package pl.kathelan.gamemediarekru.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.kathelan.gamemediarekru.dtos.CurrencyExchangeDetails;
import pl.kathelan.gamemediarekru.dtos.ExchangeRequest;
import pl.kathelan.gamemediarekru.dtos.ExchangeResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeService {

    private final ExchangeCalculationService calculationService;

    public ExchangeResponse calculateExchange(ExchangeRequest request) {
        ExchangeResponse response = new ExchangeResponse();
        response.setFrom(request.getFrom());

        Map<String, CurrencyExchangeDetails> exchangeDetailsMap = new ConcurrentHashMap<>();

        List<CompletableFuture<Void>> futures = request.getTo().stream()
                .map(targetCurrency -> calculationService.calculateSingleExchange(request.getFrom(), targetCurrency, request.getAmount())
                        .thenAccept(details -> {
                            if (details != null) {
                                exchangeDetailsMap.put(targetCurrency, details);
                            }
                        }))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        response.setExchanges(exchangeDetailsMap);
        return response;
    }
}