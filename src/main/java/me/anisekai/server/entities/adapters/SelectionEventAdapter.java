package me.anisekai.server.entities.adapters;

import fr.anisekai.wireless.api.persistence.TriggerEvent;
import fr.anisekai.wireless.remote.enums.SelectionStatus;
import fr.anisekai.wireless.remote.interfaces.SelectionEntity;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.events.selection.SelectionAnimesUpdatedEvent;
import me.anisekai.server.events.selection.SelectionStatusUpdatedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface SelectionEventAdapter extends SelectionEntity<Anime> {

    @Override
    @TriggerEvent(SelectionStatusUpdatedEvent.class)
    void setStatus(@NotNull SelectionStatus status);

    @Override
    @TriggerEvent(SelectionAnimesUpdatedEvent.class)
    void setAnimes(@NotNull Set<Anime> animes);

}
