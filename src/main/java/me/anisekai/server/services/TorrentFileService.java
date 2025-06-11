package me.anisekai.server.services;

import fr.anisekai.wireless.remote.keys.TorrentKey;
import me.anisekai.server.entities.Episode;
import me.anisekai.server.entities.Torrent;
import me.anisekai.server.entities.TorrentFile;
import me.anisekai.server.entities.adapters.TorrentFileEventAdapter;
import me.anisekai.server.persistence.DataService;
import me.anisekai.server.proxy.TorrentFileProxy;
import me.anisekai.server.repositories.TorrentFileRepository;
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
