package fr.anisekai.server.entities.adapters;

import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.events.anime.*;
import fr.anisekai.wireless.api.persistence.TriggerEvent;
import fr.anisekai.wireless.remote.enums.AnimeList;
import fr.anisekai.wireless.remote.interfaces.AnimeEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface AnimeEventAdapter extends AnimeEntity<DiscordUser> {

    @Override
    @TriggerEvent(AnimeTitleUpdatedEvent.class)
    void setTitle(@NotNull String title);

    @Override
    @TriggerEvent(AnimeListUpdatedEvent.class)
    void setList(@NotNull AnimeList list);

    @Override
    @TriggerEvent(AnimeSynopsisUpdatedEvent.class)
    void setSynopsis(@Nullable String synopsis);

    @Override
    @TriggerEvent(AnimeTagsUpdatedEvent.class)
    void setTags(@NotNull List<String> tags);

    @Override
    @TriggerEvent(AnimeThumbnailUpdatedEvent.class)
    void setThumbnailUrl(@Nullable String thumbnailUrl);

    @Override
    @TriggerEvent(AnimeUrlUpdatedEvent.class)
    void setUrl(@Nullable String url);

    @Override
    @TriggerEvent(AnimeWatchedUpdatedEvent.class)
    void setWatched(int watched);

    @Override
    @TriggerEvent(AnimeTotalUpdatedEvent.class)
    void setTotal(int total);

    @Override
    @TriggerEvent(AnimeEpisodeDurationUpdatedEvent.class)
    void setEpisodeDuration(int episodeDuration);

}
