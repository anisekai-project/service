package fr.anisekai.server.repositories;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    Optional<Episode> findByAnimeAndNumber(Anime anime, int number);

    List<Episode> findAllByReadyTrue();

}
