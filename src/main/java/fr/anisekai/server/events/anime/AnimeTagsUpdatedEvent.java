package fr.anisekai.server.events.anime;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.events.AnimeUpdatedEvent;

import java.util.List;

public class AnimeTagsUpdatedEvent extends AnimeUpdatedEvent<List<String>> {

    public AnimeTagsUpdatedEvent(Object source, Anime entity, List<String> previous, List<String> current) {

        super(source, entity, previous, current);
    }

}
