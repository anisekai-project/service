package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.server.entities.Watchlist;

/**
 * Event notifying when a {@link Watchlist} is being inserted in the database.
 */
public class WatchlistUpdatedEvent<V> extends EntityUpdatedEvent<Watchlist, V> {

    public WatchlistUpdatedEvent(Object source, Watchlist entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
