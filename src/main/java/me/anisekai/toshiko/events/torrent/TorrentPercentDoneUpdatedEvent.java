package me.anisekai.toshiko.events.torrent;

import me.anisekai.toshiko.entities.Torrent;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class TorrentPercentDoneUpdatedEvent extends EntityUpdatedEvent<Torrent, Double> {

    public TorrentPercentDoneUpdatedEvent(Object source, Torrent entity, Double previous, Double current) {

        super(source, entity, previous, current);
    }

}
