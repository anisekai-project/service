package fr.anisekai.server.repositories;

import fr.anisekai.server.entities.Watchlist;
import fr.anisekai.wireless.remote.enums.AnimeList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchlistRepository extends JpaRepository<Watchlist, AnimeList> {

}
