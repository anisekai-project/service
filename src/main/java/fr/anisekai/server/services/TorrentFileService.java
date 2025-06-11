package fr.anisekai.server.services;

import fr.anisekai.wireless.remote.keys.TorrentKey;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Torrent;
import fr.anisekai.server.entities.TorrentFile;
import fr.anisekai.server.entities.adapters.TorrentFileEventAdapter;
import fr.anisekai.server.persistence.DataService;
import fr.anisekai.server.proxy.TorrentFileProxy;
import fr.anisekai.server.repositories.TorrentFileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TorrentFileService extends DataService<TorrentFile, TorrentKey, TorrentFileEventAdapter, TorrentFileRepository, TorrentFileProxy> {

    public TorrentFileService(TorrentFileProxy proxy) {

        super(proxy);
    }

    public List<TorrentFile> getFiles(Torrent torrent) {

        return this.fetchAll(repo -> repo.findAllByTorrent(torrent));
    }

    public Optional<TorrentFile> getFile(Episode episode) {

        return this.getProxy().fetchEntity(repository -> repository.findByEpisode(episode));
    }

}
