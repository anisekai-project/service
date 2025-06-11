package fr.anisekai.server.events;

import fr.anisekai.server.entities.Track;

/**
 * Event notifying when a {@link Track} is being inserted in the database.
 */
public class TrackCreatedEvent extends EntityCreatedEventAdapter<Track> {

    public TrackCreatedEvent(Object source, Track entity) {

        super(source, entity);
    }

}
