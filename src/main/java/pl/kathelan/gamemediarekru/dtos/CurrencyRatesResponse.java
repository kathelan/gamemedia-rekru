package pl.kathelan.gamemediarekru.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class CurrencyRatesResponse {
    private String source;
    private Map<String, String> rates;
}
