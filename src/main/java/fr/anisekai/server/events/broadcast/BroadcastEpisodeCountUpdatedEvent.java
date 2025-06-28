package fr.anisekai.server.events.broadcast;

import fr.anisekai.server.entities.Broadcast;
import fr.anisekai.server.events.BroadcastUpdatedEvent;

public class BroadcastEpisodeCountUpdatedEvent extends BroadcastUpdatedEvent<Integer> {

    public BroadcastEpisodeCountUpdatedEvent(Object source, Broadcast entity, Integer oldValue, Integer newValue) {

        super(source, entity, oldValue, newValue);
    }

}
