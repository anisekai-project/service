package fr.anisekai.server.events;

import fr.anisekai.server.entities.Voter;

/**
 * Event notifying when a {@link Voter} is being inserted in the database.
 */
public class VoterCreatedEvent extends EntityCreatedEventAdapter<Voter> {

    public VoterCreatedEvent(Object source, Voter entity) {

        super(source, entity);
    }

}
