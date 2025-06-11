package fr.anisekai.server.repositories;

import fr.anisekai.wireless.remote.keys.TorrentKey;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Torrent;
import fr.anisekai.server.entities.TorrentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TorrentFileRepository extends JpaRepository<TorrentFile, TorrentKey> {

    List<TorrentFile> findAllByTorrent(Torrent torrent);

    Optional<TorrentFile> findByEpisode(Episode episode);

}
