package me.anisekai.server.interfaces;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.api.plannifier.interfaces.entities.Plannifiable;
import me.anisekai.server.enums.BroadcastStatus;
import me.anisekai.server.events.broadcast.BroadcastEventUpdatedEvent;
import me.anisekai.server.events.broadcast.BroadcastStatusUpdatedEvent;

/**
 * Interface representing an object holding data about a broadcast.
 */
public interface IBroadcast<A extends IAnime<?>> extends IEntity<Long>, Plannifiable<A> {

    /**
     * Define this {@link IBroadcast}'s watch target.
     *
     * @param watchTarget
     *         A watch target.
     */
    void setWatchTarget(A watchTarget);

    /**
     * Retrieve the discord event id matching this {@link IBroadcast}.
     *
     * @return A discord event id.
     */
    Long getEventId();

    /**
     * Define the discord event id matching this {@link IBroadcast}.
     *
     * @param eventId
     *         A discord event id.
     */
    @TriggerEvent(BroadcastEventUpdatedEvent.class)
    void setEventId(Long eventId);

    /**
     * Retrieve this {@link IBroadcast} status.
     *
     * @return A {@link BroadcastStatus}
     */
    BroadcastStatus getStatus();

    /**
     * Define this {@link IBroadcast} status.
     *
     * @param status
     *         A {@link BroadcastStatus}
     */
    @TriggerEvent(BroadcastStatusUpdatedEvent.class)
    void setStatus(BroadcastStatus status);

    /**
     * Check if this {@link IBroadcast} should advance the progress on the {@link IAnime} associated once this
     * {@link IBroadcast} ends.
     *
     * @return True if the {@link IAnime} should progress at the end of this {@link IBroadcast}, false otherwise.
     */
    boolean shouldDoProgress();

    /**
     * Define if this {@link IBroadcast} should advance the progress on the {@link IAnime} associated once this
     * {@link IBroadcast} ends.
     *
     * @param shouldDoProgress
     *         True if the {@link IAnime} should progress at the end of this {@link IBroadcast}, false otherwise.
     */
    void setDoProgress(boolean shouldDoProgress);

}
