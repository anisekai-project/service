package me.anisekai.modules.linn.events.anime;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.linn.entities.Anime;

public class AnimeSynopsisUpdatedEvent extends EntityUpdatedEvent<Anime, String> {

    public AnimeSynopsisUpdatedEvent(Object source, Anime entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
