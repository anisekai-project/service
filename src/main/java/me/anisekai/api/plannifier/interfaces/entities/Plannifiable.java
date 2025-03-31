package me.anisekai.api.plannifier.interfaces.entities;

import me.anisekai.api.plannifier.interfaces.ScheduleSpotData;

public interface Plannifiable<T extends WatchTarget> extends ScheduleSpotData<T> {

    /**
     * Get the first episode number that will be watched in this {@link Plannifiable}.
     *
     * @return The first episode number.
     */
    long getFirstEpisode();

    /**
     * Set the first episode number that will be watched in this {@link Plannifiable}.
     *
     * @param firstEpisode
     *         The first episode number.
     */
    void setFirstEpisode(long firstEpisode);

    /**
     * Get the last episode number that will be watched in this {@link Plannifiable}. There is no setter counterpart as
     * this value is processed using {@link #getFirstEpisode()} and {@link #getEpisodeCount()}.
     *
     * @return The last episode number.
     */
    default long getLastEpisode() {

        return this.getFirstEpisode() + (this.getEpisodeCount() - 1);
    }

}
