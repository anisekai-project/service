package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.server.entities.Anime;

/**
 * Event notifying when an {@link Anime} is being inserted in the database.
 */
public class AnimeCreatedEvent extends EntityCreatedEvent<Anime> {

    public AnimeCreatedEvent(Object source, Anime entity) {

        super(source, entity);
    }

}
