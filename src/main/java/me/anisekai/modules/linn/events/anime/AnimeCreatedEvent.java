package me.anisekai.modules.linn.events.anime;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.modules.linn.entities.Anime;

public class AnimeCreatedEvent extends EntityCreatedEvent<Anime> {

    public AnimeCreatedEvent(Object source, Anime entity) {

        super(source, entity);
    }

}