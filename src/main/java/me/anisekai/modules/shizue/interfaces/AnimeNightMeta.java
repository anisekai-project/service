package me.anisekai.modules.shizue.interfaces;

import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.api.plannifier.interfaces.Plannifiable;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.events.broadcast.*;

import java.time.ZonedDateTime;

public interface AnimeNightMeta extends Plannifiable<Anime> {

    // <editor-fold desc="Trigger Overrides — @TriggerEvent applied to superclass(es)">

    @Override
    @TriggerEvent(BroadcastAmountUpdatedEvent.class)
    void setEpisodeCount(long episodeCount);

    @Override
    @TriggerEvent(BroadcastFirstEpisodeUpdatedEvent.class)
    void setFirstEpisode(long firstEpisode);

    @Override
    @TriggerEvent(BroadcastStartDateTimeUpdatedEvent.class)
    void setStartingAt(ZonedDateTime time);

    // </editor-fold>

    /**
     * Check if the current {@link AnimeNightMeta} collides with the provided {@link ZonedDateTime}s.
     *
     * @param otherStartingAt
     *         The {@link ZonedDateTime} at which the other event may be scheduled.
     * @param otherEndingAt
     *         The {@link ZonedDateTime} at which the other event ends.
     *
     * @return True if it collides with the current {@link AnimeNightMeta}, false otherwise.
     *
     * @deprecated This is used in the now deprecated scheduler v1, {@link BroadcastScheduler}. Please use
     *         {@link AdvancedScheduler} now.
     */
    @Deprecated
    default boolean isColliding(ZonedDateTime otherStartingAt, ZonedDateTime otherEndingAt) {

        ZonedDateTime currentStartingAt = this.getStartingAt();
        ZonedDateTime currentEndingAt   = this.getEndingAt();

        boolean isSameStart = currentStartingAt.isEqual(otherStartingAt);
        boolean isSameEnd   = currentEndingAt.isEqual(otherEndingAt);

        boolean isStartDuring = currentStartingAt.isAfter(otherStartingAt) &&
                currentEndingAt.isBefore(otherEndingAt);

        boolean isEndDuring = currentEndingAt.isAfter(otherStartingAt) &&
                currentEndingAt.isBefore(otherEndingAt);

        boolean isOnTop = currentStartingAt.isBefore(otherStartingAt) &&
                currentEndingAt.isAfter(otherEndingAt);

        return isSameStart || isSameEnd || isStartDuring || isEndDuring || isOnTop;
    }

    /**
     * Check if the current {@link AnimeNightMeta} collides with the provided {@link AnimeNightMeta}.
     *
     * @param meta
     *         The {@link AnimeNightMeta} which may be scheduled.
     *
     * @return True if it collides with the current {@link AnimeNightMeta}, false otherwise.
     *
     * @deprecated This is used in the now deprecated scheduler v1, {@link BroadcastScheduler}. Please use
     *         {@link AdvancedScheduler} now.
     */
    @Deprecated
    default boolean isColliding(AnimeNightMeta meta) {

        return this.isColliding(meta.getStartingAt(), meta.getEndingAt());
    }

}
