package pl.kathelan.gamemediarekru.schedulers;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.kathelan.gamemediarekru.services.ExchangeService;

import java.util.List;

@Service
public class ScheduledTasks {
    private final ExchangeService exchangeService;

    public ScheduledTasks(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void updateCurrencyRates() {
        exchangeService.updateRates("bitcoin", List.of("USD", "EUR"));
    }
}