package me.anisekai.server.events.torrent;

import me.anisekai.server.entities.Torrent;
import me.anisekai.server.events.TorrentUpdatedEvent;

public class TorrentProgressUpdatedEvent extends TorrentUpdatedEvent<Double> {

    public TorrentProgressUpdatedEvent(Object source, Torrent entity, Double previous, Double current) {

        super(source, entity, previous, current);
    }

}
