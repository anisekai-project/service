package fr.anisekai.server.events;

import fr.anisekai.server.entities.Anime;

/**
 * Event notifying when an {@link Anime} is being inserted in the database.
 */
public class AnimeCreatedEvent extends EntityCreatedEventAdapter<Anime> {

    public AnimeCreatedEvent(Object source, Anime entity) {

        super(source, entity);
    }

}
