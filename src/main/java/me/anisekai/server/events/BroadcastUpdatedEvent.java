package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.server.entities.Broadcast;

public class BroadcastUpdatedEvent<V> extends EntityUpdatedEvent<Broadcast, V> {

    public BroadcastUpdatedEvent(Object source, Broadcast entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
