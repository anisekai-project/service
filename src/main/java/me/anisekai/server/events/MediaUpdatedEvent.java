package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.server.entities.Media;

public class MediaUpdatedEvent<V> extends EntityUpdatedEvent<Media, V> {

    public MediaUpdatedEvent(Object source, Media entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
