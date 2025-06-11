package me.anisekai.server.events;

import me.anisekai.server.entities.TorrentFile;

/**
 * Event notifying when a {@link TorrentFile} is being inserted in the database.
 */
public class TorrentFileCreatedEvent extends EntityCreatedEventAdapter<TorrentFile> {

    public TorrentFileCreatedEvent(Object source, TorrentFile entity) {

        super(source, entity);
    }

}
