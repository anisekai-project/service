package me.anisekai.server.events.broadcast;

import me.anisekai.server.entities.Broadcast;
import me.anisekai.server.events.BroadcastUpdatedEvent;

public class BroadcastFirstEpisodeUpdatedEvent extends BroadcastUpdatedEvent<Long> {

    public BroadcastFirstEpisodeUpdatedEvent(Object source, Broadcast entity, Long oldValue, Long newValue) {

        super(source, entity, oldValue, newValue);
    }

}
