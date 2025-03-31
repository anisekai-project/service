package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.server.entities.Selection;
import me.anisekai.server.entities.Torrent;

/**
 * Event notifying when a {@link Selection} is being inserted in the database.
 */
public class TorrentCreatedEvent extends EntityCreatedEvent<Torrent> {

    public TorrentCreatedEvent(Object source, Torrent entity) {

        super(source, entity);
    }

}
