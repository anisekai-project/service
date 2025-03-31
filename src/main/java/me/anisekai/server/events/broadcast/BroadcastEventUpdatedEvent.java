package me.anisekai.server.events.broadcast;

import me.anisekai.server.entities.Broadcast;
import me.anisekai.server.events.BroadcastUpdatedEvent;

public class BroadcastEventUpdatedEvent extends BroadcastUpdatedEvent<Long> {

    public BroadcastEventUpdatedEvent(Object source, Broadcast entity, Long previous, Long current) {

        super(source, entity, previous, current);
    }

}
