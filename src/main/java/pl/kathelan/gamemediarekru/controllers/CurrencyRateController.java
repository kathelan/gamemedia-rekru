package pl.kathelan.gamemediarekru.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.kathelan.gamemediarekru.dtos.ExchangeRequest;
import pl.kathelan.gamemediarekru.dtos.ExchangeResult;
import pl.kathelan.gamemediarekru.entities.CurrencyRate;
import pl.kathelan.gamemediarekru.services.CurrencyRateService;
import pl.kathelan.gamemediarekru.services.ExchangeService;

import java.util.List;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
public class CurrencyRateController {
    private final CurrencyRateService currencyRateService;
    private final ExchangeService exchangeService;

    @GetMapping("/{currency}")
    public CurrencyRate getRates(@PathVariable String currency, @RequestParam(required = false) List<String> filter) {
        return currencyRateService.findRateById(currency);
    }

    @PostMapping("/exchange")
    public ExchangeResult exchange(@RequestBody ExchangeRequest exchangeRequest) {
        return exchangeService.calculateExchange(exchangeRequest);
    }
}
