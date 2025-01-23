package pl.kathelan.gamemediarekru.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kathelan.gamemediarekru.dtos.ExchangeDetails;
import pl.kathelan.gamemediarekru.dtos.ExchangeRequest;
import pl.kathelan.gamemediarekru.dtos.ExchangeResult;
import pl.kathelan.gamemediarekru.entities.CurrencyRate;
import pl.kathelan.gamemediarekru.repositories.CurrencyRateRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final CurrencyRateRepository currencyRateRepository;
    private final CryptoRateFetcherService cryptoRateFetcherService;
    private static final double FEE_RATE = 0.01;

    public ExchangeResult calculateExchange(ExchangeRequest request) {
        ExchangeResult result = new ExchangeResult();
        result.setFrom(request.getFrom());
        Map<String, ExchangeDetails> detailsMap = new HashMap<>();

        for (String target : request.getTo()) {
            CurrencyRate rate = currencyRateRepository.findById(request.getFrom() + "-" + target).orElse(null);
            if (rate != null) {
                ExchangeDetails details = new ExchangeDetails();
                double exchangedAmount = request.getAmount() * rate.getRate();
                double fee = exchangedAmount * FEE_RATE;
                details.setRate(rate.getRate());
                details.setAmount(request.getAmount());
                details.setResult(exchangedAmount - fee);
                details.setFee(fee);
                detailsMap.put(target, details);
            }
        }

        result.setExchanges(detailsMap);
        return result;
    }

    public void updateRates(String baseCurrency, List<String> targetCurrencies) {
        Map<String, Double> rates = cryptoRateFetcherService.fetchCryptoRates(baseCurrency, targetCurrencies);
        for (Map.Entry<String, Double> entry : rates.entrySet()) {
            CurrencyRate rate = new CurrencyRate(baseCurrency, entry.getKey(), entry.getValue());
            currencyRateRepository.save(rate);
        }
    }

}
