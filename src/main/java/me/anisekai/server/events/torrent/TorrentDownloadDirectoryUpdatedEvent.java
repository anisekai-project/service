package me.anisekai.server.events.torrent;

import me.anisekai.server.entities.Torrent;
import me.anisekai.server.events.TorrentUpdatedEvent;

public class TorrentDownloadDirectoryUpdatedEvent extends TorrentUpdatedEvent<String> {

    public TorrentDownloadDirectoryUpdatedEvent(Object source, Torrent entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
