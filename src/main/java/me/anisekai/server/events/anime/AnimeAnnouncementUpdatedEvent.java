package me.anisekai.server.events.anime;

import me.anisekai.server.entities.Anime;
import me.anisekai.server.events.AnimeUpdatedEvent;

public class AnimeAnnouncementUpdatedEvent extends AnimeUpdatedEvent<Long> {

    public AnimeAnnouncementUpdatedEvent(Object source, Anime entity, Long previous, Long current) {

        super(source, entity, previous, current);
    }

}
