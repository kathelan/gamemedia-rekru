package pl.kathelan.gamemediarekru.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.kathelan.gamemediarekru.dtos.CurrencyExchangeDetails;
import pl.kathelan.gamemediarekru.services.clients.BinanceClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ExchangeCalculationService {

    private final BinanceClient binanceClient;
    private final double FEE = 0.01;

    public ExchangeCalculationService(BinanceClient binanceClient) {
        this.binanceClient = binanceClient;
    }

    @Async
    public CompletableFuture<CurrencyExchangeDetails> calculateSingleExchange(String baseCurrency, String targetCurrency, double amount) {
        String symbol = baseCurrency + targetCurrency;
        log.info("calculating exchange for symbol: {}", symbol);
        try {
            Map<String, String> rates = binanceClient.getRates(symbol);
            double rate = Double.parseDouble(rates.get("price"));

            BigDecimal amountBigDecimal = BigDecimal.valueOf(amount);
            BigDecimal fee = amountBigDecimal.multiply(BigDecimal.valueOf(FEE));
            BigDecimal amountAfterFee = amountBigDecimal.subtract(fee);
            BigDecimal result = amountAfterFee.multiply(BigDecimal.valueOf(rate));

            CurrencyExchangeDetails details = new CurrencyExchangeDetails();
            details.setRate(rate);
            details.setAmount(amount);
            details.setResult(result);
            details.setFee(fee);

            return CompletableFuture.completedFuture(details);
        } catch (Exception e) {
            log.error("Failed to fetch rate for symbol: {}", symbol, e);
            return CompletableFuture.completedFuture(null);
        }
    }
}

