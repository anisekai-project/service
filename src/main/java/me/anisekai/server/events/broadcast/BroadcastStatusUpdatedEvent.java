package me.anisekai.server.events.broadcast;

import me.anisekai.server.entities.Broadcast;
import me.anisekai.server.enums.BroadcastStatus;
import me.anisekai.server.events.BroadcastUpdatedEvent;

public class BroadcastStatusUpdatedEvent extends BroadcastUpdatedEvent<BroadcastStatus> {

    public BroadcastStatusUpdatedEvent(Object source, Broadcast entity, BroadcastStatus previous, BroadcastStatus current) {

        super(source, entity, previous, current);
    }

}
