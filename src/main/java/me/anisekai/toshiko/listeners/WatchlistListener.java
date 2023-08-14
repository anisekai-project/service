package me.anisekai.toshiko.listeners;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.events.anime.AnimeCreatedEvent;
import me.anisekai.toshiko.events.anime.AnimeStatusUpdatedEvent;
import me.anisekai.toshiko.events.anime.AnimeTotalUpdatedEvent;
import me.anisekai.toshiko.events.anime.AnimeWatchedUpdatedEvent;
import me.anisekai.toshiko.events.user.UserActiveUpdatedEvent;
import me.anisekai.toshiko.events.user.UserEmoteUpdatedEvent;
import me.anisekai.toshiko.services.data.WatchlistDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class WatchlistListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchlistListener.class);

    private final WatchlistDataService service;

    public WatchlistListener(WatchlistDataService service) {

        this.service = service;
    }

    @EventListener
    public void onAnimeStatusUpdated(AnimeStatusUpdatedEvent event) {

        if (event.getPrevious().shouldDisplayList()) {
            this.service.refresh(event.getPrevious());
        }

        if (event.getCurrent().shouldDisplayList()) {
            this.service.refresh(event.getCurrent());
        }
    }

    @EventListener
    public void onAnimeWatchedUpdated(AnimeWatchedUpdatedEvent event) {

        Anime anime = event.getEntity();

        if (anime.getStatus().shouldDisplayList()) {
            this.service.refresh(anime.getStatus());
        }
    }

    @EventListener
    public void onAnimeTotalUpdated(AnimeTotalUpdatedEvent event) {

        Anime anime = event.getEntity();

        if (anime.getStatus().shouldDisplayList()) {
            this.service.refresh(anime.getStatus());
        }
    }

    @EventListener
    public void onAnimeCreated(AnimeCreatedEvent event) {

        Anime anime = event.getEntity();

        if (anime.getStatus().shouldDisplayList()) {
            this.service.refresh(anime.getStatus());
        }
    }

    @EventListener({UserEmoteUpdatedEvent.class, UserActiveUpdatedEvent.class})
    public void requireRefresh() {

        this.service.refreshAll();
    }

}
