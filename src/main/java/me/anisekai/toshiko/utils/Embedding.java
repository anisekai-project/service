package me.anisekai.toshiko.utils;

import me.anisekai.toshiko.interfaces.entities.IAnime;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.context.ApplicationEvent;

import java.time.OffsetDateTime;

public class Embedding {

    public static String link(String name, String link) {

        return "[%s](%s)".formatted(name, link);
    }

    public static String link(IAnime anime) {

        return link(anime.getName(), anime.getLink());
    }

    public static EmbedBuilder event(ApplicationEvent event) {

        return new EmbedBuilder()
                .setFooter(event.getSource().getClass().getName())
                .setTimestamp(OffsetDateTime.now());
    }

}
