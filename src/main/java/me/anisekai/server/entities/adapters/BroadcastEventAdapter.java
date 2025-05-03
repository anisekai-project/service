package me.anisekai.server.entities.adapters;

import fr.anisekai.wireless.api.persistence.TriggerEvent;
import fr.anisekai.wireless.remote.enums.BroadcastStatus;
import fr.anisekai.wireless.remote.interfaces.BroadcastEntity;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.events.broadcast.BroadcastEpisodeCountUpdatedEvent;
import me.anisekai.server.events.broadcast.BroadcastFirstEpisodeUpdatedEvent;
import me.anisekai.server.events.broadcast.BroadcastStartingAtUpdatedEvent;
import me.anisekai.server.events.broadcast.BroadcastStatusUpdatedEvent;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;

public interface BroadcastEventAdapter extends BroadcastEntity<Anime> {

    @Override
    @TriggerEvent(BroadcastStatusUpdatedEvent.class)
    void setStatus(@NotNull BroadcastStatus status);

    @Override
    @TriggerEvent(BroadcastFirstEpisodeUpdatedEvent.class)
    void setFirstEpisode(long firstEpisode);

    @Override
    @TriggerEvent(BroadcastStartingAtUpdatedEvent.class)
    void setStartingAt(@NotNull ZonedDateTime time);

    @Override
    @TriggerEvent(BroadcastEpisodeCountUpdatedEvent.class)
    void setEpisodeCount(long episodeCount);

}
