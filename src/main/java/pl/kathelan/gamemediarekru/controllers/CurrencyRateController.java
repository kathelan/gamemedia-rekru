package pl.kathelan.gamemediarekru.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kathelan.gamemediarekru.dtos.CurrencyRatesResponse;
import pl.kathelan.gamemediarekru.services.CurrencyRateService;

import java.util.List;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
public class CurrencyRateController {
    private final CurrencyRateService currencyRateService;


    @GetMapping("/{currency}")
    public ResponseEntity<CurrencyRatesResponse> getRates(
            @PathVariable String currency,
            @RequestParam(value = "filter", required = false) List<String> filters) {
        CurrencyRatesResponse rates = currencyRateService.getRates(currency, filters).join();
        return ResponseEntity.ok(rates);
    }

//    @PostMapping("/exchange")
//    public ResponseEntity<ExchangeResult> exchange(@RequestBody ExchangeRequest exchangeRequest) {
//        return ResponseEntity.ok().build();
//    }
}
