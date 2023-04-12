package me.anisekai.toshiko;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ToshikoApplication {

    @Value("${discord.bot.enabled:true}")
    private boolean botEnabled;

    ToshikoApplication(ToshikoBot bot) {

        if (this.botEnabled) bot.login();
    }

    public static void main(String[] args) {

        SpringApplication.run(ToshikoApplication.class, args);
    }
}
