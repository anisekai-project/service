package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.server.entities.Torrent;

public class TorrentUpdatedEvent<V> extends EntityUpdatedEvent<Torrent, V> {

    public TorrentUpdatedEvent(Object source, Torrent entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
