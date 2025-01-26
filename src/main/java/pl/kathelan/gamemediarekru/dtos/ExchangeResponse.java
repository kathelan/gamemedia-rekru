package pl.kathelan.gamemediarekru.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ExchangeResponse {
    private String from;
    private Map<String, CurrencyExchangeDetails> exchanges;
}
