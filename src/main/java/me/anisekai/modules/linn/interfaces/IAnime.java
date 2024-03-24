package me.anisekai.modules.linn.interfaces;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.linn.events.anime.*;
import me.anisekai.modules.shizue.entities.Interest;

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
