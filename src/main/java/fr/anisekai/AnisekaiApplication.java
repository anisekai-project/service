package fr.anisekai;

import fr.anisekai.discord.DiscordService;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class AnisekaiApplication {

    @SuppressWarnings({"StaticNonFinalField", "CanBeFinal"})
    public static boolean enableDetailedOutput = false;

    public AnisekaiApplication(ListableBeanFactory beanFactory, DiscordService service) {

        service.login(beanFactory);
    }

    public static void main(String... args) {

        SpringApplication.run(AnisekaiApplication.class, args);
    }

}
