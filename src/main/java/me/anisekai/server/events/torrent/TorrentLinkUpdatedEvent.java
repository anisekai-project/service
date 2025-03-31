package me.anisekai.server.events.torrent;

import me.anisekai.server.entities.Torrent;
import me.anisekai.server.events.TorrentUpdatedEvent;

public class TorrentLinkUpdatedEvent extends TorrentUpdatedEvent<String> {

    public TorrentLinkUpdatedEvent(Object source, Torrent entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
