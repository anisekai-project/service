package me.anisekai.server.services;

import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.api.transmission.NyaaRssEntry;
import me.anisekai.api.transmission.TorrentStatus;
import me.anisekai.api.transmission.TransmissionTorrent;
import me.anisekai.library.services.SpringTransmissionClient;
import me.anisekai.server.entities.Episode;
import me.anisekai.server.entities.Torrent;
import me.anisekai.server.interfaces.ITorrent;
import me.anisekai.server.proxy.TorrentProxy;
import me.anisekai.server.repositories.TorrentRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TorrentService extends DataService<Torrent, String, ITorrent<Episode>, TorrentRepository, TorrentProxy> {

    private final SpringTransmissionClient client;

    public TorrentService(TorrentProxy proxy, SpringTransmissionClient client) {

        super(proxy);
        this.client = client;
    }

    public SpringTransmissionClient getClient() {

        return this.client;
    }

    public Torrent download(NyaaRssEntry entry, Episode episode, long priority) throws Exception {

        TransmissionTorrent response = this.client.offerTorrent(entry);

        return this.getProxy().create(torrent -> {
            torrent.setId(response.getHash());
            torrent.setName(entry.getTitle());
            torrent.setEpisode(episode);
            torrent.setStatus(TorrentStatus.VERIFY_QUEUED);
            torrent.setProgress(0);
            torrent.setLink(entry.getLink());
            torrent.setDownloadDirectory(response.getDownloadDir());
            torrent.setFileName(response.getFile());
            torrent.setPriority(priority);
        });
    }

    public Optional<Torrent> getTorrent(Episode episode) {

        return this.getProxy().fetchEntity(repo -> repo.findByEpisode(episode));
    }

    public List<Torrent> getAllDownloading() {

        List<TorrentStatus> statuses = Arrays.stream(TorrentStatus.values())
                                             .filter(status -> !status.isFinished())
                                             .toList();

        return this.getProxy().fetchEntities(repository -> repository.findByStatusIn(statuses));
    }

}
