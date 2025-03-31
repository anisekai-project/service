package me.anisekai.server.events.media;

import me.anisekai.server.entities.Media;
import me.anisekai.server.events.MediaUpdatedEvent;

public class MediaMetaUpdatedEvent extends MediaUpdatedEvent<String> {

    public MediaMetaUpdatedEvent(Object source, Media entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
