package me.anisekai.toshiko.events.anime;

import me.anisekai.toshiko.entities.Anime;
import org.springframework.context.ApplicationEvent;

public class AnimeUpdatedEvent extends ApplicationEvent {

    private final Anime anime;

    public AnimeUpdatedEvent(Object source, Anime anime) {

        super(source);
        this.anime = anime;
    }

    public Anime getAnime() {

        return this.anime;
    }
}
