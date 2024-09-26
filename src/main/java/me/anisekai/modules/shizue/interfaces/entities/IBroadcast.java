package me.anisekai.modules.shizue.interfaces.entities;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
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
}
