package me.anisekai.server.events.track;

import me.anisekai.server.entities.Media;
import me.anisekai.server.entities.Track;
import me.anisekai.server.events.TrackUpdatedEvent;

public class TrackMediaUpdatedEvent extends TrackUpdatedEvent<Media> {

    public TrackMediaUpdatedEvent(Object source, Track entity, Media previous, Media current) {

        super(source, entity, previous, current);
    }

}
