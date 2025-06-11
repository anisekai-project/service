package fr.anisekai.server.events;

import fr.anisekai.server.entities.Track;

public class TrackUpdatedEvent<V> extends EntityUpdatedEventAdapter<Track, V> {

    public TrackUpdatedEvent(Object source, Track entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
