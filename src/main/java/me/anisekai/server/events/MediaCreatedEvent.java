package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.server.entities.Media;

/**
 * Event notifying when a {@link Media} is being inserted in the database.
 */
public class MediaCreatedEvent extends EntityCreatedEvent<Media> {

    public MediaCreatedEvent(Object source, Media entity) {

        super(source, entity);
    }

}
