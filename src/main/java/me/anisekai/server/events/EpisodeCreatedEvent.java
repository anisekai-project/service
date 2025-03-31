package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.server.entities.Episode;

/**
 * Event notifying when an {@link Episode} is being inserted in the database.
 */
public class EpisodeCreatedEvent extends EntityCreatedEvent<Episode> {

    public EpisodeCreatedEvent(Object source, Episode entity) {

        super(source, entity);
    }

}
