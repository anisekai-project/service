package me.anisekai.server.events.anime;

import me.anisekai.server.entities.Anime;
import me.anisekai.server.events.AnimeUpdatedEvent;

import java.util.List;

public class AnimeTagsUpdatedEvent extends AnimeUpdatedEvent<List<String>> {

    public AnimeTagsUpdatedEvent(Object source, Anime entity, List<String> previous, List<String> current) {

        super(source, entity, previous, current);
    }

}
