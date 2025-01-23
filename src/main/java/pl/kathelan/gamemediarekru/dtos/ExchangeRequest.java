package pl.kathelan.gamemediarekru.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExchangeRequest {
    private String from;
    private List<String> to;
    private double amount;
}
