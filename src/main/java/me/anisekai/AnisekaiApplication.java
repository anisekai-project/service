package me.anisekai;

import me.anisekai.modules.toshiko.ToshikoBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class AnisekaiApplication {

    AnisekaiApplication(ToshikoBot bot) {

        bot.login();
    }

    public static void main(String... args) {

        SpringApplication.run(AnisekaiApplication.class, args);
    }

}
