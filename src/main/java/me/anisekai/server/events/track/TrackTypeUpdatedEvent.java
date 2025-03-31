package me.anisekai.server.events.track;

import me.anisekai.server.entities.Track;
import me.anisekai.server.enums.TrackType;
import me.anisekai.server.events.TrackUpdatedEvent;

public class TrackTypeUpdatedEvent extends TrackUpdatedEvent<TrackType> {

    public TrackTypeUpdatedEvent(Object source, Track entity, TrackType previous, TrackType current) {

        super(source, entity, previous, current);
    }

}
