package me.anisekai.server.events.media;

import me.anisekai.server.entities.Media;
import me.anisekai.server.events.MediaUpdatedEvent;

public class MediaDurationUpdatedEvent extends MediaUpdatedEvent<Long> {

    public MediaDurationUpdatedEvent(Object source, Media entity, Long previous, Long current) {

        super(source, entity, previous, current);
    }

}
