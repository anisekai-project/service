package me.anisekai.api.plannifier.data;

import me.anisekai.api.plannifier.interfaces.Plannifiable;

import java.time.ZonedDateTime;
import java.util.Objects;

public class TestWatchParty implements Plannifiable<TestWatchTarget> {

    private final long            id;
    private final TestWatchTarget target;
    private       long            firstEpisode;
    private       ZonedDateTime   startingAt;
    private       long            episodeCount;
    private       boolean         skipEnabled;

    private boolean deleted = false;

    public TestWatchParty(long id, TestWatchTarget target, long firstEpisode, ZonedDateTime startingAt, long episodeCount, boolean skipEnabled) {

        this.id           = id;
        this.target       = target;
        this.firstEpisode = firstEpisode;
        this.startingAt   = startingAt;
        this.episodeCount = episodeCount;
        this.skipEnabled  = skipEnabled;
    }

    /**
     * Get the first episode number that will be watched in this {@link Plannifiable}.
     *
     * @return The first episode number.
     */
    @Override
    public long getFirstEpisode() {

        return this.firstEpisode;
    }

    /**
     * Set the first episode number that will be watched in this {@link Plannifiable}.
     *
     * @param firstEpisode
     *         The first episode number.
     */
    @Override
    public void setFirstEpisode(long firstEpisode) {

        this.firstEpisode = firstEpisode;
    }

    /**
     * Retrieve the entity that will be watched during the {@link Plannifiable}.
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

        this.startingAt = time;
    }

    /**
     * Get the amount of episode that will be watched during this {@link Plannifiable}. If not applicable, just return 1.
     *
     * @return The amount of episode that will be watched.
     */
    @Override
    public long getEpisodeCount() {

        return this.episodeCount;
    }

    /**
     * Set the amount of episode that will be watched during this {@link Plannifiable}. If not applicable, just set 1.
     *
     * @param episodeCount
     *         The amount of episode that will be watched.
     */
    @Override
    public void setEpisodeCount(long episodeCount) {

        this.episodeCount = episodeCount;
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

        this.skipEnabled = skipEnabled;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        TestWatchParty that = (TestWatchParty) o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(this.id);
    }

    @Override
    public String toString() {

        return "TestWatchParty{" +
                "id=" + this.id +
                ", target=" + this.target +
                ", firstEpisode=" + this.firstEpisode +
                ", startingAt=" + this.startingAt +
                ", episodeCount=" + this.episodeCount +
                ", skipEnabled=" + this.skipEnabled +
                '}';
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public void tagDeleted() {
        this.deleted = true;
    }
}
