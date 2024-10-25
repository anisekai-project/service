package me.anisekai.modules.shizue.listeners;

import me.anisekai.modules.linn.events.anime.AnimeWatchedUpdatedEvent;
import me.anisekai.modules.shizue.services.data.BroadcastDataService;
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

        this.service.createScheduler().calibrate(); // TODO: Send report to admin channel
    }

}
