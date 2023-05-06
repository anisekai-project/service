package me.anisekai.toshiko.events.anime;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.enums.AnimeStatus;
import org.springframework.context.ApplicationEvent;

public class AnimeStatusUpdatedEvent extends ApplicationEvent {

    private final Anime       anime;
    private final AnimeStatus oldValue;
    private final AnimeStatus newValue;

    public AnimeStatusUpdatedEvent(Object source, Anime anime, AnimeStatus oldValue, AnimeStatus newValue) {

        super(source);
        this.anime    = anime;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Anime getAnime() {

        return this.anime;
    }

    public AnimeStatus getOldValue() {

        return this.oldValue;
    }

    public AnimeStatus getNewValue() {

        return this.newValue;
    }
}
