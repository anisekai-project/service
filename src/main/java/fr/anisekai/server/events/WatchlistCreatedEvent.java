package fr.anisekai.server.events;

import fr.anisekai.server.entities.Watchlist;

/**
 * Event notifying when a {@link Watchlist} is being inserted in the database.
 */
public class WatchlistCreatedEvent extends EntityCreatedEventAdapter<Watchlist> {

    public WatchlistCreatedEvent(Object source, Watchlist entity) {

        super(source, entity);
    }

}
