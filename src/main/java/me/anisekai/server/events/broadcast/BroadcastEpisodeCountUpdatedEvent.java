package me.anisekai.server.events.broadcast;

import me.anisekai.server.entities.Broadcast;
import me.anisekai.server.events.BroadcastUpdatedEvent;

public class BroadcastEpisodeCountUpdatedEvent extends BroadcastUpdatedEvent<Long> {

    public BroadcastEpisodeCountUpdatedEvent(Object source, Broadcast entity, Long oldValue, Long newValue) {

        super(source, entity, oldValue, newValue);
    }

}
