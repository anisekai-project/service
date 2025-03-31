package me.anisekai.server.interfaces;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.server.events.media.MediaEpisodeUpdatedEvent;
import me.anisekai.server.events.media.MediaMetaUpdatedEvent;
import me.anisekai.server.events.media.MediaNameUpdatedEvent;

/**
 * Interface representing an object holding data about a media file.
 */
public interface IMedia<O extends IEpisode<?>> extends IEntity<Long> {

    /**
     * Retrieve the {@link IEpisode} to which this {@link IMedia} belongs.
     *
     * @return An {@link IEpisode}.
     */
    O getEpisode();

    /**
     * Define the {@link IEpisode} to which this {@link IMedia} belongs.
     *
     * @param episode
     *         An {@link IEpisode}.
     */
    @TriggerEvent(MediaEpisodeUpdatedEvent.class)
    void setEpisode(O episode);

    /**
     * Retrieve this {@link IMedia}'s name.
     *
     * @return A name.
     */
    String getName();

    /**
     * Define this {@link IMedia}'s name.
     *
     * @param name
     *         A name.
     */
    @TriggerEvent(MediaNameUpdatedEvent.class)
    void setName(String name);

    /**
     * Retrieve this {@link IMedia}'s additional data, formatted as JSON.
     *
     * @return Additional data as JSON.
     */
    String getMeta();

    /**
     * Define this {@link IMedia}'s additional data, formatted as JSON.
     *
     * @param meta
     *         Additional data as JSON.
     */
    @TriggerEvent(MediaMetaUpdatedEvent.class)
    void setMeta(String meta);

}
