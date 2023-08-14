package me.anisekai.toshiko.events.anime;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class AnimeNameUpdatedEvent extends EntityUpdatedEvent<Anime, String> {

    public AnimeNameUpdatedEvent(Object source, Anime entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
