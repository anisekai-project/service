package fr.anisekai.server.events.broadcast;

import fr.anisekai.server.entities.Broadcast;
import fr.anisekai.server.events.BroadcastUpdatedEvent;

import java.time.ZonedDateTime;

public class BroadcastStartingAtUpdatedEvent extends BroadcastUpdatedEvent<ZonedDateTime> {

    public BroadcastStartingAtUpdatedEvent(Object source, Broadcast entity, ZonedDateTime oldValue, ZonedDateTime newValue) {

        super(source, entity, oldValue, newValue);
    }

}
