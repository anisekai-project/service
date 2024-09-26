package me.anisekai.api.plannifier.data;

import me.anisekai.api.plannifier.interfaces.WatchTarget;

import java.util.Objects;

public class TestWatchTarget implements WatchTarget {

    private final long id;
    private long episodeWatched;
    private long episodeCount;
    private long episodeDuration = TestData.EPISODE_DURATION_MINUTES;

    public TestWatchTarget(long id, long episodeWatched, long episodeCount) {

        this.id             = id;
        this.episodeWatched = episodeWatched;
        this.episodeCount   = episodeCount;
    }

    /**
     * Retrieve the number of episode watched.
     *
     * @return Number of episode watched.
     */
    @Override
    public long getEpisodeWatched() {

        return this.episodeWatched;
    }

    /**
     * Define the number of episode watched.
     *
     * @param episodeWatched
     *         Number of episode watched.
     */
    @Override
    public void setEpisodeWatched(long episodeWatched) {

        this.episodeWatched = episodeWatched;
    }

    /**
     * Retrieve the number of episode in total.
     *
     * @return Number of episode in total
     */
    @Override
    public long getEpisodeCount() {

        return this.episodeCount;
    }

    /**
     * Define the number of episode in total.
     *
     * @param episodeCount
     *         Number of episode in total
     */
    @Override
    public void setEpisodeCount(long episodeCount) {

        this.episodeCount = episodeCount;
    }

    /**
     * Retrieve the duration of one episode.
     *
     * @return Duration of one episode.
     */
    @Override
    public long getEpisodeDuration() {

        return this.episodeDuration;
    }

    /**
     * Retrieve the duration of one episode.
     *
     * @param episodeDuration
     *         Duration of one episode.
     */
    @Override
    public void setEpisodeDuration(long episodeDuration) {

        this.episodeDuration = episodeDuration;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        TestWatchTarget that = (TestWatchTarget) o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(this.id);
    }

    @Override
    public String toString() {

        return "TestWatchTarget{" +
                "id=" + this.id +
                ", episodeWatched=" + this.episodeWatched +
                ", episodeCount=" + this.episodeCount +
                ", episodeDuration=" + this.episodeDuration +
                '}';
    }

}
