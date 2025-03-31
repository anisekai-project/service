package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.server.entities.Track;

public class TrackUpdatedEvent<V> extends EntityUpdatedEvent<Track, V> {

    public TrackUpdatedEvent(Object source, Track entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
