package me.anisekai.toshiko.repositories;

import me.anisekai.toshiko.entities.Watchlist;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.enums.CronState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, AnimeStatus> {

    List<Watchlist> findAllByState(CronState state);

}
