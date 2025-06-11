package me.anisekai.server.events;

import me.anisekai.server.entities.Broadcast;

/**
 * Event notifying when a {@link Broadcast} is being inserted in the database.
 */
public class BroadcastCreatedEvent extends EntityCreatedEventAdapter<Broadcast> {

    public BroadcastCreatedEvent(Object source, Broadcast entity) {

        super(source, entity);
    }

}
