package me.anisekai.toshiko.services;

import me.anisekai.toshiko.components.JdaStore;
import me.anisekai.toshiko.components.RankingHandler;
import me.anisekai.toshiko.entities.Watchlist;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.enums.CronState;
import me.anisekai.toshiko.repositories.WatchlistRepository;
import me.anisekai.toshiko.services.misc.TaskService;
import me.anisekai.toshiko.tasks.WatchlistTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WatchlistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchlistService.class);

    private final WatchlistRepository repository;
    private final RankingHandler      ranking;
    private final TaskService         taskService;
    private final AnimeService        animeService;
    private final JdaStore            store;

    public WatchlistService(WatchlistRepository repository, RankingHandler ranking, TaskService taskService, AnimeService animeService, JdaStore store) {

        this.repository   = repository;
        this.ranking      = ranking;
        this.taskService  = taskService;
        this.animeService = animeService;
        this.store        = store;
    }

    private void onPostPersist(Watchlist watchlist) {

        watchlist.setAnimes(this.animeService.findByStatus(watchlist.getStatus()));
    }

    public WatchlistRepository getRepository() {

        return this.repository;
    }

    /**
     * Remove all existing {@link Watchlist} entities from the database and re-create them.
     */
    public void createWatchlists() {

        LOGGER.warn("createWatchlists: Deleting every watchlist !");
        this.repository.deleteAll();
        this.repository.flush();

        LOGGER.info("createWatchlists: Re-creating entities...");
        List<Watchlist> watchlists = AnimeStatus.getDisplayable()
                                                .stream()
                                                .map(Watchlist::new)
                                                .peek(watchlist -> watchlist.setState(CronState.NONE))
                                                .toList();

        List<Watchlist> saved = this.repository.saveAll(watchlists);
        saved.forEach(this::onPostPersist);

        for (Watchlist watchlist : saved) {
            this.taskService.queue(new WatchlistTask(this, this.ranking, watchlist, this.store.getWatchlistChannel()));
        }
    }

    /**
     * Queue every {@link Watchlist} for a refresh
     */
    public void updateAll() {

        LOGGER.info("updateAll: Requesting CRON update for every watchlists...");
        List<Watchlist> watchlists = this.repository.findAll();

        for (Watchlist watchlist : watchlists) {
            this.taskService.queue(new WatchlistTask(this, this.ranking, watchlist, this.store.getWatchlistChannel()));
        }
    }

    /**
     * Queue the {@link Watchlist} having the provided {@link AnimeStatus} for a refresh.
     *
     * @param status
     *         The {@link Watchlist}'s {@link AnimeStatus}
     */
    public void update(AnimeStatus status) {

        LOGGER.info("update: Requesting CRON update for watchlist {}", status.name());
        this.repository.findById(status)
                       .ifPresent(watchlist -> this.taskService.queue(new WatchlistTask(
                               this,
                               this.ranking,
                               watchlist,
                               this.store.getWatchlistChannel()
                       )));
    }

}
