package fr.anisekai.server.events;

import fr.anisekai.server.entities.Broadcast;

/**
 * Event notifying when a {@link Broadcast} is being inserted in the database.
 */
public class BroadcastCreatedEvent extends EntityCreatedEventAdapter<Broadcast> {

    public BroadcastCreatedEvent(Object source, Broadcast entity) {

        super(source, entity);
    }

}
