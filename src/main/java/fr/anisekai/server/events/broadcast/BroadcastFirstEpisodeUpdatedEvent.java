package fr.anisekai.server.events.broadcast;

import fr.anisekai.server.entities.Broadcast;
import fr.anisekai.server.events.BroadcastUpdatedEvent;

public class BroadcastFirstEpisodeUpdatedEvent extends BroadcastUpdatedEvent<Integer> {

    public BroadcastFirstEpisodeUpdatedEvent(Object source, Broadcast entity, Integer oldValue, Integer newValue) {

        super(source, entity, oldValue, newValue);
    }

}
