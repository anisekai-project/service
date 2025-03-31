package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.server.entities.Anime;

public class AnimeUpdatedEvent<V> extends EntityUpdatedEvent<Anime, V> {

    public AnimeUpdatedEvent(Object source, Anime entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
