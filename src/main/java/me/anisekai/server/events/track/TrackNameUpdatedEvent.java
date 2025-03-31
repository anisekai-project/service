package me.anisekai.server.events.track;

import me.anisekai.server.entities.Track;
import me.anisekai.server.events.TrackUpdatedEvent;

public class TrackNameUpdatedEvent extends TrackUpdatedEvent<String> {

    public TrackNameUpdatedEvent(Object source, Track entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
