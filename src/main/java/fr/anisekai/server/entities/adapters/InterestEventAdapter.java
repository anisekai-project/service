package fr.anisekai.server.entities.adapters;

import fr.anisekai.wireless.api.persistence.TriggerEvent;
import fr.anisekai.wireless.remote.interfaces.InterestEntity;
import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.events.interest.InterestLevelUpdatedEvent;

public interface InterestEventAdapter extends InterestEntity<Anime, DiscordUser> {

    @Override
    @TriggerEvent(InterestLevelUpdatedEvent.class)
    void setLevel(byte level);

}
