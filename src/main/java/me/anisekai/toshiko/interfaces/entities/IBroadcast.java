package me.anisekai.toshiko.interfaces.entities;

import me.anisekai.toshiko.events.broadcast.BroadcastImageUrlUpdatedEvent;
import me.anisekai.toshiko.events.broadcast.BroadcastStatusUpdatedEvent;
import me.anisekai.toshiko.interfaces.AnimeNightMeta;
import me.anisekai.toshiko.interfaces.persistence.IEntity;
import me.anisekai.toshiko.helpers.proxy.TriggerEvent;
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

}
