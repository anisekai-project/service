package me.anisekai.server.repositories;

import fr.anisekai.wireless.api.services.Transmission;
import me.anisekai.server.entities.Torrent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TorrentRepository extends JpaRepository<Torrent, String> {

    List<Torrent> findByStatusIn(Collection<Transmission.TorrentStatus> statuses);

}
