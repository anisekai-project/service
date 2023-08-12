package me.anisekai.toshiko.listeners;

import me.anisekai.toshiko.events.anime.AnimeWatchedUpdatedEvent;
import me.anisekai.toshiko.services.data.BroadcastDataService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BroadcastListener {

    private final BroadcastDataService service;

    public BroadcastListener(BroadcastDataService service) {

        this.service = service;
    }

    @EventListener
    public void onAnimeWatchedUpdated(AnimeWatchedUpdatedEvent event) {

        this.service.calibrate(event.getEntity());
    }

}
