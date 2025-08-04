package fr.anisekai.server.entities.adapters;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.events.broadcast.BroadcastEpisodeCountUpdatedEvent;
import fr.anisekai.server.events.broadcast.BroadcastFirstEpisodeUpdatedEvent;
import fr.anisekai.server.events.broadcast.BroadcastStartingAtUpdatedEvent;
import fr.anisekai.server.events.broadcast.BroadcastStatusUpdatedEvent;
import fr.anisekai.wireless.api.persistence.TriggerEvent;
import fr.anisekai.wireless.remote.enums.BroadcastStatus;
import fr.anisekai.wireless.remote.interfaces.BroadcastEntity;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;

public interface BroadcastEventAdapter extends BroadcastEntity<Anime> {

    @Override
    @TriggerEvent(BroadcastStatusUpdatedEvent.class)
    void setStatus(@NotNull BroadcastStatus status);

    @Override
    @TriggerEvent(BroadcastFirstEpisodeUpdatedEvent.class)
    void setFirstEpisode(int firstEpisode);

    @Override
    @TriggerEvent(BroadcastStartingAtUpdatedEvent.class)
    void setStartingAt(@NotNull ZonedDateTime time);

    @Override
    @TriggerEvent(BroadcastEpisodeCountUpdatedEvent.class)
    void setEpisodeCount(int episodeCount);

}
