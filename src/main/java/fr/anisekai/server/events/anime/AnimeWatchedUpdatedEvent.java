package fr.anisekai.server.events.anime;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.events.AnimeUpdatedEvent;

public class AnimeWatchedUpdatedEvent extends AnimeUpdatedEvent<Integer> {

    public AnimeWatchedUpdatedEvent(Object source, Anime entity, Integer previous, Integer current) {

        super(source, entity, previous, current);
    }

}
