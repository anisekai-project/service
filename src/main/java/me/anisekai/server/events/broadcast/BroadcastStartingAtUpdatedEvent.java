package me.anisekai.server.events.broadcast;

import me.anisekai.server.entities.Broadcast;
import me.anisekai.server.events.BroadcastUpdatedEvent;

import java.time.ZonedDateTime;

public class BroadcastStartingAtUpdatedEvent extends BroadcastUpdatedEvent<ZonedDateTime> {

    public BroadcastStartingAtUpdatedEvent(Object source, Broadcast entity, ZonedDateTime oldValue, ZonedDateTime newValue) {

        super(source, entity, oldValue, newValue);
    }

}
