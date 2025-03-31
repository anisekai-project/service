package me.anisekai.server.events.media;

import me.anisekai.server.entities.Media;
import me.anisekai.server.events.MediaUpdatedEvent;

public class MediaNameUpdatedEvent extends MediaUpdatedEvent<String> {

    public MediaNameUpdatedEvent(Object source, Media entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
