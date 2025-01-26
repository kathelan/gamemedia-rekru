package pl.kathelan.gamemediarekru.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CurrencyExchangeDetails {
    private double rate;
    private double amount;
    private BigDecimal result;
    private BigDecimal fee;
}
