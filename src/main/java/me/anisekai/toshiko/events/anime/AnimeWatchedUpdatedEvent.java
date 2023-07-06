package me.anisekai.toshiko.events.anime;

import me.anisekai.toshiko.entities.Anime;
import org.springframework.context.ApplicationEvent;

public class AnimeWatchedUpdatedEvent extends ApplicationEvent {

    private final Anime anime;
    private final long  oldWatched;
    private final long  newWatched;

    public AnimeWatchedUpdatedEvent(Object source, Anime anime, long oldWatched, long newWatched) {

        super(source);
        this.anime      = anime;
        this.oldWatched = oldWatched;
        this.newWatched = newWatched;
    }

    public Anime getAnime() {

        return this.anime;
    }

    public long getOldWatched() {

        return this.oldWatched;
    }

    public long getNewWatched() {

        return this.newWatched;
    }

}
