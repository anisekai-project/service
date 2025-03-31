package me.anisekai.api.plannifier.interfaces;

import me.anisekai.api.plannifier.interfaces.entities.Plannifiable;
import me.anisekai.api.plannifier.interfaces.entities.WatchTarget;

import java.time.Duration;
import java.time.ZonedDateTime;

public interface ScheduleSpotData<T extends WatchTarget> {

    /**
     * Retrieve the entity {@link T} that will be watched during the {@link ScheduleSpotData}.
     *
     * @return The watch target
     */
    T getWatchTarget();

    /**
     * Retrieve the {@link ZonedDateTime} at which this {@link ScheduleSpotData} will take place.
     *
     * @return A {@link ZonedDateTime}
     */
    ZonedDateTime getStartingAt();

    /**
     * Define the {@link ZonedDateTime} at which this {@link ScheduleSpotData} will take place.
     *
     * @param time
     *         A {@link ZonedDateTime}
     */
    void setStartingAt(ZonedDateTime time);

    /**
     * Get the amount of episode that will be watched during this {@link ScheduleSpotData}. If not applicable, just
     * return 1.
     *
     * @return The amount of episode that will be watched.
     */
    long getEpisodeCount();

    /**
     * Set the amount of episode that will be watched during this {@link ScheduleSpotData}. If not applicable, just set
     * 1.
     *
     * @param episodeCount
     *         The amount of episode that will be watched.
     */
    void setEpisodeCount(long episodeCount);

    /**
     * Check if skips for opening and ending are enabled. Check {@link #setSkipEnabled(boolean)} for more details.
     *
     * @return True if superfluous opening and ending should be skipped, false otherwise.
     */
    boolean isSkipEnabled();

    /**
     * Enable skips for opening and ending. Applicable for anime mostly. In case {@link #getEpisodeCount()} is greater
     * than 1, each additional episode will remove 3 minutes of duration in the total.
     * <p>
     * <b>Details:</b>
     * As each opening and ending are usually 1m30, if more than 1 episode are watched, only the opening of the first
     * episode and the ending of the last episode will be watched, causing the skipping of each opening and ending in
     * between. This will remove 1m30 for each skipped opening and 1m30 for each skipped ending.
     *
     * @param skipEnabled
     *         True if superfluous opening and ending should be skipped, false otherwise.
     */
    void setSkipEnabled(boolean skipEnabled);

    /**
     * Delay the starting date of this {@link Plannifiable} by the provided {@link Duration}.
     *
     * @param modBy
     *         The {@link Duration} to delay
     */
    default void delayBy(Duration modBy) {

        this.setStartingAt(this.getStartingAt().plus(modBy));
    }

    /**
     * Retrieve the {@link ZonedDateTime} at which this {@link Plannifiable} will end. This is merely a shorthand for
     * {@link #getStartingAt()} + {@link #getDuration()}.
     *
     * @return A {@link ZonedDateTime}
     */
    default ZonedDateTime getEndingAt() {

        return this.getStartingAt().plus(this.getDuration());
    }

    /**
     * Retrieve the total {@link Duration} of this {@link Plannifiable}.
     *
     * @return A {@link Duration}
     */
    default Duration getDuration() {

        if (this.getEpisodeCount() == 1) return Duration.ofMinutes(this.getWatchTarget().getEpisodeDuration());

        long totalRuntime       = this.getWatchTarget().getEpisodeDuration() * this.getEpisodeCount();
        long superfluousRuntime = this.isSkipEnabled() ? (this.getEpisodeCount() - 1) * 3 : 0;

        return Duration.ofMinutes(totalRuntime - superfluousRuntime);
    }

}
