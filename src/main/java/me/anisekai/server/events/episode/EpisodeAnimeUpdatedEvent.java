package me.anisekai.server.events.episode;

import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.Episode;
import me.anisekai.server.events.EpisodeUpdatedEvent;

public class EpisodeAnimeUpdatedEvent extends EpisodeUpdatedEvent<Anime> {

    public EpisodeAnimeUpdatedEvent(Object source, Episode entity, Anime previous, Anime current) {

        super(source, entity, previous, current);
    }

}
