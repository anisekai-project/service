package me.anisekai.toshiko.tasks;


import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.enums.AnimeUpdateType;
import me.anisekai.toshiko.events.AnimeUpdateEvent;
import me.anisekai.toshiko.events.WatchlistUpdatedEvent;
import me.anisekai.toshiko.interfaces.AnimeProvider;
import me.anisekai.toshiko.repositories.AnimeRepository;
import me.anisekai.toshiko.services.AnimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class AnimeTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnimeTask.class);

    private final BlockingDeque<Anime>      animes;
    private final ApplicationEventPublisher publisher;
    private final AnimeRepository           repository;
    private final AnimeService              service;

    public AnimeTask(ApplicationEventPublisher publisher, AnimeRepository repository, AnimeService service) {

        this.publisher  = publisher;
        this.repository = repository;
        this.service    = service;
        this.animes     = new LinkedBlockingDeque<>();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void execute() {

        this.service.getUpdatableAnime().forEach(this.animes::offer);
    }

    @Scheduled(cron = "0 * * * * *")
    public void listBadEpisodeCount() {

        this.repository.findAllEpisodeUpdatable().forEach(this.animes::offer);
    }

    @Scheduled(cron = "0/5 * * * * *")
    public void check() {

        Anime anime = this.animes.poll();

        if (anime == null) {
            return;
        }

        LOGGER.info("({} left) Checking anime {} ({})", this.animes.size(), anime.getId(), anime.getName());

        try {
            AnimeProvider   provider = AnimeProvider.of(anime.getLink());
            AnimeUpdateType type     = null;

            if (provider.getEpisodeCount().isPresent()) {
                type = AnimeUpdateType.UPDATE;
                anime.setTotal(provider.getEpisodeCount().get());
                this.repository.save(anime);
            }

            if (provider.getRating().isPresent()) {
                type = AnimeUpdateType.UPDATE;
            }

            switch (provider.getPublicationState()) {
                case FINISHED -> {
                    if (anime.getStatus() == AnimeStatus.SIMULCAST_AVAILABLE) {
                        anime.setStatus(AnimeStatus.NOT_DOWNLOADED);
                        this.repository.save(anime);
                        type = AnimeUpdateType.RELEASED;
                        this.publisher.publishEvent(new WatchlistUpdatedEvent(this, AnimeStatus.SIMULCAST_AVAILABLE));
                        this.publisher.publishEvent(new WatchlistUpdatedEvent(this, AnimeStatus.NOT_DOWNLOADED));
                    } else if (anime.getStatus() == AnimeStatus.SIMULCAST) {
                        anime.setStatus(AnimeStatus.WATCHING);
                        this.repository.save(anime);
                        this.publisher.publishEvent(new WatchlistUpdatedEvent(this, AnimeStatus.SIMULCAST));
                        this.publisher.publishEvent(new WatchlistUpdatedEvent(this, AnimeStatus.WATCHING));
                        type = AnimeUpdateType.RELEASED;
                    }
                }
                case AIRING -> {
                    if (anime.getStatus() == AnimeStatus.UNAVAILABLE) {
                        anime.setStatus(AnimeStatus.SIMULCAST_AVAILABLE);
                        this.repository.save(anime);
                        type = AnimeUpdateType.RELEASING;
                        this.publisher.publishEvent(new WatchlistUpdatedEvent(this, AnimeStatus.UNAVAILABLE));
                        this.publisher.publishEvent(new WatchlistUpdatedEvent(this, AnimeStatus.SIMULCAST_AVAILABLE));
                    }
                }
            }

            if (type != null) {
                this.publisher.publishEvent(new AnimeUpdateEvent(this, anime, type));
                this.publisher.publishEvent(new WatchlistUpdatedEvent(this, anime.getStatus()));
            }

        } catch (Exception e) {
            LOGGER.error("An error occured", e);
        }
    }
}
