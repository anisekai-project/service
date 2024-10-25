package me.anisekai.modules.shizue.repositories;

import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.shizue.entities.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, AnimeStatus> {

}
