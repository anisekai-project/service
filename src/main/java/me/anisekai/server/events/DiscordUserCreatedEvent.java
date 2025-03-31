package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityCreatedEvent;
import me.anisekai.server.entities.DiscordUser;

/**
 * Event notifying when a {@link DiscordUser} is being inserted in the database.
 */
public class DiscordUserCreatedEvent extends EntityCreatedEvent<DiscordUser> {

    public DiscordUserCreatedEvent(Object source, DiscordUser entity) {

        super(source, entity);
    }

}
