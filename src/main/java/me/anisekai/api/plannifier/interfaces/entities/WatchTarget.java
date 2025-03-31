package me.anisekai.api.plannifier.interfaces.entities;

public interface WatchTarget {

    /**
     * Retrieve the number of episode watched for this {@link WatchTarget}.
     *
     * @return Number of episode watched.
     */
    long getWatched();

    /**
     * Define the number of episode watched for this {@link WatchTarget}.
     *
     * @param watched
     *         Number of episode watched.
     */
    void setWatched(long watched);

    /**
     * Retrieve the total amount of episode for this {@link WatchTarget}. Negative values will be used for a temporary
     * estimation of the total amount of episode.
     *
     * @return Number of episode in total
     */
    long getTotal();

    /**
     * Define the number of episode in total.
     *
     * @param total
     *         Number of episode in total
     */
    void setTotal(long total);

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
