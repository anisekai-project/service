package me.anisekai.toshiko.events.torrent;

import me.anisekai.toshiko.entities.Torrent;
import me.anisekai.toshiko.enums.TorrentStatus;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class TorrentStatusUpdatedEvent extends EntityUpdatedEvent<Torrent, TorrentStatus> {

    public TorrentStatusUpdatedEvent(Object source, Torrent entity, TorrentStatus previous, TorrentStatus current) {

        super(source, entity, previous, current);
    }

}
