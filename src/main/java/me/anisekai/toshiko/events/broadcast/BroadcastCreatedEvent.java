package me.anisekai.toshiko.events.broadcast;

import me.anisekai.toshiko.entities.Broadcast;
import me.anisekai.toshiko.events.EntityCreatedEvent;

public class BroadcastCreatedEvent extends EntityCreatedEvent<Broadcast> {

    public BroadcastCreatedEvent(Object source, Broadcast entity) {

        super(source, entity);
    }

}
