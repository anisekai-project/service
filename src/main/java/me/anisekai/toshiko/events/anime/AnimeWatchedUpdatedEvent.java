package me.anisekai.toshiko.events.anime;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class AnimeWatchedUpdatedEvent extends EntityUpdatedEvent<Anime, Long> {

    public AnimeWatchedUpdatedEvent(Object source, Anime entity, Long previous, Long current) {

        super(source, entity, previous, current);
    }

}
