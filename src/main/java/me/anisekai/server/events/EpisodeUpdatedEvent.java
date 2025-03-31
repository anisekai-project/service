package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.server.entities.Episode;

public class EpisodeUpdatedEvent<V> extends EntityUpdatedEvent<Episode, V> {

    public EpisodeUpdatedEvent(Object source, Episode entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
