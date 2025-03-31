package me.anisekai.server.repositories;

import me.anisekai.api.transmission.TorrentStatus;
import me.anisekai.server.entities.Episode;
import me.anisekai.server.entities.Torrent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TorrentRepository extends JpaRepository<Torrent, String> {

    Optional<Torrent> findByEpisode(Episode episode);

    List<Torrent> findByStatusIn(Collection<TorrentStatus> statuses);

}
