package me.anisekai.toshiko.events.torrent;

import me.anisekai.toshiko.entities.Torrent;
import me.anisekai.toshiko.events.EntityCreatedEvent;

public class TorrentCreatedEvent extends EntityCreatedEvent<Torrent> {

    public TorrentCreatedEvent(Object source, Torrent entity) {

        super(source, entity);
    }

}
