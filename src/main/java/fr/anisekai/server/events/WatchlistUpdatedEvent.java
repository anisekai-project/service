package fr.anisekai.server.events;

import fr.anisekai.server.entities.Watchlist;

/**
 * Event notifying when a {@link Watchlist} is being inserted in the database.
 */
public class WatchlistUpdatedEvent<V> extends EntityUpdatedEventAdapter<Watchlist, V> {

    public WatchlistUpdatedEvent(Object source, Watchlist entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
