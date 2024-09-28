package me.anisekai.modules.shizue.interfaces;

import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.api.plannifier.interfaces.Plannifiable;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.events.broadcast.*;

import java.time.ZonedDateTime;

public interface AnimeNightMeta extends Plannifiable<Anime> {

    // <editor-fold desc="Trigger Overrides — @TriggerEvent applied to superclass(es)">

    @Override
    @TriggerEvent(BroadcastAmountUpdatedEvent.class)
    void setEpisodeCount(long episodeCount);

    @Override
    @TriggerEvent(BroadcastFirstEpisodeUpdatedEvent.class)
    void setFirstEpisode(long firstEpisode);

    @Override
    @TriggerEvent(BroadcastStartDateTimeUpdatedEvent.class)
    void setStartingAt(ZonedDateTime time);

    // </editor-fold>

}
