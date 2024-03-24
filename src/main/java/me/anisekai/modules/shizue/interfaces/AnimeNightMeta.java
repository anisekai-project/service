package me.anisekai.modules.shizue.interfaces;

import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.events.broadcast.*;

import java.time.ZonedDateTime;

public interface AnimeNightMeta {

    Anime getAnime();

    void setAnime(Anime anime);

    long getAmount();

    @TriggerEvent(BroadcastAmountUpdatedEvent.class)
    void setAmount(long amount);

    long getFirstEpisode();

    @TriggerEvent(BroadcastFirstEpisodeUpdatedEvent.class)
    void setFirstEpisode(long firstEpisode);

    long getLastEpisode();

    @TriggerEvent(BroadcastLastEpisodeUpdatedEvent.class)
    void setLastEpisode(long lastEpisode);

    ZonedDateTime getStartDateTime();

    @TriggerEvent(BroadcastStartDateTimeUpdatedEvent.class)
    void setStartDateTime(ZonedDateTime startDateTime);

    ZonedDateTime getEndDateTime();

    @TriggerEvent(BroadcastEndDateTimeUpdatedEvent.class)
    void setEndDateTime(ZonedDateTime endDateTime);

    default boolean isColliding(ZonedDateTime startTime, ZonedDateTime endTime) {

        boolean isSameStart = this.getStartDateTime().isEqual(startTime);
        boolean isSameEnd   = this.getEndDateTime().isEqual(endTime);

        boolean isStartDuring = this.getStartDateTime().isAfter(startTime) &&
                this.getStartDateTime().isBefore(endTime);

        boolean isEndDuring = this.getEndDateTime().isAfter(startTime) &&
                this.getEndDateTime().isBefore(endTime);

        boolean isOnTop = this.getStartDateTime().isBefore(startTime) &&
                this.getEndDateTime().isAfter(endTime);

        return isSameStart || isSameEnd || isStartDuring || isEndDuring || isOnTop;
    }

    default boolean isColliding(AnimeNightMeta meta) {

        return this.isColliding(meta.getStartDateTime(), meta.getEndDateTime());
    }

}
