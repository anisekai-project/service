package me.anisekai.server.interfaces;


import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.api.transmission.TorrentStatus;
import me.anisekai.server.events.torrent.*;

/**
 * Interface representing an object holding data about a torrent.
 */
public interface ITorrent<T extends IEpisode<?>> extends IEntity<String> {

    /**
     * Retrieve this {@link ITorrent}'s name, identical from the auto-download source.
     *
     * @return A name.
     */
    String getName();

    /**
     * Define this {@link ITorrent}'s name, identical from the auto-download source.
     *
     * @param name
     *         A name.
     */
    @TriggerEvent(TorrentNameUpdatedEvent.class)
    void setName(String name);

    /**
     * Retrieve the {@link IEpisode} to which this {@link ITorrent} belongs.
     *
     * @return An {@link IEpisode}.
     */
    T getEpisode();

    /**
     * Define the {@link IEpisode} to which this {@link ITorrent} belongs.
     *
     * @param episode
     *         An {@link IEpisode}.
     */
    @TriggerEvent(TorrentEpisodeUpdatedEvent.class)
    void setEpisode(T episode);

    /**
     * Retrieve this {@link ITorrent}'s status. This status is entirely controlled by the external download service.
     *
     * @return A {@link TorrentStatus}.
     */
    TorrentStatus getStatus();

    /**
     * Define this {@link ITorrent}'s status. This status is entirely controlled by the external download service.
     *
     * @param status
     *         A {@link TorrentStatus}.
     */
    @TriggerEvent(TorrentStatusUpdatedEvent.class)
    void setStatus(TorrentStatus status);

    /**
     * Retrieve this {@link ITorrent}'s download progression between 0 and 1 where 1 means the download is completed.
     * This value is entirely controlled by the external download service.
     *
     * @return The download progress.
     */
    double getProgress();

    /**
     * Define this {@link ITorrent}'s download progression between 0 and 1 where 1 means the download is completed. This
     * value is entirely controlled by the external download service.
     *
     * @param progress
     *         The progress.
     */
    @TriggerEvent(TorrentProgressUpdatedEvent.class)
    void setProgress(double progress);

    /**
     * Retrieve this {@link ITorrent}'s source link.
     *
     * @return A link.
     */
    String getLink();

    /**
     * Define this {@link ITorrent}'s source link.
     *
     * @param link
     *         A link.
     */
    @TriggerEvent(TorrentLinkUpdatedEvent.class)
    void setLink(String link);

    /**
     * Retrieve this {@link ITorrent}'s download directory.
     *
     * @return A download directory path.
     */
    String getDownloadDirectory();

    /**
     * Define this {@link ITorrent}'s download directory.
     *
     * @param downloadDirectory
     *         A download directory path.
     */
    @TriggerEvent(TorrentDownloadDirectoryUpdatedEvent.class)
    void setDownloadDirectory(String downloadDirectory);

    /**
     * Retrieve this {@link ITorrent}'s file name.
     *
     * @return A file name.
     */
    String getFileName();

    /**
     * Define this {@link ITorrent}'s file name.
     *
     * @param fileName
     *         A file name.
     */
    @TriggerEvent(TorrentFilenameUpdatedEvent.class)
    void setFileName(String fileName);

    long getPriority();

    void setPriority(long priority);

}
