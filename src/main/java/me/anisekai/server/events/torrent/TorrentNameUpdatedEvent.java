package me.anisekai.server.events.torrent;

import me.anisekai.server.entities.Torrent;
import me.anisekai.server.events.TorrentUpdatedEvent;

public class TorrentNameUpdatedEvent extends TorrentUpdatedEvent<String> {

    public TorrentNameUpdatedEvent(Object source, Torrent entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
