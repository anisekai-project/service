package me.anisekai.toshiko.events.animenight;

import me.anisekai.toshiko.entities.AnimeNight;
import org.springframework.context.ApplicationEvent;

public class AnimeNightClosedEvent extends ApplicationEvent {

    private final AnimeNight animeNight;

    public AnimeNightClosedEvent(Object source, AnimeNight animeNight) {

        super(source);
        this.animeNight = animeNight;
    }

    public AnimeNight getAnimeNight() {

        return this.animeNight;
    }
}
