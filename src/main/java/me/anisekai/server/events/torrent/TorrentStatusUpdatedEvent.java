package me.anisekai.server.events.torrent;

import me.anisekai.api.transmission.TorrentStatus;
import me.anisekai.server.entities.Torrent;
import me.anisekai.server.events.TorrentUpdatedEvent;

public class TorrentStatusUpdatedEvent extends TorrentUpdatedEvent<TorrentStatus> {

    public TorrentStatusUpdatedEvent(Object source, Torrent entity, TorrentStatus previous, TorrentStatus current) {

        super(source, entity, previous, current);
    }

}
