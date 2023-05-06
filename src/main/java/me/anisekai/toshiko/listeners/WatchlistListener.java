package me.anisekai.toshiko.listeners;

import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.events.anime.*;
import me.anisekai.toshiko.events.user.UserEmoteUpdatedEvent;
import me.anisekai.toshiko.services.WatchlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class WatchlistListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchlistListener.class);

    private final WatchlistService service;

    public WatchlistListener(WatchlistService service) {

        this.service = service;
    }

    @EventListener
    public void onAnimeStatusUpdated(AnimeStatusUpdatedEvent event) {

        LOGGER.info(
                "onAnimeStatusUpdated: Anime {} - {} > {}",
                event.getAnime().getId(),
                event.getOldValue().name(),
                event.getNewValue().name()
        );

        if (event.getOldValue().shouldDisplayList()) {
            this.service.update(event.getOldValue());
        }

        if (event.getNewValue().shouldDisplayList() && event.getOldValue() != event.getNewValue()) {
            this.service.update(event.getNewValue());
        }
    }

    @EventListener
    public void onAnimeWatchedUpdated(AnimeWatchedUpdatedEvent event) {

        LOGGER.info(
                "onAnimeWatchedUpdated: Anime {} - {} > {}",
                event.getAnime().getId(),
                event.getOldWatched(),
                event.getNewWatched()
        );

        AnimeStatus status = event.getAnime().getStatus();

        if (status == AnimeStatus.WATCHING || status == AnimeStatus.SIMULCAST) {
            this.service.update(status);
        }
    }

    @EventListener
    public void onAnimeTotalUpdated(AnimeTotalUpdatedEvent event) {

        LOGGER.info(
                "onAnimeTotalUpdated: Anime {} - {} > {}",
                event.getAnime().getId(),
                event.getOldValue(),
                event.getNewValue()
        );

        AnimeStatus status = event.getAnime().getStatus();

        if (status == AnimeStatus.WATCHING || status == AnimeStatus.SIMULCAST) {
            this.service.update(status);
        }
    }

    @EventListener
    public void onAnimeCreated(AnimeCreatedEvent event) {

        LOGGER.info("onAnimeCreated: Anime {}", event.getAnime().getId());

        this.service.update(event.getAnime().getStatus());
    }

    @EventListener
    public void onAnimeUpdated(AnimeUpdatedEvent event) {

        LOGGER.info("onAnimeUpdated: Anime {}", event.getAnime().getId());
        this.service.update(event.getAnime().getStatus());
    }

    @EventListener
    public void onUserEmoteUpdated(UserEmoteUpdatedEvent event) {

        LOGGER.info("onUserEmoteUpdated: User {}", event.getUser().getId());
        this.service.updateAll();
    }
}
