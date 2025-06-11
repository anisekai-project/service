package fr.anisekai.server.events.broadcast;

import fr.anisekai.server.entities.Broadcast;
import fr.anisekai.server.events.BroadcastUpdatedEvent;

public class BroadcastEpisodeCountUpdatedEvent extends BroadcastUpdatedEvent<Long> {

    public BroadcastEpisodeCountUpdatedEvent(Object source, Broadcast entity, Long oldValue, Long newValue) {

        super(source, entity, oldValue, newValue);
    }

}
