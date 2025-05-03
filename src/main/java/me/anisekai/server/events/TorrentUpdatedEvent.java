package me.anisekai.server.events;

import me.anisekai.server.entities.Torrent;

public class TorrentUpdatedEvent<V> extends EntityUpdatedEventAdapter<Torrent, V> {

    public TorrentUpdatedEvent(Object source, Torrent entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
