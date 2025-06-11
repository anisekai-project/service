package me.anisekai.server.events;

import me.anisekai.server.entities.Interest;

/**
 * Event notifying when an {@link Interest} is being inserted in the database.
 */
public class InterestCreatedEvent extends EntityCreatedEventAdapter<Interest> {

    public InterestCreatedEvent(Object source, Interest entity) {

        super(source, entity);
    }

}
