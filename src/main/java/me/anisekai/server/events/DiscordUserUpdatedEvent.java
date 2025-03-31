package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.server.entities.DiscordUser;

public class DiscordUserUpdatedEvent<V> extends EntityUpdatedEvent<DiscordUser, V> {

    public DiscordUserUpdatedEvent(Object source, DiscordUser entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
