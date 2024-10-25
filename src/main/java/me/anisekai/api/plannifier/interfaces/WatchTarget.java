package me.anisekai.api.plannifier.interfaces;

public interface WatchTarget {

    /**
     * Retrieve the number of episode watched.
     *
     * @return Number of episode watched.
     */
    long getEpisodeWatched();

    /**
     * Define the number of episode watched.
     *
     * @param episodeWatched
     *         Number of episode watched.
     */
    void setEpisodeWatched(long episodeWatched);

    /**
     * Retrieve the number of episode in total.
     *
     * @return Number of episode in total
     */
    long getEpisodeCount();

    /**
     * Define the number of episode in total.
     *
     * @param episodeCount
     *         Number of episode in total
     */
    void setEpisodeCount(long episodeCount);

    /**
     * Retrieve the duration of one episode.
     *
     * @return Duration of one episode.
     */
    long getEpisodeDuration();

    /**
     * Retrieve the duration of one episode.
     *
     * @param episodeDuration
     *         Duration of one episode.
     */
    void setEpisodeDuration(long episodeDuration);

}
