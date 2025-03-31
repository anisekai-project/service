package me.anisekai.server.events.anime;

import me.anisekai.server.entities.Anime;
import me.anisekai.server.enums.AnimeStatus;
import me.anisekai.server.events.AnimeUpdatedEvent;

public class AnimeWatchlistUpdatedEvent extends AnimeUpdatedEvent<AnimeStatus> {

    public AnimeWatchlistUpdatedEvent(Object source, Anime entity, AnimeStatus previous, AnimeStatus current) {

        super(source, entity, previous, current);
    }

}
