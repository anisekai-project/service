package fr.anisekai.server.events;

import fr.anisekai.server.entities.Torrent;

/**
 * Event notifying when a {@link Torrent} is being inserted in the database.
 */
public class TorrentCreatedEvent extends EntityCreatedEventAdapter<Torrent> {

    public TorrentCreatedEvent(Object source, Torrent entity) {

        super(source, entity);
    }

}
