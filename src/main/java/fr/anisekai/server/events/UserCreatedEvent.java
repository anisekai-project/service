package fr.anisekai.server.events;

import fr.anisekai.server.entities.DiscordUser;

/**
 * Event notifying when a {@link DiscordUser} is being inserted in the database.
 */
public class UserCreatedEvent extends EntityCreatedEventAdapter<DiscordUser> {

    public UserCreatedEvent(Object source, DiscordUser entity) {

        super(source, entity);
    }

}
