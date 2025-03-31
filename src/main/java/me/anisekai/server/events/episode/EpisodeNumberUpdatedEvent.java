package me.anisekai.server.events.episode;

import me.anisekai.server.entities.Episode;
import me.anisekai.server.events.EpisodeUpdatedEvent;

public class EpisodeNumberUpdatedEvent extends EpisodeUpdatedEvent<Integer> {

    public EpisodeNumberUpdatedEvent(Object source, Episode entity, Integer previous, Integer current) {

        super(source, entity, previous, current);
    }

}
