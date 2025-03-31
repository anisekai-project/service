package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.server.entities.DiscordUser;

public class VoterUpdatedEvent<V> extends EntityUpdatedEvent<DiscordUser, V> {

    public VoterUpdatedEvent(Object source, DiscordUser entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
