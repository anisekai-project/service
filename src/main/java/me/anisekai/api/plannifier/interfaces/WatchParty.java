package me.anisekai.api.plannifier.interfaces;

import java.time.Duration;

/**
 * A {@link Plannifiable} which can be used to schedule an event to watch something, most of the time series.
 *
 * @param <W>
 *         Type of the watch target
 */
public interface WatchParty<W> extends Plannifiable {

    /**
     * Retrieve the entity {@link W} that will be watched during the {@link WatchParty}.
     *
     * @return The watch target
     */
    W getWatchTarget();

    /**
     * Define the entity {@link W} that will be watched during the {@link WatchParty}.
     *
     * @param watchTarget
     *         The watch target.
     */
    void setWatchTarget(W watchTarget);

    /**
     * Get the amount of episode that will be watched during this {@link WatchParty}. If not applicable, just return 1.
     *
     * @return The amount of episode that will be watched.
     */
    long getEpisodeCount();

    /**
     * Set the amount of episode that will be watched during this {@link WatchParty}. If not applicable, just set 1.
     *
     * @param episodeCount
     *         The amount of episode that will be watched.
     */
    void setEpisodeCount(long episodeCount);

    /**
     * Get the duration of an episode for the {@link W}. If not applicable, just return the {@link Plannifiable}
     * duration.
     *
     * @return The duration for one episode.
     */
    long getEpisodeDuration();

    /**
     * Set the duration of an episode for the {@link W}. If not applicable, just return the {@link Plannifiable}
     * duration.
     *
     * @param episodeDuration
     *         The duration for one episode.
     */
    void getEpisodeDuration(long episodeDuration);

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
     * Retrieve the total {@link Duration} of this {@link Plannifiable}.
     *
     * @return A {@link Duration}
     */
    @Override
    default Duration getDuration() {

        if (this.getEpisodeCount() == 1) return Duration.ofSeconds(this.getEpisodeDuration());

        long totalRuntime       = this.getEpisodeDuration() * this.getEpisodeCount();
        long superfluousRuntime = this.isSkipEnabled() ? (this.getEpisodeCount() - 1) * 180 : 0;

        return Duration.ofSeconds(totalRuntime - superfluousRuntime);
    }

}
