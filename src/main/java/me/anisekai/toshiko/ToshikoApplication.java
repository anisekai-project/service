package me.anisekai.toshiko;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ToshikoApplication {

    ToshikoApplication(ToshikoBot bot) {

        bot.login();
    }

    public static void main(String[] args) {

        SpringApplication.run(ToshikoApplication.class, args);
    }
}
