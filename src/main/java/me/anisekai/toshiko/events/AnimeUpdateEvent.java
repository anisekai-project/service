package me.anisekai.toshiko.events;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.enums.AnimeUpdateType;
import org.springframework.context.ApplicationEvent;

public class AnimeUpdateEvent extends ApplicationEvent {

    private final Anime           anime;
    private final AnimeUpdateType type;

    public AnimeUpdateEvent(Object source, Anime anime, AnimeUpdateType type) {

        super(source);
        this.anime = anime;
        this.type = type;
    }

    public Anime getAnime() {

        return this.anime;
    }

    public AnimeUpdateType getType() {

        return this.type;
    }
}
