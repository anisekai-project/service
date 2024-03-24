package me.anisekai.modules.linn.events.anime;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.modules.linn.entities.Anime;

public class AnimeEpisodeDurationUpdatedEvent extends EntityUpdatedEvent<Anime, Long> {

    public AnimeEpisodeDurationUpdatedEvent(Object source, Anime entity, Long previous, Long current) {

        super(source, entity, previous, current);
    }

}
