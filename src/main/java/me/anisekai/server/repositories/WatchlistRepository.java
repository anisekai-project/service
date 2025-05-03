package me.anisekai.server.repositories;

import fr.anisekai.wireless.remote.enums.AnimeList;
import me.anisekai.server.entities.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchlistRepository extends JpaRepository<Watchlist, AnimeList> {

}
