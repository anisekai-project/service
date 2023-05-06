package me.anisekai.toshiko.events.anime;

import me.anisekai.toshiko.entities.Anime;
import org.springframework.context.ApplicationEvent;

public class AnimeTotalUpdatedEvent extends ApplicationEvent {

    private final Anime anime;
    private final long  oldValue;
    private final long  newValue;

    public AnimeTotalUpdatedEvent(Object source, Anime anime, long oldValue, long newValue) {

        super(source);
        this.anime    = anime;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Anime getAnime() {

        return this.anime;
    }

    public long getOldValue() {

        return this.oldValue;
    }

    public long getNewValue() {

        return this.newValue;
    }
}
