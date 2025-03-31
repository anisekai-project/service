package me.anisekai.server.repositories;

import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.enums.AnimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {

    List<Anime> findByAddedBy(DiscordUser addedBy);

    Optional<Anime> findByTitle(String title);

    List<Anime> findAllByStatus(AnimeStatus animeStatus);

    List<Anime> findAllByStatusIn(List<AnimeStatus> animeStatuses);

    List<Anime> findAllByTitleRegexIsNotNull();

}
