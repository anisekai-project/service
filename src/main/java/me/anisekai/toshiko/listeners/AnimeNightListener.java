package me.anisekai.toshiko.listeners;

import me.anisekai.toshiko.events.anime.AnimeUpdatedEvent;
import me.anisekai.toshiko.events.anime.AnimeWatchedUpdatedEvent;
import me.anisekai.toshiko.services.AnimeNightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AnimeNightListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnimeNightListener.class);

    private final AnimeNightService service;

    public AnimeNightListener(AnimeNightService service) {

        this.service = service;
    }

    @EventListener
    public void onAnimeWatchedUpdated(AnimeWatchedUpdatedEvent event) {

        LOGGER.info(
                "onAnimeWatchedUpdated: Anime {} - {} > {}",
                event.getAnime().getId(),
                event.getOldWatched(),
                event.getNewWatched()
        );

        this.service.calibrate(event.getAnime());
    }

    @EventListener
    public void onAnimeUpdated(AnimeUpdatedEvent event) {

        LOGGER.info(
                "onAnimeUpdated: Anime {}",
                event.getAnime().getId()
        );

        this.service.calibrate(event.getAnime());
    }

}
