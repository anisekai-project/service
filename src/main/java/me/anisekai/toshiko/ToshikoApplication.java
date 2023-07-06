package me.anisekai.toshiko;

import me.anisekai.toshiko.configurations.ToshikoFeatureConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ToshikoApplication {

    ToshikoApplication(ToshikoBot bot, ToshikoFeatureConfiguration featureConfiguration) {

        if (featureConfiguration.isBotEnabled()) {
            bot.login();
        }
    }

    public static void main(String[] args) {

        SpringApplication.run(ToshikoApplication.class, args);
    }

}
