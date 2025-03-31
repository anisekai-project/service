package me.anisekai.server.repositories;

import me.anisekai.server.entities.Watchlist;
import me.anisekai.server.enums.AnimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchlistRepository extends JpaRepository<Watchlist, AnimeStatus> {

}
