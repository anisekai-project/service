package me.anisekai.toshiko.events.watchlist;

import me.anisekai.toshiko.entities.Watchlist;
import me.anisekai.toshiko.events.EntityCreatedEvent;

public class WatchlistCreatedEvent extends EntityCreatedEvent<Watchlist> {

    public WatchlistCreatedEvent(Object source, Watchlist entity) {

        super(source, entity);
    }

}
