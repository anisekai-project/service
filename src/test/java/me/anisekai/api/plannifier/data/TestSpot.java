package me.anisekai.api.plannifier.data;

import me.anisekai.api.plannifier.interfaces.ScheduleSpotData;

import java.time.ZonedDateTime;

public class TestSpot implements ScheduleSpotData<TestWatchTarget> {

    private final TestWatchTarget target;
    private final ZonedDateTime   startingAt;
    private final long            episodeCount;
    private final boolean         skipEnabled;

    public TestSpot(TestWatchTarget target, ZonedDateTime startingAt, long episodeCount) {

        this.target       = target;
        this.startingAt   = startingAt;
        this.episodeCount = episodeCount;
        this.skipEnabled  = true;
    }

    /**
     * Retrieve the entity {@link T} that will be watched during the {@link WatchParty}.
     *
     * @return The watch target
     */
    @Override
    public TestWatchTarget getWatchTarget() {

        return this.target;
    }

    /**
     * Retrieve the {@link ZonedDateTime} at which this {@link Plannifiable} will take place.
     *
     * @return A {@link ZonedDateTime}
     */
    @Override
    public ZonedDateTime getStartingAt() {

        return this.startingAt;
    }

    /**
     * Define the {@link ZonedDateTime} at which this {@link Plannifiable} will take place.
     *
     * @param time
     *         A {@link ZonedDateTime}
     */
    @Override
    public void setStartingAt(ZonedDateTime time) {

    }

    /**
     * Get the amount of episode that will be watched during this {@link WatchParty}. If not applicable, just return 1.
     *
     * @return The amount of episode that will be watched.
     */
    @Override
    public long getEpisodeCount() {

        return this.episodeCount;
    }

    /**
     * Set the amount of episode that will be watched during this {@link WatchParty}. If not applicable, just set 1.
     *
     * @param episodeCount
     *         The amount of episode that will be watched.
     */
    @Override
    public void setEpisodeCount(long episodeCount) {

    }

    /**
     * Check if skips for opening and ending are enabled. Check {@link #setSkipEnabled(boolean)} for more details.
     *
     * @return True if superfluous opening and ending should be skipped, false otherwise.
     */
    @Override
    public boolean isSkipEnabled() {

        return this.skipEnabled;
    }

    /**
     * Enable skips for opening and ending. Applicable for anime mostly. In case {@link #getEpisodeCount()} is greater than
     * 1, each additional episode will remove 3 minutes of duration in the total.
     * <p>
     * <b>Details:</b>
     * As each opening and ending are usually 1m30, if more than 1 episode are watched, only the opening of the first
     * episode and the ending of the last episode will be watched, causing the skipping of each opening and ending in
     * between. This will remove 1m30 for each skipped opening and 1m30 for each skipped ending.
     *
     * @param skipEnabled
     *         True if superfluous opening and ending should be skipped, false otherwise.
     */
    @Override
    public void setSkipEnabled(boolean skipEnabled) {

    }

}
