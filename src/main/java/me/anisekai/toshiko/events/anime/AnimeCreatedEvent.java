package me.anisekai.toshiko.events.anime;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.events.EntityCreatedEvent;

public class AnimeCreatedEvent extends EntityCreatedEvent<Anime> {

    public AnimeCreatedEvent(Object source, Anime entity) {

        super(source, entity);
    }

}