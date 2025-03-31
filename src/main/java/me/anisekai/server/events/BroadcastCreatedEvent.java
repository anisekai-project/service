package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.server.entities.Broadcast;

/**
 * Event notifying when a {@link Broadcast} is being inserted in the database.
 */
public class BroadcastCreatedEvent extends EntityCreatedEvent<Broadcast> {

    public BroadcastCreatedEvent(Object source, Broadcast entity) {

        super(source, entity);
    }

}
