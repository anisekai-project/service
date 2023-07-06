package me.anisekai.toshiko.repositories;

import me.anisekai.toshiko.entities.Watchlist;
import me.anisekai.toshiko.enums.AnimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, AnimeStatus> {

}
