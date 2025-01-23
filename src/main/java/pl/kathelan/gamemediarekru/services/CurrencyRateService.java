package pl.kathelan.gamemediarekru.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kathelan.gamemediarekru.entities.CurrencyRate;
import pl.kathelan.gamemediarekru.repositories.CurrencyRateRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyRateService {
    private final CurrencyRateRepository currencyRateRepository;

    public List<CurrencyRate> findAllRates() {
        return currencyRateRepository.findAll();
    }

    public CurrencyRate findRateById(String id) {
        return currencyRateRepository.findById(id).orElse(null);
    }

}
