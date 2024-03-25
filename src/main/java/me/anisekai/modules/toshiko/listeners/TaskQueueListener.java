package me.anisekai.modules.toshiko.listeners;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.events.anime.*;
import me.anisekai.modules.shizue.data.Task;
import me.anisekai.modules.shizue.entities.Broadcast;
import me.anisekai.modules.shizue.events.broadcast.*;
import me.anisekai.modules.shizue.services.RateLimitedTaskService;
import me.anisekai.modules.linn.services.data.AnimeDataService;
import me.anisekai.modules.shizue.services.data.BroadcastDataService;
import me.anisekai.modules.toshiko.JdaStore;
import me.anisekai.modules.toshiko.tasks.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * This class handle all {@link Task} queuing related to application event.
 */
@Component
public class TaskQueueListener {

    private final RateLimitedTaskService service;
    private final JdaStore               store;

    private final AnimeDataService     animeService;
    private final BroadcastDataService broadcastService;

    public TaskQueueListener(RateLimitedTaskService service, JdaStore store, AnimeDataService animeService, BroadcastDataService broadcastService) {

        this.service          = service;
        this.store            = store;
        this.animeService     = animeService;
        this.broadcastService = broadcastService;
    }

    @EventListener
    public void onAnimeCreated(AnimeCreatedEvent event) {

        if (event.getEntity().getStatus().shouldDisplayList()) { // Only announce visible anime
            this.service.queue(new SendAnnouncementTask(this.animeService, this.store, event.getEntity()));
        }

        this.service.queue(new AnimeCountTask(this.animeService, this.store.getWatchlistChannel()));
    }

    @EventListener({
            AnimeGenresUpdatedEvent.class,
            AnimeNameUpdatedEvent.class,
            AnimeSynopsisUpdatedEvent.class,
            AnimeThemesUpdatedEvent.class
    })
    public void onAnimeUpdated(EntityUpdatedEvent<Anime, ?> event) {

        this.service.queue(new UpdateAnnouncementTask(this.store, event.getEntity()));
    }

    @EventListener
    public void onAnimeStatusUpdated(AnimeStatusUpdatedEvent event) {

        this.service.queue(new UpdateAnnouncementTask(this.store, event.getEntity()));

        if (event.getPrevious().shouldDisplayList() != event.getCurrent().shouldDisplayList()) {
            this.service.queue(new AnimeCountTask(this.animeService, this.store.getWatchlistChannel()));
        }
    }

    @EventListener
    public void onAnimeNightCreated(BroadcastCreatedEvent event) {

        this.service.queue(new ScheduleAnimeNightTask(
                this.broadcastService,
                this.store.getBotGuild(),
                event.getEntity()
        ));
    }

    @EventListener({
            BroadcastAmountUpdatedEvent.class,
            BroadcastEndDateTimeUpdatedEvent.class,
            BroadcastFirstEpisodeUpdatedEvent.class,
            BroadcastStartDateTimeUpdatedEvent.class,
            BroadcastLastEpisodeUpdatedEvent.class
    })
    public void onAnimeNightUpdated(EntityUpdatedEvent<Broadcast, ?> event) {

        this.service.queue(new UpdateBroadcastTask(
                this.store.getBotGuild(),
                event.getEntity()
        ));
    }

}
