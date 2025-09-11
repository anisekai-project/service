package fr.anisekai.server.events.torrent;

import fr.anisekai.server.entities.Torrent;
import fr.anisekai.server.events.TorrentUpdatedEvent;
import fr.anisekai.wireless.api.services.Transmission;

public class TorrentStatusUpdatedEvent extends TorrentUpdatedEvent<Transmission.TorrentStatus> {

    public TorrentStatusUpdatedEvent(Object source, Torrent entity, Transmission.TorrentStatus previous, Transmission.TorrentStatus current) {

        super(source, entity, previous, current);
    }

}
