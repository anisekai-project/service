package fr.anisekai.server.repositories;

import fr.anisekai.server.entities.Torrent;
import fr.anisekai.wireless.api.services.Transmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface TorrentRepository extends JpaRepository<Torrent, String> {

    List<Torrent> findByStatusIn(Collection<Transmission.TorrentStatus> statuses);

    List<Torrent> findByStatusInAndUpdatedAtLessThan(Collection<Transmission.TorrentStatus> statuses, ZonedDateTime updatedAt);

}
