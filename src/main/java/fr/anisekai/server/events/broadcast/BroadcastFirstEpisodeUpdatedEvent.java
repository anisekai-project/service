package fr.anisekai.server.events.broadcast;

import fr.anisekai.server.entities.Broadcast;
import fr.anisekai.server.events.BroadcastUpdatedEvent;

public class BroadcastFirstEpisodeUpdatedEvent extends BroadcastUpdatedEvent<Long> {

    public BroadcastFirstEpisodeUpdatedEvent(Object source, Broadcast entity, Long oldValue, Long newValue) {

        super(source, entity, oldValue, newValue);
    }

}
