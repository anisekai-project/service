package me.anisekai.server.events.media;

import me.anisekai.server.entities.Episode;
import me.anisekai.server.entities.Media;
import me.anisekai.server.events.MediaUpdatedEvent;

public class MediaEpisodeUpdatedEvent extends MediaUpdatedEvent<Episode> {

    public MediaEpisodeUpdatedEvent(Object source, Media entity, Episode previous, Episode current) {

        super(source, entity, previous, current);
    }

}
