package fr.anisekai.server.events;

import fr.anisekai.server.entities.Episode;

/**
 * Event notifying when an {@link Episode} is being inserted in the database.
 */
public class EpisodeCreatedEvent extends EntityCreatedEventAdapter<Episode> {

    public EpisodeCreatedEvent(Object source, Episode entity) {

        super(source, entity);
    }

}
