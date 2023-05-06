package me.anisekai.toshiko.listeners;

import me.anisekai.toshiko.components.JdaStore;
import me.anisekai.toshiko.data.Task;
import me.anisekai.toshiko.events.anime.AnimeCreatedEvent;
import me.anisekai.toshiko.events.anime.AnimeStatusUpdatedEvent;
import me.anisekai.toshiko.events.anime.AnimeUpdatedEvent;
import me.anisekai.toshiko.events.animenight.AnimeNightCreatedEvent;
import me.anisekai.toshiko.events.animenight.AnimeNightUpdatedEvent;
import me.anisekai.toshiko.services.AnimeNightService;
import me.anisekai.toshiko.services.AnimeService;
import me.anisekai.toshiko.services.misc.TaskService;
import me.anisekai.toshiko.tasks.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * This class handle all {@link Task} queuing related to application event.
 */
@Component
public class TaskQueueListener {

    private final TaskService service;
    private final JdaStore    store;

    private final AnimeService      animeService;
    private final AnimeNightService animeNightService;

    public TaskQueueListener(TaskService service, JdaStore store, AnimeService animeService, AnimeNightService animeNightService) {

        this.service           = service;
        this.store             = store;
        this.animeService      = animeService;
        this.animeNightService = animeNightService;
    }

    @EventListener
    public void onAnimeCreated(AnimeCreatedEvent event) {

        this.service.queue(new SendAnnouncementTask(this.animeService, this.store, event.getAnime()));
        this.service.queue(new AnimeCountTask(this.animeService, this.store.getWatchlistChannel()));
    }

    @EventListener
    public void onAnimeUpdated(AnimeUpdatedEvent event) {

        this.service.queue(new UpdateAnnouncementTask(this.animeService, this.store, event.getAnime()));
        this.service.queue(new AnimeCountTask(this.animeService, this.store.getWatchlistChannel()));
    }

    @EventListener
    public void onAnimeStatusUpdated(AnimeStatusUpdatedEvent event) {

        this.service.queue(new UpdateAnnouncementTask(this.animeService, this.store, event.getAnime()));
        this.service.queue(new AnimeCountTask(this.animeService, this.store.getWatchlistChannel()));
    }

    @EventListener
    public void onAnimeNightCreated(AnimeNightCreatedEvent event) {

        this.service.queue(new ScheduleAnimeNightTask(this.animeNightService, this.store.getBotGuild(), event.getAnimeNight()));
    }

    @EventListener
    public void onAnimeNightUpdated(AnimeNightUpdatedEvent event) {

        this.service.queue(new UpdateAnimeNightTask(this.animeNightService, this.store.getBotGuild(), event.getAnimeNight()));
    }
}
