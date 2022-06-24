package me.anisekai.toshiko;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ToshikoApplication {


    public static void main(String[] args) {

        SpringApplication.run(ToshikoApplication.class, args);
    }

    ToshikoApplication(ToshikoBot bot) {

        bot.login();
    }
}
