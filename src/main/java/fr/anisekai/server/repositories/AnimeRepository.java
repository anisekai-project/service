package fr.anisekai.server.repositories;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.wireless.remote.enums.AnimeList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Long> {

    List<Anime> findByAddedBy(DiscordUser addedBy);

    List<Anime> findAllByList(AnimeList animeStatus);

    List<Anime> findAllByListIn(Collection<AnimeList> animeStatuses);

    List<Anime> findAllByTitleRegexIsNotNull();

    Optional<Anime> findByUrl(String url);

    @Query("""
                select a from Anime a
                where exists (
                    select 1 from Episode e
                    where e.anime = a and e.ready = true
                )
            """)
    List<Anime> findByEpisodeReady();


}
