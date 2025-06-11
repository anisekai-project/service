package fr.anisekai.server.events;

import fr.anisekai.server.entities.Episode;

public class EpisodeUpdatedEvent<V> extends EntityUpdatedEventAdapter<Episode, V> {

    public EpisodeUpdatedEvent(Object source, Episode entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
