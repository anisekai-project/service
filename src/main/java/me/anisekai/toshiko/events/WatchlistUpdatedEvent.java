package me.anisekai.toshiko.events;

import me.anisekai.toshiko.enums.AnimeStatus;
import org.springframework.context.ApplicationEvent;

public class WatchlistUpdatedEvent extends ApplicationEvent {

    private final AnimeStatus status;

    public WatchlistUpdatedEvent(Object source, AnimeStatus status) {

        super(source);
        this.status = status;
    }

    public AnimeStatus getStatus() {

        return this.status;
    }
}
