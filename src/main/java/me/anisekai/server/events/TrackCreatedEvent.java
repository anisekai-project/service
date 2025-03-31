package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.server.entities.Track;

/**
 * Event notifying when a {@link Track} is being inserted in the database.
 */
public class TrackCreatedEvent extends EntityCreatedEvent<Track> {

    public TrackCreatedEvent(Object source, Track entity) {

        super(source, entity);
    }

}
