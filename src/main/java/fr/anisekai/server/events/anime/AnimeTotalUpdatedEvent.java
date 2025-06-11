package fr.anisekai.server.events.anime;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.events.AnimeUpdatedEvent;

public class AnimeTotalUpdatedEvent extends AnimeUpdatedEvent<Long> {

    public AnimeTotalUpdatedEvent(Object source, Anime entity, Long previous, Long current) {

        super(source, entity, previous, current);
    }

}
