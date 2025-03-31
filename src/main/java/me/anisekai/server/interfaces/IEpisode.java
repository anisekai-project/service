package me.anisekai.server.interfaces;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.events.episode.EpisodeAnimeUpdatedEvent;
import me.anisekai.server.events.episode.EpisodeNumberUpdatedEvent;

/**
 * Interface representing an object holding data about an episode.
 */
public interface IEpisode<O extends IAnime<?>> extends IEntity<Long> {

    /**
     * Retrieve the {@link IAnime} to which this {@link IEpisode} belongs.
     *
     * @return An {@link IAnime}.
     */
    O getAnime();

    /**
     * Define the {@link Anime} to which this {@link IEpisode} belongs.
     *
     * @param anime
     *         An {@link Anime}
     */
    @TriggerEvent(EpisodeAnimeUpdatedEvent.class)
    void setAnime(O anime);

    /**
     * Retrieve the {@link IEpisode} number, representing its watch order within its {@link IAnime}.
     *
     * @return An episode number
     */
    int getNumber();

    /**
     * Define the {@link IEpisode} number, representing its watch order within its {@link IAnime}.
     *
     * @param number
     *         An episode number
     */
    @TriggerEvent(EpisodeNumberUpdatedEvent.class)
    void setNumber(int number);

}
