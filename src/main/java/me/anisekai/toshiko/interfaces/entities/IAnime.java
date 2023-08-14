package me.anisekai.toshiko.interfaces.entities;

import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.events.anime.*;
import me.anisekai.toshiko.interfaces.persistence.IEntity;
import me.anisekai.toshiko.helpers.proxy.TriggerEvent;

import java.util.Set;

public interface IAnime extends IEntity<Long> {

    String getName();

    @TriggerEvent(AnimeNameUpdatedEvent.class)
    void setName(String name);

    String getSynopsis();

    @TriggerEvent(AnimeSynopsisUpdatedEvent.class)
    void setSynopsis(String synopsis);

    String getGenres();

    @TriggerEvent(AnimeGenresUpdatedEvent.class)
    void setGenres(String genres);

    String getThemes();

    @TriggerEvent(AnimeThemesUpdatedEvent.class)
    void setThemes(String themes);

    AnimeStatus getStatus();

    @TriggerEvent(AnimeStatusUpdatedEvent.class)
    void setStatus(AnimeStatus status);

    Set<Interest> getInterests();

    void setInterests(Set<Interest> interests);

    DiscordUser getAddedBy();

    void setAddedBy(DiscordUser user);

    String getLink();

    void setLink(String link);

    String getImage();

    void setImage(String image);

    long getWatched();

    @TriggerEvent(AnimeWatchedUpdatedEvent.class)
    void setWatched(long watched);

    long getTotal();

    @TriggerEvent(AnimeTotalUpdatedEvent.class)
    void setTotal(long total);

    long getEpisodeDuration();

    @TriggerEvent(AnimeEpisodeDurationUpdatedEvent.class)
    void setEpisodeDuration(long episodeDuration);

    Long getAnnounceMessage();

    void setAnnounceMessage(Long announceMessage);

    String getRssMatch();

    void setRssMatch(String rssMatch);

    String getDiskPath();

    void setDiskPath(String diskPath);

}
