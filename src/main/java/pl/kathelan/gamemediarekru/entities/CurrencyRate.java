package pl.kathelan.gamemediarekru.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "currency_rates")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyRate {
    @Id
    private String id;
    private String baseCurrency;
    private String targetCurrency;
    private double rate;

    public CurrencyRate(String baseCurrency, String targetCurrency, double rate) {
        this.id = baseCurrency + "-" + targetCurrency;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
}
