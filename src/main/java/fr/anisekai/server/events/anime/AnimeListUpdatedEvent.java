package fr.anisekai.server.events.anime;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.events.AnimeUpdatedEvent;
import fr.anisekai.wireless.remote.enums.AnimeList;

public class AnimeListUpdatedEvent extends AnimeUpdatedEvent<AnimeList> {

    public AnimeListUpdatedEvent(Object source, Anime entity, AnimeList previous, AnimeList current) {

        super(source, entity, previous, current);
    }

}
