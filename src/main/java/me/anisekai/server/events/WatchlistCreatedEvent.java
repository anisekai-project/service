package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.server.entities.Watchlist;

/**
 * Event notifying when a {@link Watchlist} is being inserted in the database.
 */
public class WatchlistCreatedEvent extends EntityCreatedEvent<Watchlist> {

    public WatchlistCreatedEvent(Object source, Watchlist entity) {

        super(source, entity);
    }

}
