package fr.anisekai.server.events;

import fr.anisekai.server.entities.Interest;

/**
 * Event notifying when an {@link Interest} is being inserted in the database.
 */
public class InterestCreatedEvent extends EntityCreatedEventAdapter<Interest> {

    public InterestCreatedEvent(Object source, Interest entity) {

        super(source, entity);
    }

}
