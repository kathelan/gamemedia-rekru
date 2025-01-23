package pl.kathelan.gamemediarekru.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeDetails {
    private double rate;
    private double amount;
    private double result;
    private double fee;
}
