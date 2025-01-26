package pl.kathelan.gamemediarekru.dtos;

import java.util.Set;

public enum CurrencyType {
    QUOTE(Set.of("USD", "EUR", "PLN", "USDT"));

    private final Set<String> quoteCurrencies;

    CurrencyType(Set<String> quoteCurrencies) {
        this.quoteCurrencies = quoteCurrencies;
    }

    public boolean isQuoteCurrency(String currency) {
        return quoteCurrencies.contains(currency);
    }
}