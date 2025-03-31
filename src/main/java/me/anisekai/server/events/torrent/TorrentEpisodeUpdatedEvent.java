package me.anisekai.server.events.torrent;

import me.anisekai.server.entities.Episode;
import me.anisekai.server.entities.Torrent;
import me.anisekai.server.events.TorrentUpdatedEvent;

public class TorrentEpisodeUpdatedEvent extends TorrentUpdatedEvent<Episode> {

    public TorrentEpisodeUpdatedEvent(Object source, Torrent entity, Episode previous, Episode current) {

        super(source, entity, previous, current);
    }

}
