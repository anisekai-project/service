package me.anisekai.server.interfaces;

import jakarta.annotation.Nullable;
import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.api.plannifier.interfaces.entities.WatchTarget;
import me.anisekai.server.enums.AnimeStatus;
import me.anisekai.server.events.anime.*;
import org.jetbrains.annotations.NotNull;

/**
 * Interface representing an object holding data about an anime.
 */
public interface IAnime<O extends IDiscordUser> extends IEntity<Long>, WatchTarget {

    /**
     * Retrieve this {@link IAnime}'s title.
     *
     * @return The {@link IAnime}'s title.
     */
    String getTitle();

    /**
     * Define this {@link IAnime}'s title.
     *
     * @param title
     *         The {@link IAnime}'s title.
     */
    @TriggerEvent(AnimeTitleUpdatedEvent.class)
    void setTitle(String title);

    /**
     * Retrieve the {@link AnimeStatus} to which this {@link IAnime} belongs.
     *
     * @return A {@link AnimeStatus}
     */
    @NotNull
    AnimeStatus getWatchlist();

    /**
     * Define the {@link AnimeStatus} to which this {@link IAnime} belongs.
     *
     * @param animeStatus
     *         A {@link AnimeStatus}
     */
    @TriggerEvent(AnimeWatchlistUpdatedEvent.class)
    void setWatchlist(@NotNull AnimeStatus animeStatus);

    /**
     * Retrieve this {@link IAnime}'s synopsis.
     *
     * @return The synopsis
     */
    String getSynopsis();

    /**
     * Define this {@link IAnime}'s synopsis
     *
     * @param synopsis
     *         The synopsis
     */
    @TriggerEvent(AnimeSynopsisUpdatedEvent.class)
    void setSynopsis(String synopsis);

    /**
     * Retrieve a comma-separated list of tags, which represents the genre and theme of this {@link IAnime}.
     *
     * @return A group of tags
     */
    String getTags();

    /**
     * Define a comma-separated list of tags, which represents the genre and theme of this {@link IAnime}.
     *
     * @param tags
     *         A group of tags
     */
    @TriggerEvent(AnimeTagsUpdatedEvent.class)
    void setTags(String tags);

    /**
     * Retrieve a link pointing to the thumbnail image of this {@link IAnime}.
     *
     * @return A link
     */
    String getThumbnail();

    /**
     * Define a link pointing to the thumbnail image of this {@link IAnime}.
     *
     * @param thumbnail
     *         A link
     */
    @TriggerEvent(AnimeThumbnailUpdatedEvent.class)
    void setThumbnail(String thumbnail);

    /**
     * Retrieve a link pointing to the Nautiljon page of this {@link IAnime}.
     *
     * @return A link
     */
    String getNautiljonUrl();

    /**
     * Define a link pointing to the Nautiljon page of this {@link IAnime}.
     *
     * @param nautiljonUrl
     *         A link
     */
    @TriggerEvent(AnimeNautiljonUrlUpdatedEvent.class)
    void setNautiljonUrl(String nautiljonUrl);

    /**
     * Retrieve the regex that can be used to match this {@link IAnime}'s title on the remote source for
     * auto-downloading. The regex <b>must</b> define a capture group named `episode` to know which {@link IEpisode} is
     * matching the title.
     *
     * @return A regex
     */
    String getTitleRegex();

    /**
     * Define the regex that can be used to match this {@link IAnime}'s title on the remote source for auto-downloading.
     * The regex <b>must</b> define a capture group named `episode` to know which {@link IEpisode} is matching the
     * title.
     *
     * @param titleRegex
     *         A regex
     */
    @TriggerEvent(AnimeTitleRegexUpdatedEvent.class)
    void setTitleRegex(@Nullable String titleRegex);

    /**
     * Retrieve which {@link IDiscordUser} added this {@link IAnime} to the database.
     *
     * @return A {@link IDiscordUser}
     */
    O getAddedBy();

    /**
     * Define which {@link IDiscordUser} added this {@link IAnime} to the database.
     *
     * @param addedBy
     *         A {@link IDiscordUser}
     */
    @TriggerEvent(AnimeAddedByUpdatedEvent.class)
    void setAddedBy(O addedBy);

    /**
     * Retrieve this {@link IAnime}'s anilist ID.
     *
     * @return An anilist id.
     */
    Long getAnilistId();

    /**
     * Retrieve this {@link IAnime}'s anilist ID.
     *
     * @param anilistId
     *         An anilist id.
     */
    @TriggerEvent(AnimeAnilistUpdatedEvent.class)
    void setAnilistId(Long anilistId);

    /**
     * Retrieve the discord message id representing this {@link IAnime}'s announce message.
     *
     * @return A discord message id.
     */
    Long getAnnouncementId();

    /**
     * Define the discord message id representing this {@link IAnime}'s announce message.
     *
     * @param announcementId
     *         A discord message id.
     */
    @TriggerEvent(AnimeAnnouncementUpdatedEvent.class)
    void setAnnouncementId(Long announcementId);

    // -- Overrides

    /**
     * Define the number of episode watched for this {@link WatchTarget}.
     *
     * @param watched
     *         Number of episode watched.
     */
    @Override
    @TriggerEvent(AnimeWatchlistUpdatedEvent.class)
    void setWatched(long watched);

    /**
     * Define the number of episode in total.
     *
     * @param total
     *         Number of episode in total
     */
    @Override
    @TriggerEvent(AnimeTotalUpdatedEvent.class)
    void setTotal(long total);

    /**
     * Retrieve the duration of one episode.
     *
     * @param episodeDuration
     *         Duration of one episode.
     */
    @Override
    @TriggerEvent(AnimeDurationUpdatedEvent.class)
    void setEpisodeDuration(long episodeDuration);

}
