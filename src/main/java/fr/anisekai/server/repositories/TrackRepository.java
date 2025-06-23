package fr.anisekai.server.repositories;

import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

    List<Track> findByEpisode(Episode episode);

    long deleteByEpisode(Episode episode);

}
