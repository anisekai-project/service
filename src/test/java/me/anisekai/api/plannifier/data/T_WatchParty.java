package me.anisekai.api.plannifier.data;

import me.anisekai.api.plannifier.interfaces.Plannifiable;
import me.anisekai.api.plannifier.interfaces.WatchParty;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;

import static me.anisekai.api.plannifier.PlannifierTestData.DURATION;

public class T_WatchParty implements WatchParty<Long> {

    private final Long          id;
    private       ZonedDateTime startingAt;
    private       Long          watchTarget;
    private       Long          episodeCount;
    private       Long          episodeDuration = DURATION.getSeconds();
    private       boolean       testTagDeleted  = false;

    public T_WatchParty(Long id, ZonedDateTime startingAt, Long watchTarget, Long episodeCount) {

        this.id           = id;
        this.startingAt   = startingAt;
        this.watchTarget  = watchTarget;
        this.episodeCount = episodeCount;
    }

    /**
     * Retrieve the entity {@link W} that will be watched during the {@link WatchParty}.
     *
     * @return The watch target
     */
    @Override
    public Long getWatchTarget() {

        return this.watchTarget;
    }

    /**
     * Define the entity {@link W} that will be watched during the {@link WatchParty}.
     *
     * @param watchTarget
     *         The watch target.
     */
    @Override
    public void setWatchTarget(Long watchTarget) {

        this.watchTarget = watchTarget;
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

        this.episodeCount = episodeCount;
    }

    /**
     * Get the duration of an episode for the {@link W}. If not applicable, just return the {@link Plannifiable}
     * duration.
     *
     * @return The duration for one episode.
     */
    @Override
    public long getEpisodeDuration() {

        return this.episodeDuration;
    }

    /**
     * Set the duration of an episode for the {@link W}. If not applicable, just return the {@link Plannifiable}
     * duration.
     *
     * @param episodeDuration
     *         The duration for one episode.
     */
    @Override
    public void getEpisodeDuration(long episodeDuration) {

        this.episodeDuration = episodeDuration;
    }

    /**
     * Check if skips for opening and ending are enabled. Check {@link #setSkipEnabled(boolean)} for more details.
     *
     * @return True if superfluous opening and ending should be skipped, false otherwise.
     */
    @Override
    public boolean isSkipEnabled() {

        return true;
    }

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
    @Override
    public void setSkipEnabled(boolean skipEnabled) {

        // NO-OP
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
     * Set the total {@link Duration} of this {@link Plannifiable}.
     *
     * @param duration
     *         A {@link Duration}
     */
    @Override
    public void setDuration(Duration duration) {

        // NO-OP (managed by episode count)
    }

    public boolean isTestTagDeleted() {

        return this.testTagDeleted;
    }

    public void setTestTagDeleted(boolean testTagDeleted) {

        this.testTagDeleted = testTagDeleted;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        T_WatchParty item = (T_WatchParty) o;
        return Objects.equals(this.id, item.id);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(this.id);
    }

}
