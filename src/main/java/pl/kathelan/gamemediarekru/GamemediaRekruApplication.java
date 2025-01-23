package pl.kathelan.gamemediarekru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GamemediaRekruApplication {

    public static void main(String[] args) {
        SpringApplication.run(GamemediaRekruApplication.class, args);
    }

}
