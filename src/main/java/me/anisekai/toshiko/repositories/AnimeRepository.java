package me.anisekai.toshiko.repositories;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.enums.AnimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {

    Optional<Anime> findByName(String name);

    List<Anime> findAllByStatus(AnimeStatus status);

    List<Anime> findAllByStatusIn(Collection<AnimeStatus> status);

    @Query("SELECT a FROM Anime a WHERE a.total = -1 AND (a.status = 'WATCHING' OR a.status = 'SIMULCAST' OR a.status = 'SIMULCAST_AVAILABLE')")
    List<Anime> findAllEpisodeUpdatable();
}
