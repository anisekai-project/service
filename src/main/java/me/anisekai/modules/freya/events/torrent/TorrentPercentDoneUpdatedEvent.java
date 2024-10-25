package me.anisekai.modules.freya.events.torrent;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.freya.entities.Torrent;

public class TorrentPercentDoneUpdatedEvent extends EntityUpdatedEvent<Torrent, Double> {

    public TorrentPercentDoneUpdatedEvent(Object source, Torrent entity, Double previous, Double current) {

        super(source, entity, previous, current);
    }

}
