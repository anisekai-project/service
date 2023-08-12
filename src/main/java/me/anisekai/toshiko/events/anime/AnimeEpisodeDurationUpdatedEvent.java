package me.anisekai.toshiko.events.anime;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class AnimeEpisodeDurationUpdatedEvent extends EntityUpdatedEvent<Anime, Long> {

    public AnimeEpisodeDurationUpdatedEvent(Object source, Anime entity, Long previous, Long current) {

        super(source, entity, previous, current);
    }

}
