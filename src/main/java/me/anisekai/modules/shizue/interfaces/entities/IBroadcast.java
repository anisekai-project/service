package me.anisekai.modules.shizue.interfaces.entities;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.api.plannifier.EventScheduler;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.events.broadcast.BroadcastImageUrlUpdatedEvent;
import me.anisekai.modules.shizue.events.broadcast.BroadcastStatusUpdatedEvent;
import me.anisekai.modules.shizue.interfaces.AnimeNightMeta;
import net.dv8tion.jda.api.entities.ScheduledEvent;

public interface IBroadcast extends IEntity<Long>, AnimeNightMeta, Comparable<AnimeNightMeta> {

    Long getEventId();

    void setEventId(Long eventId);

    ScheduledEvent.Status getStatus();

    @TriggerEvent(BroadcastStatusUpdatedEvent.class)
    void setStatus(ScheduledEvent.Status status);

    String getImageUrl();

    @TriggerEvent(BroadcastImageUrlUpdatedEvent.class)
    void setImageUrl(String imageUrl);

    void setWatchTarget(Anime anime);

    /**
     * Check if the event is in a scheduled state. Scheduled event will be used in {@link EventScheduler} as valid
     * events.
     *
     * @return True if the event is in a scheduled state, false otherwise.
     */
    boolean isScheduled();

    /**
     * Define if the event is in a scheduled state. Scheduled event will be used in {@link EventScheduler} as valid
     * events.
     *
     * @param scheduled
     *         True if the event is in a scheduled state, false otherwise.
     */
    void setScheduled(boolean scheduled);

}
