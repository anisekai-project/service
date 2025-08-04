package fr.anisekai.server.services;

import fr.anisekai.library.services.SpringTransmissionClient;
import fr.anisekai.server.entities.Torrent;
import fr.anisekai.server.entities.adapters.TorrentEventAdapter;
import fr.anisekai.server.persistence.DataService;
import fr.anisekai.server.proxy.TorrentProxy;
import fr.anisekai.server.repositories.TorrentRepository;
import fr.anisekai.wireless.api.services.Transmission;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class TorrentService extends DataService<Torrent, String, TorrentEventAdapter, TorrentRepository, TorrentProxy> {

    private final SpringTransmissionClient client;

    public TorrentService(TorrentProxy proxy, SpringTransmissionClient client) {

        super(proxy);
        this.client = client;
    }

    public SpringTransmissionClient getClient() {

        return this.client;
    }

    public List<Torrent> getAllDownloading() {

        List<Transmission.TorrentStatus> statuses = Arrays.stream(Transmission.TorrentStatus.values())
                                                          .filter(status -> !status.isFinished())
                                                          .filter(status -> status != Transmission.TorrentStatus.UNKNOWN)
                                                          .toList();

        return this.getProxy().fetchEntities(repository -> repository.findByStatusIn(statuses));
    }

    public List<Torrent> getAllFinishedBefore(ZonedDateTime start) {

        List<Transmission.TorrentStatus> statuses = Arrays.stream(Transmission.TorrentStatus.values())
                                                          .filter(Transmission.TorrentStatus::isFinished)
                                                          .toList();
        return this.getProxy()
                   .fetchEntities(repository -> repository.findByStatusInAndUpdatedAtLessThan(statuses, start));
    }

}
