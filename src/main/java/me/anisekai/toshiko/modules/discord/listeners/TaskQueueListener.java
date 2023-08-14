package me.anisekai.toshiko.modules.discord.listeners;

import me.anisekai.toshiko.data.Task;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.Broadcast;
import me.anisekai.toshiko.events.EntityUpdatedEvent;
import me.anisekai.toshiko.events.anime.*;
import me.anisekai.toshiko.events.broadcast.*;
import me.anisekai.toshiko.modules.discord.JdaStore;
import me.anisekai.toshiko.modules.discord.tasks.*;
import me.anisekai.toshiko.services.TaskService;
import me.anisekai.toshiko.services.data.AnimeDataService;
import me.anisekai.toshiko.services.data.BroadcastDataService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * This class handle all {@link Task} queuing related to application event.
 */
@Component
public class TaskQueueListener {

    private final TaskService service;
    private final JdaStore    store;

    private final AnimeDataService     animeService;
    private final BroadcastDataService broadcastService;

    public TaskQueueListener(TaskService service, JdaStore store, AnimeDataService animeService, BroadcastDataService broadcastService) {

        this.service          = service;
        this.store            = store;
        this.animeService     = animeService;
        this.broadcastService = broadcastService;
    }

    @EventListener
    public void onAnimeCreated(AnimeCreatedEvent event) {

        this.service.queue(new SendAnnouncementTask(this.animeService, this.store, event.getEntity()));
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
