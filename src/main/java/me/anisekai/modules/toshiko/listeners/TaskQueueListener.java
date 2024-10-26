package me.anisekai.modules.toshiko.listeners;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.globals.tasking.TaskingService;
import me.anisekai.globals.tasking.factories.*;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.events.anime.*;
import me.anisekai.modules.shizue.data.Task;
import me.anisekai.modules.shizue.entities.Broadcast;
import me.anisekai.modules.shizue.events.broadcast.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * This class handle all {@link Task} queuing related to application event.
 */
@Component
public class TaskQueueListener {

    private final TaskingService service;

    public TaskQueueListener(TaskingService service) {

        this.service = service;
    }

    @EventListener
    public void onAnimeCreated(AnimeCreatedEvent event) {

        if (event.getEntity().getStatus().shouldDisplayList()) { // Only announce visible anime
            AnnouncementCreateTaskFactory.queue(this.service, event.getEntity());
        }
        AnimeCountTaskFactory.queue(this.service);
    }

    @EventListener({
            AnimeGenresUpdatedEvent.class,
            AnimeNameUpdatedEvent.class,
            AnimeSynopsisUpdatedEvent.class,
            AnimeThemesUpdatedEvent.class
    })
    public void onAnimeUpdated(EntityUpdatedEvent<Anime, ?> event) {

        AnnouncementUpdateTaskFactory.queue(this.service, event.getEntity());
    }

    @EventListener
    public void onAnimeStatusUpdated(AnimeStatusUpdatedEvent event) {

        AnnouncementUpdateTaskFactory.queue(this.service, event.getEntity());

        if (event.getPrevious().shouldDisplayList() != event.getCurrent().shouldDisplayList()) {
            AnimeCountTaskFactory.queue(this.service);
        }
    }

    @EventListener
    public void onAnimeNightCreated(BroadcastCreatedEvent event) {

        BroadcastScheduleTaskFactory.queue(this.service, event.getEntity());
    }

    @EventListener({
            BroadcastAmountUpdatedEvent.class,
            BroadcastEndDateTimeUpdatedEvent.class,
            BroadcastFirstEpisodeUpdatedEvent.class,
            BroadcastStartDateTimeUpdatedEvent.class,
            BroadcastLastEpisodeUpdatedEvent.class
    })
    public void onAnimeNightUpdated(EntityUpdatedEvent<Broadcast, ?> event) {

        BroadcastUpdateTaskFactory.queue(this.service, event.getEntity());
    }

}
