package me.anisekai.server.interfaces;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.server.enums.TrackType;
import me.anisekai.server.events.track.TrackMediaUpdatedEvent;
import me.anisekai.server.events.track.TrackNameUpdatedEvent;
import me.anisekai.server.events.track.TrackTypeUpdatedEvent;

/**
 * Interface representing an object holding data about a track.
 */
public interface ITrack<O extends IMedia<?>> extends IEntity<Long> {

    /**
     * Retrieve the {@link IMedia} to which this {@link ITrack} belongs.
     *
     * @return A {@link IMedia}.
     */
    O getMedia();

    /**
     * Define the {@link IMedia} to which this {@link ITrack} belongs.
     *
     * @param media
     *         A {@link IMedia}.
     */
    @TriggerEvent(TrackMediaUpdatedEvent.class)
    void setMedia(O media);

    /**
     * Retrieve this {@link ITrack}'s name.
     *
     * @return A name
     */
    String getName();

    /**
     * Define this {@link ITrack}'s name.
     *
     * @param name
     *         A name
     */
    @TriggerEvent(TrackNameUpdatedEvent.class)
    void setName(String name);

    /**
     * Retrieve this {@link ITrack}'s type.
     *
     * @return A {@link TrackType}.
     */
    TrackType getType();

    /**
     * Define this {@link ITrack}'s type.
     *
     * @param type
     *         A {@link TrackType}.
     */
    @TriggerEvent(TrackTypeUpdatedEvent.class)
    void setType(TrackType type);

}
