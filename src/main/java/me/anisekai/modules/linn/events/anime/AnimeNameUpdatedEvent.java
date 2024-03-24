package me.anisekai.modules.linn.events.anime;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.linn.entities.Anime;

public class AnimeNameUpdatedEvent extends EntityUpdatedEvent<Anime, String> {

    public AnimeNameUpdatedEvent(Object source, Anime entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
