package me.anisekai.toshiko.events.anime;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.events.EntityUpdatedEvent;

public class AnimeStatusUpdatedEvent extends EntityUpdatedEvent<Anime, AnimeStatus> {

    public AnimeStatusUpdatedEvent(Object source, Anime entity, AnimeStatus previous, AnimeStatus current) {

        super(source, entity, previous, current);
    }

}
