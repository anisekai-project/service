package me.anisekai.server.repositories;

import me.anisekai.server.entities.Episode;
import me.anisekai.server.entities.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    Optional<Media> findByEpisode(Episode episode);

}
